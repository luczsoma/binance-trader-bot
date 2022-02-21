package co.lucz.binancetraderbot.binance.entities.filters.exchange;

import co.lucz.binancetraderbot.binance.entities.enums.FilterTypeExchange;
import co.lucz.binancetraderbot.binance.entities.filters.ExchangeFilter;

public final class ExchangeMaxNumOrders extends ExchangeFilter {
    private final int maxNumOrders;

    public ExchangeMaxNumOrders(int maxNumOrders) {
        super(FilterTypeExchange.EXCHANGE_MAX_NUM_ORDERS);
        this.maxNumOrders = maxNumOrders;
    }

    public int getMaxNumOrders() {
        return maxNumOrders;
    }
}
