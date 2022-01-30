package co.lucz.binancetraderbot.entities;

import co.lucz.binancetraderbot.strategies.TradingStrategyId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class TradingConfiguration {
    @Id
    @GeneratedValue
    private long id;

    @Column(unique = true)
    private String symbolId;

    private TradingStrategyId tradingStrategyId;

    private String tradingStrategyConfigurationJson;

    public String getSymbolId() {
        return symbolId;
    }

    public void setSymbolId(String symbolId) {
        this.symbolId = symbolId;
    }

    public TradingStrategyId getTradingStrategyId() {
        return tradingStrategyId;
    }

    public void setTradingStrategyId(TradingStrategyId tradingStrategyId) {
        this.tradingStrategyId = tradingStrategyId;
    }

    public String getTradingStrategyConfigurationJson() {
        return tradingStrategyConfigurationJson;
    }

    public void setTradingStrategyConfigurationJson(String tradingStrategyConfigurationJson) {
        this.tradingStrategyConfigurationJson = tradingStrategyConfigurationJson;
    }
}
