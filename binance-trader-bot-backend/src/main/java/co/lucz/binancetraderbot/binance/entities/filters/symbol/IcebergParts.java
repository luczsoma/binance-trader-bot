package co.lucz.binancetraderbot.binance.entities.filters.symbol;

import co.lucz.binancetraderbot.binance.entities.enums.FilterTypeSymbol;
import co.lucz.binancetraderbot.binance.entities.filters.SymbolFilter;

public final class IcebergParts extends SymbolFilter {
    private final int limit;

    public IcebergParts(int limit) {
        super(FilterTypeSymbol.ICEBERG_PARTS);
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }
}
