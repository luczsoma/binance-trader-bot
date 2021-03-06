package co.lucz.binancetraderbot.binance.entities;

import co.lucz.binancetraderbot.binance.entities.enums.FilterTypeExchange;
import co.lucz.binancetraderbot.binance.entities.filters.ExchangeFilter;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

public class ExchangeInfo {
    private final List<RateLimit> rateLimits;
    private final Map<FilterTypeExchange, ExchangeFilter> exchangeFilters;
    private final Map<String, Symbol> symbols;

    public ExchangeInfo(List<RateLimit> rateLimits,
                        Map<FilterTypeExchange, ExchangeFilter> exchangeFilters,
                        Map<String, Symbol> symbols) {
        this.rateLimits = rateLimits;
        this.exchangeFilters = exchangeFilters;
        this.symbols = symbols;
    }

    public static ExchangeInfo of(JSONObject jsonObject) {
        return new ExchangeInfo(
                RateLimit.of(jsonObject.getJSONArray("rateLimits")),
                ExchangeFilter.of(jsonObject.getJSONArray("exchangeFilters")).stream()
                        .collect(Collectors.toMap(ExchangeFilter::getFilterType, identity())),
                Symbol.of(jsonObject.getJSONArray("symbols")).stream()
                        .collect(Collectors.toMap(Symbol::getSymbol, identity()))
        );
    }

    public List<RateLimit> getRateLimits() {
        return rateLimits;
    }

    public Map<FilterTypeExchange, ExchangeFilter> getExchangeFilters() {
        return exchangeFilters;
    }

    public Map<String, Symbol> getSymbols() {
        return symbols;
    }
}
