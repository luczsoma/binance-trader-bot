package co.lucz.binancetraderbot.binance.entities.filters.symbol;

import co.lucz.binancetraderbot.binance.entities.enums.FilterTypeSymbol;
import co.lucz.binancetraderbot.binance.entities.filters.SymbolFilter;

import java.math.BigDecimal;

public final class MinNotional extends SymbolFilter {
    private final BigDecimal minNotional;
    private final boolean applyToMarket;
    private final int avgPriceMins;

    public MinNotional(BigDecimal minNotional, boolean applyToMarket, int avgPriceMins) {
        super(FilterTypeSymbol.MIN_NOTIONAL);
        this.minNotional = minNotional;
        this.applyToMarket = applyToMarket;
        this.avgPriceMins = avgPriceMins;
    }

    public BigDecimal getMinNotional() {
        return minNotional;
    }

    public boolean isApplyToMarket() {
        return applyToMarket;
    }

    public int getAvgPriceMins() {
        return avgPriceMins;
    }
}
