package co.lucz.binancetraderbot.binance.entities.filters.exchange;

import co.lucz.binancetraderbot.binance.entities.enums.FilterTypeExchange;
import co.lucz.binancetraderbot.binance.entities.filters.ExchangeFilter;

public final class ExchangeMaxNumAlgoOrders extends ExchangeFilter {
    private final int maxNumAlgoOrders;

    public ExchangeMaxNumAlgoOrders(int maxNumAlgoOrders) {
        super(FilterTypeExchange.EXCHANGE_MAX_NUM_ALGO_ORDERS);
        this.maxNumAlgoOrders = maxNumAlgoOrders;
    }

    public int getMaxNumAlgoOrders() {
        return maxNumAlgoOrders;
    }
}
