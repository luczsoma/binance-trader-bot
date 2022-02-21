package co.lucz.binancetraderbot.binance.entities.filters.symbol;

import co.lucz.binancetraderbot.binance.entities.enums.FilterTypeSymbol;
import co.lucz.binancetraderbot.binance.entities.filters.SymbolFilter;

import java.math.BigDecimal;

public final class PriceFilter extends SymbolFilter {
    private final BigDecimal minPrice;
    private final BigDecimal maxPrice;
    private final BigDecimal tickSize;

    public PriceFilter(BigDecimal minPrice, BigDecimal maxPrice, BigDecimal tickSize) {
        super(FilterTypeSymbol.PRICE_FILTER);
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.tickSize = tickSize;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public BigDecimal getTickSize() {
        return tickSize;
    }
}
