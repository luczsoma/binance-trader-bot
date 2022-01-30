package co.lucz.binancetraderbot.methods.entities.requests;

public class CreateTradingConfigurationRequest {
    private String symbolId;
    private String tradingStrategyIdentifier;
    private String tradingStrategyConfiguration;

    public String getSymbolId() {
        return symbolId;
    }

    public String getTradingStrategyIdentifier() {
        return tradingStrategyIdentifier;
    }

    public String getTradingStrategyConfiguration() {
        return tradingStrategyConfiguration;
    }
}
