package co.lucz.binancetraderbot.methods.entities.responses;

import co.lucz.binancetraderbot.strategies.TradingStrategyName;

public class GetTradingConfigurationResponse {
    private String symbolId;
    private TradingStrategyName tradingStrategyName;
    private String tradingStrategyConfiguration;

    public GetTradingConfigurationResponse(String symbolId,
                                           TradingStrategyName tradingStrategyName,
                                           String tradingStrategyConfiguration) {
        this.symbolId = symbolId;
        this.tradingStrategyName = tradingStrategyName;
        this.tradingStrategyConfiguration = tradingStrategyConfiguration;
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
}
