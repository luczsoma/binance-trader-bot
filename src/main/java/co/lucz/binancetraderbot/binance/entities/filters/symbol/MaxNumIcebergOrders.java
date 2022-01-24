package co.lucz.binancetraderbot.binance.entities.filters.symbol;

import co.lucz.binancetraderbot.binance.entities.enums.FilterTypeSymbol;
import co.lucz.binancetraderbot.binance.entities.filters.SymbolFilter;

public final class MaxNumIcebergOrders extends SymbolFilter {
    private final int maxNumIcebergOrders;

    public MaxNumIcebergOrders(int maxNumIcebergOrders) {
        super(FilterTypeSymbol.MAX_NUM_ICEBERG_ORDERS);
        this.maxNumIcebergOrders = maxNumIcebergOrders;
    }

    public int getMaxNumIcebergOrders() {
        return maxNumIcebergOrders;
    }
}
