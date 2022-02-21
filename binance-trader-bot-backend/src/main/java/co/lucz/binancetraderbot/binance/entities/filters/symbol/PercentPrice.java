package co.lucz.binancetraderbot.binance.entities.filters.symbol;

import co.lucz.binancetraderbot.binance.entities.enums.FilterTypeSymbol;
import co.lucz.binancetraderbot.binance.entities.filters.SymbolFilter;

import java.math.BigDecimal;

public final class PercentPrice extends SymbolFilter {
    private final BigDecimal multiplierUp;
    private final BigDecimal multiplierDown;
    private final int avgPriceMins;

    public PercentPrice(BigDecimal multiplierUp, BigDecimal multiplierDown, int avgPriceMins) {
        super(FilterTypeSymbol.PERCENT_PRICE);
        this.multiplierUp = multiplierUp;
        this.multiplierDown = multiplierDown;
        this.avgPriceMins = avgPriceMins;
    }

    public BigDecimal getMultiplierUp() {
        return multiplierUp;
    }

    public BigDecimal getMultiplierDown() {
        return multiplierDown;
    }

    public int getAvgPriceMins() {
        return avgPriceMins;
    }
}
