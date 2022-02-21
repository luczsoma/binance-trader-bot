package co.lucz.binancetraderbot.binance.entities;

import co.lucz.binancetraderbot.binance.entities.enums.OrderSide;
import co.lucz.binancetraderbot.binance.entities.enums.OrderStatus;
import co.lucz.binancetraderbot.binance.entities.enums.OrderType;
import co.lucz.binancetraderbot.binance.entities.enums.TimeInForce;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class OpenOrderResponse {
    private final String symbol;
    private final long orderId;
    private final long orderListId;
    private final String clientOrderId;
    private final BigDecimal price;
    private final BigDecimal origQty;
    private final BigDecimal executedQty;
    private final BigDecimal cummulativeQuoteQty;
    private final OrderStatus status;
    private final TimeInForce timeInForce;
    private final OrderType type;
    private final OrderSide side;
    private final BigDecimal stopPrice;
    private final BigDecimal icebergQty;
    private final long time;
    private final long updateTime;
    private final boolean isWorking;
    private final BigDecimal origQuoteOrderQty;

    private OpenOrderResponse(String symbol,
                              long orderId,
                              long orderListId,
                              String clientOrderId,
                              BigDecimal price,
                              BigDecimal origQty,
                              BigDecimal executedQty,
                              BigDecimal cummulativeQuoteQty,
                              OrderStatus status,
                              TimeInForce timeInForce,
                              OrderType type,
                              OrderSide side,
                              BigDecimal stopPrice,
                              BigDecimal icebergQty,
                              long time,
                              long updateTime, boolean isWorking, BigDecimal origQuoteOrderQty) {
        this.symbol = symbol;
        this.orderId = orderId;
        this.orderListId = orderListId;
        this.clientOrderId = clientOrderId;
        this.price = price;
        this.origQty = origQty;
        this.executedQty = executedQty;
        this.cummulativeQuoteQty = cummulativeQuoteQty;
        this.status = status;
        this.timeInForce = timeInForce;
        this.type = type;
        this.side = side;
        this.stopPrice = stopPrice;
        this.icebergQty = icebergQty;
        this.time = time;
        this.updateTime = updateTime;
        this.isWorking = isWorking;
        this.origQuoteOrderQty = origQuoteOrderQty;
    }

    public static OpenOrderResponse of(JSONObject jsonObject) {
        return new OpenOrderResponse(
                jsonObject.getString("symbol"),
                jsonObject.getLong("orderId"),
                jsonObject.getLong("orderListId"),
                jsonObject.getString("clientOrderId"),
                new BigDecimal(jsonObject.getString("price")),
                new BigDecimal(jsonObject.getString("origQty")),
                new BigDecimal(jsonObject.getString("executedQty")),
                new BigDecimal(jsonObject.getString("cummulativeQuoteQty")),
                OrderStatus.valueOf(jsonObject.getString("status")),
                TimeInForce.valueOf(jsonObject.getString("timeInForce")),
                OrderType.valueOf(jsonObject.getString("type")),
                OrderSide.valueOf(jsonObject.getString("side")),
                new BigDecimal(jsonObject.getString("stopPrice")),
                new BigDecimal(jsonObject.getString("icebergQty")),
                jsonObject.getLong("time"),
                jsonObject.getLong("updateTime"),
                jsonObject.getBoolean("isWorking"),
                new BigDecimal(jsonObject.getString("origQuoteOrderQty"))
        );
    }

    public static List<OpenOrderResponse> of(JSONArray jsonArray) {
        List<OpenOrderResponse> openOrderResponses = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject openOrderResponseJson = jsonArray.getJSONObject(i);
            OpenOrderResponse openOrderResponse = OpenOrderResponse.of(openOrderResponseJson);
            openOrderResponses.add(openOrderResponse);
        }
        return openOrderResponses;
    }
}
