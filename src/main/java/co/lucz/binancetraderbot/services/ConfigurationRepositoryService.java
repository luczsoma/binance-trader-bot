package co.lucz.binancetraderbot.services;

import co.lucz.binancetraderbot.binance.BinanceClient;
import co.lucz.binancetraderbot.entities.TradingConfiguration;
import co.lucz.binancetraderbot.exceptions.internal.BadRequestException;
import co.lucz.binancetraderbot.helpers.SymbolHelpers;
import co.lucz.binancetraderbot.methods.entities.requests.CreateTradingConfigurationRequest;
import co.lucz.binancetraderbot.methods.entities.requests.EditTradingConfigurationRequest;
import co.lucz.binancetraderbot.repositories.TradingConfigurationRepository;
import co.lucz.binancetraderbot.strategies.BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy;
import co.lucz.binancetraderbot.strategies.TradingStrategy;
import co.lucz.binancetraderbot.strategies.TradingStrategyName;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConfigurationRepositoryService {
    private final Map<String, TradingStrategy> tradingStrategyBySymbolId = new HashMap<>();

    @Autowired
    private TradingConfigurationRepository tradingConfigurationRepository;

    @Autowired
    private BinanceClient binanceClient;

    public Map<String, TradingStrategy> getTradingStrategies() {
        return tradingStrategyBySymbolId;
    }

    public void refreshConfiguration() {
        this.tradingConfigurationRepository.findAll().forEach(tradingConfiguration -> {
            String symbolId = tradingConfiguration.getSymbolId();
            TradingStrategy tradingStrategy = this.getValidTradingStrategy(tradingConfiguration.getTradingStrategyName(),
                                                                           tradingConfiguration.getTradingStrategyConfiguration());
            this.tradingStrategyBySymbolId.put(symbolId, tradingStrategy);
        });
    }

    public Duration getLongestPriceMonitorWindow() {
        return Collections.max(
                this.tradingStrategyBySymbolId.values().stream()
                        .map(TradingStrategy::getPriceMonitorWindow)
                        .collect(Collectors.toSet())
        );
    }

    public void createTradingConfiguration(CreateTradingConfigurationRequest request) {
        String symbolId = request.getSymbolId();
        this.validateSymbol(symbolId);

        TradingStrategyName tradingStrategyName = this.validateTradingStrategyIdentifier(request.getTradingStrategyIdentifier());

        String tradingStrategyConfiguration = request.getTradingStrategyConfiguration();
        this.validateTradingStrategyConfiguration(tradingStrategyName, tradingStrategyConfiguration);

        TradingConfiguration tradingConfiguration = new TradingConfiguration(symbolId,
                                                                             tradingStrategyName,
                                                                             tradingStrategyConfiguration);
        this.tradingConfigurationRepository.save(tradingConfiguration);
    }

    public void editTradingConfiguration(EditTradingConfigurationRequest request) {
        long tradingConfigurationId = request.getTradingConfigurationId();
        TradingConfiguration tradingConfiguration = this.tradingConfigurationRepository.findById(tradingConfigurationId)
                .orElseThrow(() -> new BadRequestException("invalid trading configuration id"));

        String symbolId = request.getSymbolId();
        this.validateSymbol(symbolId);

        TradingStrategyName tradingStrategyName = this.validateTradingStrategyIdentifier(request.getTradingStrategyIdentifier());

        String tradingStrategyConfiguration = request.getTradingStrategyConfiguration();
        this.validateTradingStrategyConfiguration(tradingStrategyName, tradingStrategyConfiguration);

        tradingConfiguration.setSymbolId(symbolId);
        tradingConfiguration.setTradingStrategyName(tradingStrategyName);
        tradingConfiguration.setTradingStrategyConfiguration(tradingStrategyConfiguration);

        this.tradingConfigurationRepository.save(tradingConfiguration);
    }

    private TradingStrategyName validateTradingStrategyIdentifier(String tradingStrategyIdentifier) {
        try {
            return TradingStrategyName.valueOf(tradingStrategyIdentifier);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("invalid trading strategy identifier");
        }
    }

    private void validateSymbol(String symbolId) {
        String symbol = SymbolHelpers.getSymbol(symbolId);
        try {
            this.binanceClient.getExchangeInfo(Set.of(symbol));
        } catch (Exception ex) {
            throw new BadRequestException("invalid symbol id");
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
