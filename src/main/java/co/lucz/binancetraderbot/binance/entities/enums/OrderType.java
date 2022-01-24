package co.lucz.binancetraderbot.binance.entities.enums;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public enum OrderType {
    LIMIT,
    MARKET,
    STOP_LOSS,
    STOP_LOSS_LIMIT,
    TAKE_PROFIT,
    TAKE_PROFIT_LIMIT,
    LIMIT_MAKER;

    public static List<OrderType> of(JSONArray jsonArray) {
        List<OrderType> orderTypes = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            String orderTypeString = jsonArray.getString(i);
            OrderType orderType = OrderType.valueOf(orderTypeString);
            orderTypes.add(orderType);
        }
        return orderTypes;
    }
}