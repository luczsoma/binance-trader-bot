package co.lucz.binancetraderbot.services;

import co.lucz.binancetraderbot.binance.BinanceClient;
import co.lucz.binancetraderbot.binance.entities.Balance;
import co.lucz.binancetraderbot.binance.entities.OpenOrderResponse;
import co.lucz.binancetraderbot.entities.GlobalTradingLock;
import co.lucz.binancetraderbot.helpers.SymbolHelpers;
import co.lucz.binancetraderbot.methods.entities.requests.CreateTradingConfigurationRequest;
import co.lucz.binancetraderbot.methods.entities.requests.EditTradingConfigurationRequest;
import co.lucz.binancetraderbot.methods.entities.requests.SetGlobalTradingLockRequest;
import co.lucz.binancetraderbot.methods.entities.responses.GetGlobalTradingLockResponse;
import co.lucz.binancetraderbot.repositories.GlobalTradingLockRepository;
import co.lucz.binancetraderbot.strategies.TradingStrategy;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class TraderService {
    @Autowired
    private BinanceClient binanceClient;

    @Autowired
    private ConfigurationRepositoryService configurationRepositoryService;

    @Autowired
    private ErrorLoggerService errorLoggerService;

    @Autowired
    private GlobalTradingLockRepository globalTradingLockRepository;

    private final Map<String, List<PriceInfo>> priceInfosBySymbolId = new HashMap<>();

    private final AtomicBoolean temporaryTradingLock = new AtomicBoolean();

    public void createTradingConfiguration(CreateTradingConfigurationRequest request) {
        this.configurationRepositoryService.createTradingConfiguration(request);
        this.renewTradingStrategySubscriptions();
    }

    public void editTradingConfiguration(EditTradingConfigurationRequest request) {
        this.configurationRepositoryService.editTradingConfiguration(request);
        this.renewTradingStrategySubscriptions();
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
                this.globalTradingLockRepository.save(new GlobalTradingLock());
            }
        } else {
            this.globalTradingLockRepository.deleteAll();
        }
    }

    public List<OpenOrderResponse> getCurrentOpenOrders() {
        return this.binanceClient.getOpenOrders();
    }

    public List<Balance> getBalances() {
        return new ArrayList<>(this.binanceClient.getBalances().values());
    }

    @PostConstruct
    private void initializeTrading() {
        Map<String, TradingStrategy> tradingStrategies = this.configurationRepositoryService.getTradingStrategies();
        Set<String> symbols = tradingStrategies.keySet().stream()
                .map(SymbolHelpers::getSymbol)
                .collect(Collectors.toSet());
        this.binanceClient.cacheExchangeInfo(symbols);
        this.subscribeTradingStrategies(tradingStrategies);
    }

    @Scheduled(fixedDelay = 23, timeUnit = TimeUnit.HOURS)
    private void renewTradingStrategySubscriptions() {
        this.binanceClient.closeAllWebsocketConnections();

        Map<String, TradingStrategy> tradingStrategies = this.configurationRepositoryService.getTradingStrategies();
        this.subscribeTradingStrategies(tradingStrategies);
    }

    private void subscribeTradingStrategies(Map<String, TradingStrategy> tradingStrategies) {
        tradingStrategies.forEach((symbolId, strategy) -> {
            strategy.setBinanceClient(this.binanceClient);
            this.binanceClient.bookTicker(SymbolHelpers.getSymbol(symbolId), response -> {
                JSONObject responseJson = new JSONObject(response);

                BigDecimal bestBidPrice = new BigDecimal(responseJson.getString("b"));
                BigDecimal bestBidQuantity = new BigDecimal(responseJson.getString("B"));
                BigDecimal bestAskPrice = new BigDecimal(responseJson.getString("a"));
                BigDecimal bestAskQuantity = new BigDecimal(responseJson.getString("A"));

                PriceInfo latestPriceInfo = new PriceInfo(bestBidPrice, bestBidQuantity, bestAskPrice, bestAskQuantity);
                List<PriceInfo> priceInfos = this.updatePriceInfos(symbolId, latestPriceInfo);

                boolean isGlobalTradingLocked = this.getGlobalTradingLockInternal();
                if (!isGlobalTradingLocked) {
                    if (this.temporaryTradingLock.compareAndSet(false, true)) {
                        try {
                            strategy.act(symbolId, priceInfos);
                        } catch (Exception e) {
                            this.errorLoggerService.logThrowable(e);
                        } finally {
                            this.temporaryTradingLock.set(false);
                        }
                    }
                }
            });
        });
    }

    private boolean getGlobalTradingLockInternal() {
        return this.globalTradingLockRepository.count() > 0;
    }

    private List<PriceInfo> updatePriceInfos(String symbolId, PriceInfo latestPriceInfo) {
        List<PriceInfo> priceInfos = this.priceInfosBySymbolId.getOrDefault(symbolId, new ArrayList<>());

        Duration longestPriceMonitorWindow = this.configurationRepositoryService.getLongestPriceMonitorWindow();
        Instant monitorWindowStart = Instant.now().minus(longestPriceMonitorWindow);
        priceInfos.removeIf(priceInfo -> priceInfo.getInstant().isBefore(monitorWindowStart));

        priceInfos.add(latestPriceInfo);
        this.priceInfosBySymbolId.put(symbolId, priceInfos);

        return priceInfos;
    }
}
