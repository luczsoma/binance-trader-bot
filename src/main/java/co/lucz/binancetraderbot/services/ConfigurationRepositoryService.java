package co.lucz.binancetraderbot.services;

import co.lucz.binancetraderbot.entities.TradingConfiguration;
import co.lucz.binancetraderbot.repositories.TradingConfigurationRepository;
import co.lucz.binancetraderbot.strategies.BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy;
import co.lucz.binancetraderbot.strategies.TradingStrategy;
import co.lucz.binancetraderbot.strategies.TradingStrategyId;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConfigurationRepositoryService {
    private final Map<String, TradingStrategy> tradingStrategyBySymbolId = new HashMap<>();

    @Autowired
    private TradingConfigurationRepository tradingConfigurationRepository;

    public Map<String, TradingStrategy> getTradingStrategies() {
        return tradingStrategyBySymbolId;
    }

    public void refreshConfiguration() {
        this.tradingConfigurationRepository.findAll().forEach(tradingConfiguration -> {
            String symbolId = tradingConfiguration.getSymbolId();
            TradingStrategy tradingStrategy = this.getTradingStrategy(tradingConfiguration);
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

    private TradingStrategy getTradingStrategy(TradingConfiguration tradingConfiguration) {
        String tradingStrategyConfigurationJsonString = tradingConfiguration.getTradingStrategyConfigurationJson();
        JSONObject tradingStrategyConfigurationJson = new JSONObject(tradingStrategyConfigurationJsonString);

        TradingStrategyId tradingStrategyId = tradingConfiguration.getTradingStrategyId();
        switch (tradingStrategyId) {
            case BUY_ON_PERCENTAGE_DECREASE_IN_TIMEFRAME_AND_SET_LIMIT_ORDER:
                return new BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy(
                        Duration.ofSeconds(tradingStrategyConfigurationJson.getLong("priceMonitorWindowSeconds")),
                        new BigDecimal(tradingStrategyConfigurationJson.getString("priceDecreaseTriggerRatio")),
                        new BigDecimal(tradingStrategyConfigurationJson.getString("buySpendAmount")),
                        new BigDecimal(tradingStrategyConfigurationJson.getString("limitSellPriceRatio"))
                );

            default:
                throw new IllegalArgumentException("invalid trading strategy");
        }
    }
}
