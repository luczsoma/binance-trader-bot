package co.lucz.binancetraderbot.binance.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class OrderFill {
    private final BigDecimal price;
    private final BigDecimal qty;
    private final BigDecimal commission;
    private final String commissionAsset;

    private OrderFill(BigDecimal price, BigDecimal qty, BigDecimal commission, String commissionAsset) {
        this.price = price;
        this.qty = qty;
        this.commission = commission;
        this.commissionAsset = commissionAsset;
    }

    public static OrderFill of(JSONObject jsonObject) {
        return new OrderFill(
                new BigDecimal(jsonObject.getString("price")),
                new BigDecimal(jsonObject.getString("qty")),
                new BigDecimal(jsonObject.getString("commission")),
                jsonObject.getString("commissionAsset")
        );
    }

    public static List<OrderFill> of(JSONArray jsonArray) {
        List<OrderFill> orderFills = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject orderFillJson = jsonArray.getJSONObject(i);
            OrderFill orderFill = OrderFill.of(orderFillJson);
            orderFills.add(orderFill);
        }
        return orderFills;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public String getCommissionAsset() {
        return commissionAsset;
    }
}
