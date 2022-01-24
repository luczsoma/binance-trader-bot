package co.lucz.binancetraderbot.binance.entities.filters.symbol;

import co.lucz.binancetraderbot.binance.entities.enums.FilterTypeSymbol;
import co.lucz.binancetraderbot.binance.entities.filters.SymbolFilter;

public final class MaxNumAlgoOrders extends SymbolFilter {
    private final int maxNumAlgoOrders;

    public MaxNumAlgoOrders(int maxNumAlgoOrders) {
        super(FilterTypeSymbol.MAX_NUM_ALGO_ORDERS);
        this.maxNumAlgoOrders = maxNumAlgoOrders;
    }

    public int getMaxNumAlgoOrders() {
        return maxNumAlgoOrders;
    }
}
