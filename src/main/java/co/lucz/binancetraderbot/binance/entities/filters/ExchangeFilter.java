package co.lucz.binancetraderbot.binance.entities.filters;

import co.lucz.binancetraderbot.binance.entities.RateLimit;
import co.lucz.binancetraderbot.binance.entities.enums.FilterTypeExchange;
import co.lucz.binancetraderbot.binance.entities.filters.exchange.ExchangeMaxNumAlgoOrders;
import co.lucz.binancetraderbot.binance.entities.filters.exchange.ExchangeMaxNumOrders;
import co.lucz.binancetraderbot.binance.exceptions.InvalidFilterTypeException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class ExchangeFilter {
    private final FilterTypeExchange filterType;

    public ExchangeFilter(FilterTypeExchange filterType) {
        this.filterType = filterType;
    }

    public static ExchangeFilter of(JSONObject jsonObject) {
        String filterType = jsonObject.getString("filterType");
        FilterTypeExchange filterTypeEnum = FilterTypeExchange.valueOf(filterType);
        switch (filterTypeEnum) {
            case EXCHANGE_MAX_NUM_ORDERS:
                return new ExchangeMaxNumOrders(
                        jsonObject.getInt("maxNumOrders")
                );

            case EXCHANGE_MAX_NUM_ALGO_ORDERS:
                return new ExchangeMaxNumAlgoOrders(
                        jsonObject.getInt("maxNumAlgoOrders")
                );

            default:
                throw new InvalidFilterTypeException();
        }
    }

    public static List<ExchangeFilter> of(JSONArray jsonArray) {
        List<ExchangeFilter> exchangeFilters = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject exchangeFilterJson = jsonArray.getJSONObject(i);
            ExchangeFilter exchangeFilter = ExchangeFilter.of(exchangeFilterJson);
            exchangeFilters.add(exchangeFilter);
        }
        return exchangeFilters;
    }

    public final FilterTypeExchange getFilterType() {
        return filterType;
    }
}
