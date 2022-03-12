package co.lucz.binancetraderbot.methods.entities.requests;

public class EditTradingConfigurationRequest {
    private String symbolId;
    private String tradingStrategyName;
    private String tradingStrategyConfiguration;
    private boolean enabled;

    public String getSymbolId() {
        return symbolId;
    }

    public String getTradingStrategyName() {
        return tradingStrategyName;
    }

    public String getTradingStrategyConfiguration() {
        return tradingStrategyConfiguration;
    }

    public boolean getEnabled() {
        return enabled;
    }
}
