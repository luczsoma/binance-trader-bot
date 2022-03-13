package co.lucz.binancetraderbot.binance;

import co.lucz.binancetraderbot.binance.entities.Balance;
import co.lucz.binancetraderbot.binance.entities.ExchangeInfo;
import co.lucz.binancetraderbot.binance.entities.NewOrderResponse;
import co.lucz.binancetraderbot.binance.entities.OpenOrderResponse;
import co.lucz.binancetraderbot.binance.entities.Symbol;
import co.lucz.binancetraderbot.binance.entities.enums.FilterTypeSymbol;
import co.lucz.binancetraderbot.binance.entities.enums.OrderResponseType;
import co.lucz.binancetraderbot.binance.entities.enums.OrderSide;
import co.lucz.binancetraderbot.binance.entities.enums.OrderType;
import co.lucz.binancetraderbot.binance.entities.enums.TimeInForce;
import co.lucz.binancetraderbot.binance.entities.filters.SymbolFilter;
import co.lucz.binancetraderbot.binance.entities.filters.symbol.LotSize;
import co.lucz.binancetraderbot.binance.entities.filters.symbol.PriceFilter;
import com.binance.connector.client.impl.SpotClientImpl;
import com.binance.connector.client.impl.WebsocketClientImpl;
import com.binance.connector.client.impl.spot.Market;
import com.binance.connector.client.impl.spot.Trade;
import com.binance.connector.client.impl.spot.UserData;
import com.binance.connector.client.impl.spot.Wallet;
import com.binance.connector.client.utils.WebSocketCallback;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class BinanceClient {
    private ExchangeInfo exchangeInfoCache;

    @Value("${binance.rest-api-base-url}")
    private String restApiBaseUrl;

    @Value("${binance.ws-api-base-url}")
    private String wsApiBaseUrl;

    @Value("${binance.api-key}")
    private String apiKey;

    @Value("${binance.secret-key}")
    private String secretKey;

    @Value("${binance.show-limit-usage}")
    private boolean showLimitUsage;

    @Bean
    private SpotClientImpl getBinanceSpotClientImpl() {
        SpotClientImpl spotClient = new SpotClientImpl(this.apiKey, this.secretKey, this.restApiBaseUrl);
        spotClient.setShowLimitUsage(this.showLimitUsage);
        return spotClient;
    }

    @Bean
    private WebsocketClientImpl getBinanceWebsocketClientImpl() {
        return new WebsocketClientImpl(this.wsApiBaseUrl);
    }

    @Bean
    private Market getMarket() {
        return this.getBinanceSpotClientImpl().createMarket();
    }

    @Bean
    private Wallet getWallet() {
        return this.getBinanceSpotClientImpl().createWallet();
    }

    @Bean
    private Trade getTrade() {
        return this.getBinanceSpotClientImpl().createTrade();
    }

    @Bean
    private UserData getUserData() {
        return this.getBinanceSpotClientImpl().createUserData();
    }

    public ExchangeInfo getExchangeInfo(boolean refresh) {
        if (this.exchangeInfoCache == null || refresh) {
            this.exchangeInfoCache = this.getExchangeInfo(Collections.emptySet());
        }
        return exchangeInfoCache;
    }

    public int bookTicker(String symbol, WebSocketCallback callback) {
        return this.getBinanceWebsocketClientImpl().bookTicker(symbol, callback);
    }

    public void closeWebsocketConnection(int connectionId) {
        this.getBinanceWebsocketClientImpl().closeConnection(connectionId);
    }

    public void closeAllWebsocketConnections() {
        this.getBinanceWebsocketClientImpl().closeAllConnections();
    }

    public boolean hasOpenOrders() {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        String response = this.getTrade().getOpenOrders(parameters);
        JSONArray openOrders = new JSONArray(response);
        return openOrders.length() > 0;
    }

    public boolean hasOpenOrders(String symbol) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);
        String response = this.getTrade().getOpenOrders(parameters);
        JSONArray openOrdersOnSymbol = new JSONArray(response);
        return openOrdersOnSymbol.length() > 0;
    }

    public NewOrderResponse marketBuyBySpend(String symbol, BigDecimal amountToSpend) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);
        parameters.put("side", OrderSide.BUY.toString());
        parameters.put("type", OrderType.MARKET.toString());
        parameters.put("quoteOrderQty", this.validateQuoteOrderQuantity(symbol, amountToSpend));
        parameters.put("newOrderRespType", OrderResponseType.FULL.toString());
        String response = this.getTrade().newOrder(parameters);
        JSONObject responseJson = new JSONObject(response);
        return NewOrderResponse.of(responseJson);
    }

    public NewOrderResponse limitSellByQuantity(String symbol, BigDecimal quantity, BigDecimal price) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);
        parameters.put("side", OrderSide.SELL.toString());
        parameters.put("type", OrderType.LIMIT.toString());
        parameters.put("quantity", this.validateQuantity(symbol, quantity));
        parameters.put("price", this.validatePrice(symbol, price));
        parameters.put("timeInForce", TimeInForce.GTC.toString());
        parameters.put("newOrderRespType", OrderResponseType.FULL.toString());
        String response = this.getTrade().newOrder(parameters);
        JSONObject responseJson = new JSONObject(response);
        return NewOrderResponse.of(responseJson);
    }

    public Map<String, Balance> getBalances() {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        String response = this.getTrade().account(parameters);
        JSONObject responseJson = new JSONObject(response);
        JSONArray balancesJson = responseJson.getJSONArray("balances");
        return Balance.of(balancesJson);
    }

    public Balance getBalance(String asset) {
        Map<String, Balance> balances = this.getBalances();
        return balances.get(asset);
    }

    public List<OpenOrderResponse> getOpenOrders() {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        String response = this.getTrade().getOpenOrders(parameters);
        JSONArray openOrders = new JSONArray(response);
        return OpenOrderResponse.of(openOrders);
    }

    private ExchangeInfo getExchangeInfo(Set<String> symbols) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        if (!symbols.isEmpty()) {
            parameters.put("symbols", new ArrayList<>(symbols));
        }
        String response = this.getMarket().exchangeInfo(parameters);
        JSONObject responseJson = new JSONObject(response);
        return ExchangeInfo.of(responseJson);
    }

    private String validateQuantity(String symbol, BigDecimal quantity) {
        Symbol symbolInfo = this.exchangeInfoCache.getSymbols().get(symbol);

        BigDecimal scaledQuantity = quantity.setScale(symbolInfo.getBaseAssetPrecision(), RoundingMode.HALF_UP);

        Map<FilterTypeSymbol, SymbolFilter> filters = symbolInfo.getFilters();
        LotSize lotSize = (LotSize) filters.get(FilterTypeSymbol.LOT_SIZE);
        BigDecimal minQuantity = lotSize.getMinQty();
        BigDecimal maxQuantity = lotSize.getMaxQty();
        BigDecimal stepSize = lotSize.getStepSize();

        if (scaledQuantity.compareTo(minQuantity) < 0) {
            return minQuantity.toPlainString();
        }

        if (scaledQuantity.compareTo(maxQuantity) > 0) {
            return maxQuantity.toPlainString();
        }

        BigDecimal multiplier = scaledQuantity.subtract(minQuantity).divideToIntegralValue(stepSize);
        BigDecimal validatedQuantity = minQuantity.add(stepSize.multiply(multiplier));

        return validatedQuantity.toPlainString();
    }

    private String validateQuoteOrderQuantity(String symbol, BigDecimal amountToSpend) {
        Symbol symbolInfo = this.exchangeInfoCache.getSymbols().get(symbol);

        // We do not validate against LOT_SIZE here, since it would require the current market price.
        // It's unlikely to run into minQty or maxQty issues, and stepSize is handled by Binance.

        return amountToSpend.setScale(symbolInfo.getQuoteAssetPrecision(), RoundingMode.HALF_UP).toPlainString();
    }

    private String validatePrice(String symbol, BigDecimal price) {
        Symbol symbolInfo = this.exchangeInfoCache.getSymbols().get(symbol);

        BigDecimal scaledPrice = price.setScale(symbolInfo.getQuoteAssetPrecision(), RoundingMode.HALF_UP);

        Map<FilterTypeSymbol, SymbolFilter> filters = symbolInfo.getFilters();
        PriceFilter priceFilter = (PriceFilter) filters.get(FilterTypeSymbol.PRICE_FILTER);
        BigDecimal minPrice = priceFilter.getMinPrice();
        BigDecimal maxPrice = priceFilter.getMaxPrice();
        BigDecimal tickSize = priceFilter.getTickSize();

        if (scaledPrice.compareTo(minPrice) < 0) {
            return minPrice.toPlainString();
        }

        if (scaledPrice.compareTo(maxPrice) > 0) {
            return maxPrice.toPlainString();
        }

        BigDecimal multiplier = scaledPrice.subtract(minPrice).divideToIntegralValue(tickSize);
        BigDecimal validatedPrice = minPrice.add(tickSize.multiply(multiplier));
        return validatedPrice.toPlainString();
    }
}
