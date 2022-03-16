package co.lucz.binancetraderbot.services;

import co.lucz.binancetraderbot.binance.BinanceClient;
import co.lucz.binancetraderbot.binance.entities.Balance;
import co.lucz.binancetraderbot.binance.entities.OpenOrderResponse;
import co.lucz.binancetraderbot.binance.entities.Symbol;
import co.lucz.binancetraderbot.binance.entities.enums.SymbolStatus;
import co.lucz.binancetraderbot.entities.GlobalTradingLock;
import co.lucz.binancetraderbot.entities.TradingConfiguration;
import co.lucz.binancetraderbot.exceptions.internal.BadRequestException;
import co.lucz.binancetraderbot.exceptions.internal.SymbolAlreadyExistsException;
import co.lucz.binancetraderbot.helpers.SymbolHelpers;
import co.lucz.binancetraderbot.methods.entities.requests.CreateTradingConfigurationRequest;
import co.lucz.binancetraderbot.methods.entities.requests.DeleteTradingConfigurationRequest;
import co.lucz.binancetraderbot.methods.entities.requests.EditTradingConfigurationRequest;
import co.lucz.binancetraderbot.methods.entities.requests.SetGlobalTradingLockRequest;
import co.lucz.binancetraderbot.methods.entities.responses.GetGlobalTradingLockResponse;
import co.lucz.binancetraderbot.methods.entities.responses.GetTradingConfigurationResponse;
import co.lucz.binancetraderbot.repositories.GlobalTradingLockRepository;
import co.lucz.binancetraderbot.repositories.TradingConfigurationRepository;
import co.lucz.binancetraderbot.strategies.BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy;
import co.lucz.binancetraderbot.strategies.TradingStrategy;
import co.lucz.binancetraderbot.strategies.TradingStrategyName;
import co.lucz.binancetraderbot.structures.BookTickerSubscriptionInfo;
import co.lucz.binancetraderbot.structures.PriceInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TraderService {
    private final Duration BINANCE_WEBSOCKET_CONNECTION_MAX_DURATION = Duration.ofHours(23);

    private final Map<String, TradingConfiguration> tradingConfigurationsCache = new HashMap<>();
    private boolean globalTradingLockCache = true;

    private final Map<String, BookTickerSubscriptionInfo> bookTickerSubscriptionInfos = new HashMap<>();
    private final Map<String, List<PriceInfo>> priceInfos = new HashMap<>();

    @Autowired
    private BinanceClient binanceClient;

    @Autowired
    private ErrorLoggerService errorLoggerService;

    @Autowired
    private TradingConfigurationRepository tradingConfigurationRepository;

    @Autowired
    private GlobalTradingLockRepository globalTradingLockRepository;

    // region API interface

    public List<String> getTradableSymbols() {
        return this.binanceClient.getExchangeInfo(false).getSymbols().values().stream()
                .filter(symbol -> symbol.getStatus() == SymbolStatus.TRADING)
                .map(s -> SymbolHelpers.getSymbolId(s.getBaseAsset(), s.getQuoteAsset()))
                .collect(Collectors.toList());
    }

    public void refreshTradableSymbols() {
        this.binanceClient.getExchangeInfo(true);
    }

    public List<GetTradingConfigurationResponse> getTradingConfigurations() {
        List<GetTradingConfigurationResponse> getTradingConfigurationResponses = new ArrayList<>();
        this.tradingConfigurationRepository.findAll().forEach(
                tradingConfiguration -> getTradingConfigurationResponses.add(new GetTradingConfigurationResponse(
                                                                                     tradingConfiguration.getSymbolId(),
                                                                                     tradingConfiguration.getTradingStrategyName(),
                                                                                     tradingConfiguration.getTradingStrategyConfiguration(),
                                                                                     tradingConfiguration.getEnabled()
                                                                             )
                )
        );
        return getTradingConfigurationResponses;
    }

    public void createTradingConfiguration(CreateTradingConfigurationRequest request) {
        String symbolId = request.getSymbolId().toUpperCase();
        if (this.tradingConfigurationRepository.findBySymbolId(symbolId).isPresent()) {
            throw new SymbolAlreadyExistsException("trading configuration for given symbol already exists");
        }

        this.validateTradableSymbol(symbolId);

        TradingStrategyName tradingStrategyName = this.validateTradingStrategyName(request.getTradingStrategyName());
        String tradingStrategyConfiguration = request.getTradingStrategyConfiguration();
        this.validateTradingStrategyConfiguration(tradingStrategyName, tradingStrategyConfiguration);

        TradingConfiguration tradingConfiguration = new TradingConfiguration(symbolId,
                                                                             tradingStrategyName,
                                                                             tradingStrategyConfiguration,
                                                                             request.getEnabled());
        this.tradingConfigurationRepository.save(tradingConfiguration);

        this.tradingConfigurationsCache.put(symbolId, tradingConfiguration);
        this.subscribeTradingStrategy(symbolId);
    }

    public void editTradingConfiguration(EditTradingConfigurationRequest request) {
        String symbolId = request.getSymbolId().toUpperCase();
        TradingConfiguration tradingConfiguration = this.tradingConfigurationRepository.findBySymbolId(symbolId)
                .orElseThrow(() -> new BadRequestException("no such trading configuration for symbol id"));

        TradingStrategyName tradingStrategyName = this.validateTradingStrategyName(request.getTradingStrategyName());

        String tradingStrategyConfiguration = request.getTradingStrategyConfiguration();
        this.validateTradingStrategyConfiguration(tradingStrategyName, tradingStrategyConfiguration);

        tradingConfiguration.setTradingStrategyName(tradingStrategyName);
        tradingConfiguration.setTradingStrategyConfiguration(tradingStrategyConfiguration);
        tradingConfiguration.setEnabled(request.getEnabled());

        this.tradingConfigurationRepository.save(tradingConfiguration);

        this.tradingConfigurationsCache.put(symbolId, tradingConfiguration);
    }

    public void deleteTradingConfiguration(DeleteTradingConfigurationRequest request) {
        String symbolId = request.getSymbolId().toUpperCase();
        TradingConfiguration tradingConfiguration = this.tradingConfigurationRepository.findBySymbolId(symbolId)
                .orElseThrow(() -> new BadRequestException("no such symbol id"));

        this.tradingConfigurationRepository.delete(tradingConfiguration);
        this.unsubscribeTradingStrategy(symbolId);
        this.tradingConfigurationsCache.remove(symbolId);
    }

    public GetGlobalTradingLockResponse getGlobalTradingLock() {
        boolean tradingIsLocked = this.getGlobalTradingLockInternal();
        return new GetGlobalTradingLockResponse(tradingIsLocked);
    }

    public void setGlobalTradingLock(SetGlobalTradingLockRequest request) {
        boolean lockTargetValue = request.getLockTargetValue();
        boolean lockActualValue = this.getGlobalTradingLockInternal();

        if (lockTargetValue) {
            if (!lockActualValue) {
                try {
                    this.globalTradingLockRepository.save(new GlobalTradingLock());
                } finally {
                    this.globalTradingLockCache = true;
                }
            }
        } else {
            try {
                this.globalTradingLockRepository.deleteAll();
            } finally {
                this.globalTradingLockCache = false;
            }
        }
    }

    public List<OpenOrderResponse> getCurrentOpenOrders() {
        return this.binanceClient.getOpenOrders();
    }

    public List<Balance> getBalances() {
        return new ArrayList<>(this.binanceClient.getBalances().values());
    }

    // endregion

    // region lifecycle methods

    @PostConstruct
    private void initializeTrading() {
        this.tradingConfigurationRepository.findAll().forEach(
                tradingConfiguration -> this.tradingConfigurationsCache.put(tradingConfiguration.getSymbolId(),
                                                                            tradingConfiguration));
        this.tradingConfigurationsCache.keySet().forEach(this::subscribeTradingStrategy);

        this.globalTradingLockCache = this.getGlobalTradingLockInternal();
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    private void renewTradingStrategySubscriptionsIfNeeded() {
        this.bookTickerSubscriptionInfos.keySet().forEach(symbolId -> {
            boolean unsubscribed = this.unsubscribeTradingStrategyIfExpired(symbolId);
            if (unsubscribed) {
                this.subscribeTradingStrategy(symbolId);
            }
        });
    }

    @Scheduled(fixedDelay = 7, timeUnit = TimeUnit.DAYS)
    private void cleanUpErrorLogs() {
        this.errorLoggerService.deleteAllErrorLogs();
    }

    // endregion

    private void subscribeTradingStrategy(String symbolId) {
        String symbol = SymbolHelpers.getSymbol(symbolId);
        int connectionId = this.binanceClient.bookTicker(symbol, response ->
                this.handleBookTickerResponse(symbolId, response));
        BookTickerSubscriptionInfo bookTickerSubscriptionInfo =
                new BookTickerSubscriptionInfo(connectionId, Instant.now());
        this.bookTickerSubscriptionInfos.put(symbolId, bookTickerSubscriptionInfo);
    }

    private boolean unsubscribeTradingStrategyIfExpired(String symbolId) {
        BookTickerSubscriptionInfo bookTickerSubscriptionInfo = this.bookTickerSubscriptionInfos.get(symbolId);

        Instant connectionInitiation = bookTickerSubscriptionInfo.getSubscribedAt();
        Instant connectionExpiration = connectionInitiation.plus(this.BINANCE_WEBSOCKET_CONNECTION_MAX_DURATION);
        boolean connectionExpired = connectionExpiration.isBefore(Instant.now());
        if (connectionExpired) {
            this.unsubscribeTradingStrategy(symbolId);
        }
        return connectionExpired;
    }

    private void unsubscribeTradingStrategy(String symbolId) {
        BookTickerSubscriptionInfo bookTickerSubscriptionInfo = this.bookTickerSubscriptionInfos.get(symbolId);
        if (bookTickerSubscriptionInfo != null) {
            int websocketConnectionId = bookTickerSubscriptionInfo.getConnectionId();
            this.binanceClient.closeWebsocketConnection(websocketConnectionId);
        }
        this.bookTickerSubscriptionInfos.remove(symbolId);
    }

    private void handleBookTickerResponse(String symbolId, String response) {
        JSONObject responseJson = new JSONObject(response);

        BigDecimal bestBidPrice = new BigDecimal(responseJson.getString("b"));
        BigDecimal bestBidQuantity = new BigDecimal(responseJson.getString("B"));
        BigDecimal bestAskPrice = new BigDecimal(responseJson.getString("a"));
        BigDecimal bestAskQuantity = new BigDecimal(responseJson.getString("A"));

        PriceInfo latestPriceInfo = new PriceInfo(bestBidPrice, bestBidQuantity, bestAskPrice, bestAskQuantity);
        List<PriceInfo> priceInfos = this.updatePriceInfos(symbolId, latestPriceInfo);

        if (!this.globalTradingLockCache) {
            TradingConfiguration tradingConfiguration = this.tradingConfigurationsCache.get(symbolId);
            if (tradingConfiguration.getEnabled()) {
                try {
                    TradingStrategy tradingStrategy = this.getValidTradingStrategy(tradingConfiguration);
                    tradingStrategy.setBinanceClient(this.binanceClient);
                    tradingStrategy.act(symbolId, priceInfos);
                } catch (Exception e) {
                    this.errorLoggerService.logThrowable(e);
                }
            }
        }
    }

    private boolean getGlobalTradingLockInternal() {
        return this.globalTradingLockRepository.count() > 0;
    }

    private List<PriceInfo> updatePriceInfos(String symbolId, PriceInfo latestPriceInfo) {
        List<PriceInfo> priceInfos = this.priceInfos.getOrDefault(symbolId, new ArrayList<>());

        Duration longestPriceMonitorWindow = this.getLongestPriceMonitorWindow();
        Instant monitorWindowStart = Instant.now().minus(longestPriceMonitorWindow);
        priceInfos.removeIf(priceInfo -> priceInfo.getInstant().isBefore(monitorWindowStart));

        priceInfos.add(latestPriceInfo);
        this.priceInfos.put(symbolId, priceInfos);

        return priceInfos;
    }

    private Duration getLongestPriceMonitorWindow() {
        Set<Duration> priceMonitorWindows = this.tradingConfigurationsCache.values().stream()
                .filter(TradingConfiguration::getEnabled)
                .map(this::getValidTradingStrategy)
                .map(TradingStrategy::getPriceMonitorWindow)
                .collect(Collectors.toSet());

        return Collections.max(priceMonitorWindows);
    }

    private void validateTradableSymbol(String symbolId) {
        String symbol = SymbolHelpers.getSymbol(symbolId);
        Symbol s = this.binanceClient.getExchangeInfo(false).getSymbols().get(symbol);
        if (s == null) {
            throw new BadRequestException("not existing symbol");
        }
        if (s.getStatus() != SymbolStatus.TRADING) {
            throw new BadRequestException("not tradable symbol");
        }
    }

    private TradingStrategyName validateTradingStrategyName(String tradingStrategyName) {
        try {
            return TradingStrategyName.valueOf(tradingStrategyName);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("invalid trading strategy name");
        }
    }

    private void validateTradingStrategyConfiguration(TradingStrategyName tradingStrategyName,
                                                      String tradingStrategyConfiguration) {
        try {
            this.getValidTradingStrategy(tradingStrategyName, tradingStrategyConfiguration);
        } catch (Exception ex) {
            throw new BadRequestException("invalid trading strategy configuration");
        }
    }

    private TradingStrategy getValidTradingStrategy(TradingConfiguration tradingConfiguration) {
        return this.getValidTradingStrategy(tradingConfiguration.getTradingStrategyName(),
                                            tradingConfiguration.getTradingStrategyConfiguration());
    }

    private TradingStrategy getValidTradingStrategy(TradingStrategyName tradingStrategyName,
                                                    String tradingStrategyConfiguration) {
        JSONObject tradingStrategyConfigurationJson = new JSONObject(tradingStrategyConfiguration);

        switch (tradingStrategyName) {
            case BuyOnPercentageDecreaseInTimeframeAndSetLimitOrder:
                return BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy.ofTradingStrategyConfigurationJson(
                        tradingStrategyConfigurationJson);

            default:
                throw new IllegalArgumentException("invalid trading strategy");
        }
    }
}
