package co.lucz.binancetraderbot.entities;

import co.lucz.binancetraderbot.strategies.TradingStrategyName;

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

    private TradingStrategyName tradingStrategyName;

    private String tradingStrategyConfiguration;

    public TradingConfiguration() {
    }

    public TradingConfiguration(String symbolId,
                                TradingStrategyName tradingStrategyName,
                                String tradingStrategyConfiguration) {
        this.symbolId = symbolId;
        this.tradingStrategyName = tradingStrategyName;
        this.tradingStrategyConfiguration = tradingStrategyConfiguration;
    }

    public String getSymbolId() {
        return symbolId;
    }

    public void setSymbolId(String symbolId) {
        this.symbolId = symbolId;
    }

    public TradingStrategyName getTradingStrategyName() {
        return tradingStrategyName;
    }

    public void setTradingStrategyName(TradingStrategyName tradingStrategyName) {
        this.tradingStrategyName = tradingStrategyName;
    }

    public String getTradingStrategyConfiguration() {
        return tradingStrategyConfiguration;
    }

    public void setTradingStrategyConfiguration(String tradingStrategyConfiguration) {
        this.tradingStrategyConfiguration = tradingStrategyConfiguration;
    }
}
