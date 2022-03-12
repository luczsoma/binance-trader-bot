package co.lucz.binancetraderbot.methods.entities.responses;

import co.lucz.binancetraderbot.strategies.TradingStrategyName;

public class GetTradingConfigurationResponse {
    private final String symbolId;
    private final TradingStrategyName tradingStrategyName;
    private final String tradingStrategyConfiguration;
    private final boolean enabled;

    public GetTradingConfigurationResponse(String symbolId,
                                           TradingStrategyName tradingStrategyName,
                                           String tradingStrategyConfiguration,
                                           boolean enabled) {
        this.symbolId = symbolId;
        this.tradingStrategyName = tradingStrategyName;
        this.tradingStrategyConfiguration = tradingStrategyConfiguration;
        this.enabled = enabled;
    }

    public String getSymbolId() {
        return symbolId;
    }

    public TradingStrategyName getTradingStrategyName() {
        return tradingStrategyName;
    }

    public String getTradingStrategyConfiguration() {
        return tradingStrategyConfiguration;
    }

    public boolean getEnabled() {
        return enabled;
    }
}
