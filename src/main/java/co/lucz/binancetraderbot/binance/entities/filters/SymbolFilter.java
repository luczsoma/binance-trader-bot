package co.lucz.binancetraderbot.binance.entities.filters;

import co.lucz.binancetraderbot.binance.entities.enums.FilterTypeSymbol;
import co.lucz.binancetraderbot.binance.entities.filters.symbol.IcebergParts;
import co.lucz.binancetraderbot.binance.entities.filters.symbol.LotSize;
import co.lucz.binancetraderbot.binance.entities.filters.symbol.MarketLotSize;
import co.lucz.binancetraderbot.binance.entities.filters.symbol.MaxNumAlgoOrders;
import co.lucz.binancetraderbot.binance.entities.filters.symbol.MaxNumIcebergOrders;
import co.lucz.binancetraderbot.binance.entities.filters.symbol.MaxNumOrders;
import co.lucz.binancetraderbot.binance.entities.filters.symbol.MaxPosition;
import co.lucz.binancetraderbot.binance.entities.filters.symbol.MinNotional;
import co.lucz.binancetraderbot.binance.entities.filters.symbol.PercentPrice;
import co.lucz.binancetraderbot.binance.entities.filters.symbol.PriceFilter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public abstract class SymbolFilter {
    private final FilterTypeSymbol filterType;

    public SymbolFilter(FilterTypeSymbol filterType) {
        this.filterType = filterType;
    }

    public static SymbolFilter of(JSONObject jsonObject) {
        String filterType = jsonObject.getString("filterType");
        FilterTypeSymbol filterTypeEnum = FilterTypeSymbol.valueOf(filterType);

        switch (filterTypeEnum) {
            case PRICE_FILTER:
                return new PriceFilter(new BigDecimal(jsonObject.getString("minPrice")),
                                       new BigDecimal(jsonObject.getString("maxPrice")),
                                       new BigDecimal(jsonObject.getString("tickSize")));

            case PERCENT_PRICE:
                return new PercentPrice(new BigDecimal(jsonObject.getString("multiplierUp")),
                                        new BigDecimal(jsonObject.getString("multiplierDown")),
                                        jsonObject.getInt("avgPriceMins"));

            case LOT_SIZE:
                return new LotSize(new BigDecimal(jsonObject.getString("minQty")),
                                   new BigDecimal(jsonObject.getString("maxQty")),
                                   new BigDecimal(jsonObject.getString("stepSize")));

            case MIN_NOTIONAL:
                return new MinNotional(new BigDecimal(jsonObject.getString("minNotional")),
                                       jsonObject.getBoolean("applyToMarket"),
                                       jsonObject.getInt("avgPriceMins"));

            case ICEBERG_PARTS:
                return new IcebergParts(jsonObject.getInt("limit"));

            case MARKET_LOT_SIZE:
                return new MarketLotSize(new BigDecimal(jsonObject.getString("minQty")),
                                         new BigDecimal(jsonObject.getString("maxQty")),
                                         new BigDecimal(jsonObject.getString("stepSize")));

            case MAX_NUM_ORDERS:
                return new MaxNumOrders(jsonObject.getInt("maxNumOrders"));

            case MAX_NUM_ALGO_ORDERS:
                return new MaxNumAlgoOrders(jsonObject.getInt("maxNumAlgoOrders"));

            case MAX_NUM_ICEBERG_ORDERS:
                return new MaxNumIcebergOrders(jsonObject.getInt("maxNumIcebergOrders"));

            case MAX_POSITION:
                return new MaxPosition(new BigDecimal(jsonObject.getString("maxPosition")));

            default:
                throw new IllegalArgumentException("invalid filter type");
        }
    }

    public static List<SymbolFilter> of(JSONArray jsonArray) {
        List<SymbolFilter> symbolFilters = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject symbolFilterJson = jsonArray.getJSONObject(i);
            SymbolFilter symbolFilter = SymbolFilter.of(symbolFilterJson);
            symbolFilters.add(symbolFilter);
        }
        return symbolFilters;
    }

    public final FilterTypeSymbol getFilterType() {
        return filterType;
    }
}
