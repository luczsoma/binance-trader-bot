package co.lucz.binancetraderbot.binance.entities.filters.symbol;

import co.lucz.binancetraderbot.binance.entities.enums.FilterTypeSymbol;
import co.lucz.binancetraderbot.binance.entities.filters.SymbolFilter;

public final class MaxNumOrders extends SymbolFilter {
    private final int maxNumOrders;

    public MaxNumOrders(int maxNumOrders) {
        super(FilterTypeSymbol.MAX_NUM_ORDERS);
        this.maxNumOrders = maxNumOrders;
    }

    public int getMaxNumOrders() {
        return maxNumOrders;
    }
}
