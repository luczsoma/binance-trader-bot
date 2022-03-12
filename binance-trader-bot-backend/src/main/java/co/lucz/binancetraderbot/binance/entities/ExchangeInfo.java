package co.lucz.binancetraderbot.binance.entities;

import co.lucz.binancetraderbot.binance.entities.enums.FilterTypeExchange;
import co.lucz.binancetraderbot.binance.entities.filters.ExchangeFilter;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

public class ExchangeInfo {
    private List<RateLimit> rateLimits = new ArrayList<>();
    private Map<FilterTypeExchange, ExchangeFilter> exchangeFilters = new HashMap<>();
    private Map<String, Symbol> symbols = new HashMap<>();

    public static ExchangeInfo of(JSONObject jsonObject) {
        ExchangeInfo exchangeInfo = new ExchangeInfo();
        exchangeInfo.setRateLimits(RateLimit.of(jsonObject.getJSONArray("rateLimits")));
        exchangeInfo.setExchangeFilters(ExchangeFilter.of(jsonObject.getJSONArray("exchangeFilters")).stream()
                                                .collect(Collectors.toMap(ExchangeFilter::getFilterType, identity())));
        exchangeInfo.setSymbols(Symbol.of(jsonObject.getJSONArray("symbols")).stream()
                                        .collect(Collectors.toMap(Symbol::getSymbol, identity())));
        return exchangeInfo;
    }

    public List<RateLimit> getRateLimits() {
        return rateLimits;
    }

    private void setRateLimits(List<RateLimit> rateLimits) {
        this.rateLimits = rateLimits;
    }

    public Map<FilterTypeExchange, ExchangeFilter> getExchangeFilters() {
        return exchangeFilters;
    }

    private void setExchangeFilters(Map<FilterTypeExchange, ExchangeFilter> exchangeFilters) {
        this.exchangeFilters = exchangeFilters;
    }

    public Map<String, Symbol> getSymbols() {
        return symbols;
    }

    private void setSymbols(Map<String, Symbol> symbols) {
        this.symbols = symbols;
    }

    public void update(ExchangeInfo exchangeInfo) {
        this.rateLimits = exchangeInfo.getRateLimits();
        this.exchangeFilters = exchangeInfo.getExchangeFilters();
        this.symbols.putAll(exchangeInfo.getSymbols());
    }
}
