package co.lucz.binancetraderbot.binance.entities;

import co.lucz.binancetraderbot.binance.entities.enums.OrderSide;
import co.lucz.binancetraderbot.binance.entities.enums.OrderStatus;
import co.lucz.binancetraderbot.binance.entities.enums.OrderType;
import co.lucz.binancetraderbot.binance.entities.enums.TimeInForce;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;

public final class OrderResponse {
    private final String symbol;
    private final long orderId;
    private final long orderListId;
    private final String clientOrderId;
    private final long transactTime;
    private final BigDecimal price;
    private final BigDecimal origQty;
    private final BigDecimal executedQty;
    private final BigDecimal cummulativeQuoteQty;
    private final OrderStatus status;
    private final TimeInForce timeInForce;
    private final OrderSide side;
    private final OrderType type;
    private final List<OrderFill> fills;

    private OrderResponse(String symbol,
                          long orderId,
                          long orderListId,
                          String clientOrderId,
                          long transactTime,
                          BigDecimal price,
                          BigDecimal origQty,
                          BigDecimal executedQty,
                          BigDecimal cummulativeQuoteQty,
                          OrderStatus status,
                          TimeInForce timeInForce,
                          OrderType type,
                          OrderSide side,
                          List<OrderFill> fills) {
        this.symbol = symbol;
        this.orderId = orderId;
        this.orderListId = orderListId;
        this.clientOrderId = clientOrderId;
        this.transactTime = transactTime;
        this.price = price;
        this.origQty = origQty;
        this.executedQty = executedQty;
        this.cummulativeQuoteQty = cummulativeQuoteQty;
        this.status = status;
        this.timeInForce = timeInForce;
        this.type = type;
        this.side = side;
        this.fills = fills;
    }

    public static OrderResponse of(JSONObject jsonObject) {
        return new OrderResponse(
                jsonObject.getString("symbol"),
                jsonObject.getLong("orderId"),
                jsonObject.getLong("orderListId"),
                jsonObject.getString("clientOrderId"),
                jsonObject.getLong("transactTime"),
                new BigDecimal(jsonObject.getString("price")),
                new BigDecimal(jsonObject.getString("origQty")),
                new BigDecimal(jsonObject.getString("executedQty")),
                new BigDecimal(jsonObject.getString("cummulativeQuoteQty")),
                OrderStatus.valueOf(jsonObject.getString("status")),
                TimeInForce.valueOf(jsonObject.getString("timeInForce")),
                OrderType.valueOf(jsonObject.getString("type")),
                OrderSide.valueOf(jsonObject.getString("side")),
                OrderFill.of(jsonObject.getJSONArray("fills"))
        );
    }

    public String getSymbol() {
        return symbol;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getOrderListId() {
        return orderListId;
    }

    public String getClientOrderId() {
        return clientOrderId;
    }

    public long getTransactTime() {
        return transactTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getOrigQty() {
        return origQty;
    }

    public BigDecimal getExecutedQty() {
        return executedQty;
    }

    public BigDecimal getCummulativeQuoteQty() {
        return cummulativeQuoteQty;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public TimeInForce getTimeInForce() {
        return timeInForce;
    }

    public OrderType getType() {
        return type;
    }

    public OrderSide getSide() {
        return side;
    }

    public List<OrderFill> getFills() {
        return fills;
    }
}
