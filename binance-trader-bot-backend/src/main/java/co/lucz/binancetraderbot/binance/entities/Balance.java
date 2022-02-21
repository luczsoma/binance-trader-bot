package co.lucz.binancetraderbot.binance.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Balance {
    private final String asset;
    private final BigDecimal free;
    private final BigDecimal locked;

    public Balance(String asset, BigDecimal free, BigDecimal locked) {
        this.asset = asset;
        this.free = free;
        this.locked = locked;
    }

    public static Balance of(JSONObject jsonObject) {
        return new Balance(
                jsonObject.getString("asset"),
                new BigDecimal(jsonObject.getString("free")),
                new BigDecimal(jsonObject.getString("locked"))
        );
    }

    public static Map<String, Balance> of(JSONArray jsonArray) {
        Map<String, Balance> balances = new HashMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject balanceJson = jsonArray.getJSONObject(i);
            Balance balance = Balance.of(balanceJson);
            balances.put(balance.getAsset(), balance);
        }
        return balances;
    }

    public String getAsset() {
        return asset;
    }

    public BigDecimal getFree() {
        return free;
    }

    public BigDecimal getLocked() {
        return locked;
    }
}
