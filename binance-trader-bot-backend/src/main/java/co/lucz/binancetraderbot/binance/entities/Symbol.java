package co.lucz.binancetraderbot.binance.entities;

import co.lucz.binancetraderbot.binance.entities.enums.FilterTypeSymbol;
import co.lucz.binancetraderbot.binance.entities.enums.OrderType;
import co.lucz.binancetraderbot.binance.entities.enums.Permission;
import co.lucz.binancetraderbot.binance.entities.enums.SymbolStatus;
import co.lucz.binancetraderbot.binance.entities.filters.SymbolFilter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

public final class Symbol {
    private final String symbol;
    private final SymbolStatus status;
    private final String baseAsset;
    private final int baseAssetPrecision;
    private final String quoteAsset;
    private final int quoteAssetPrecision;
    private final Set<OrderType> orderTypes;
    private final boolean icebergAllowed;
    private final boolean ocoAllowed;
    private final boolean isSpotTradingAllowed;
    private final boolean isMarginTradingAllowed;
    private final Map<FilterTypeSymbol, SymbolFilter> filters;
    private final Set<Permission> permissions;

    private Symbol(String symbol,
                   SymbolStatus status,
                   String baseAsset,
                   int baseAssetPrecision,
                   String quoteAsset,
                   int quoteAssetPrecision,
                   Set<OrderType> orderTypes,
                   boolean icebergAllowed,
                   boolean ocoAllowed,
                   boolean isSpotTradingAllowed,
                   boolean isMarginTradingAllowed,
                   Map<FilterTypeSymbol, SymbolFilter> filters,
                   Set<Permission> permissions) {
        this.symbol = symbol;
        this.status = status;
        this.baseAsset = baseAsset;
        this.baseAssetPrecision = baseAssetPrecision;
        this.quoteAsset = quoteAsset;
        this.quoteAssetPrecision = quoteAssetPrecision;
        this.orderTypes = orderTypes;
        this.icebergAllowed = icebergAllowed;
        this.ocoAllowed = ocoAllowed;
        this.isSpotTradingAllowed = isSpotTradingAllowed;
        this.isMarginTradingAllowed = isMarginTradingAllowed;
        this.filters = filters;
        this.permissions = permissions;
    }

    public static Symbol of(JSONObject jsonObject) {
        return new Symbol(
                jsonObject.getString("symbol"),
                SymbolStatus.valueOf(jsonObject.getString("status")),
                jsonObject.getString("baseAsset"),
                jsonObject.getInt("baseAssetPrecision"),
                jsonObject.getString("quoteAsset"),
                jsonObject.getInt("quoteAssetPrecision"),
                OrderType.of(jsonObject.getJSONArray("orderTypes")).stream()
                        .collect(Collectors.toUnmodifiableSet()),
                jsonObject.getBoolean("icebergAllowed"),
                jsonObject.getBoolean("ocoAllowed"),
                jsonObject.getBoolean("isSpotTradingAllowed"),
                jsonObject.getBoolean("isMarginTradingAllowed"),
                SymbolFilter.of(jsonObject.getJSONArray("filters")).stream()
                        .collect(Collectors.toUnmodifiableMap(SymbolFilter::getFilterType, identity())),
                Permission.of(jsonObject.getJSONArray("permissions")).stream()
                        .collect(Collectors.toUnmodifiableSet())
        );
    }

    public static List<Symbol> of(JSONArray jsonArray) {
        List<Symbol> symbols = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject symbolJson = jsonArray.getJSONObject(i);
            Symbol symbol = Symbol.of(symbolJson);
            symbols.add(symbol);
        }
        return symbols;
    }

    public String getSymbol() {
        return symbol;
    }

    public SymbolStatus getStatus() {
        return status;
    }

    public String getBaseAsset() {
        return baseAsset;
    }

    public int getBaseAssetPrecision() {
        return baseAssetPrecision;
    }

    public String getQuoteAsset() {
        return quoteAsset;
    }

    public int getQuoteAssetPrecision() {
        return quoteAssetPrecision;
    }

    public Set<OrderType> getOrderTypes() {
        return orderTypes;
    }

    public boolean isIcebergAllowed() {
        return icebergAllowed;
    }

    public boolean isOcoAllowed() {
        return ocoAllowed;
    }

    public boolean isSpotTradingAllowed() {
        return isSpotTradingAllowed;
    }

    public boolean isMarginTradingAllowed() {
        return isMarginTradingAllowed;
    }

    public Map<FilterTypeSymbol, SymbolFilter> getFilters() {
        return filters;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
}
