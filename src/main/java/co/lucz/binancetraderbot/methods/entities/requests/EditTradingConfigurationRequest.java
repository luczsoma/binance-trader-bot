package co.lucz.binancetraderbot.methods.entities.requests;

public class EditTradingConfigurationRequest {
    private long tradingConfigurationId;
    private String symbolId;
    private String tradingStrategyIdentifier;
    private String tradingStrategyConfiguration;

    public long getTradingConfigurationId() {
        return tradingConfigurationId;
    }

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
