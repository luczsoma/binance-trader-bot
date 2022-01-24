package co.lucz.binancetraderbot.binance.entities.enums;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public enum Permission {
    SPOT,
    MARGIN,
    LEVERAGED,
    TRD_GRP_002;

    public static List<Permission> of(JSONArray jsonArray) {
        List<Permission> permissions = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            String permissionString = jsonArray.getString(i);
            Permission permission = Permission.valueOf(permissionString);
            permissions.add(permission);
        }
        return permissions;
    }
}
