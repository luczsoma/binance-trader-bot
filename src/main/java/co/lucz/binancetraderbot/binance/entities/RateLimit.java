package co.lucz.binancetraderbot.binance.entities;

import co.lucz.binancetraderbot.binance.entities.enums.RateLimitInterval;
import co.lucz.binancetraderbot.binance.entities.enums.RateLimitType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class RateLimit {
    private final RateLimitType rateLimitType;
    private final RateLimitInterval interval;
    private final int intervalNum;
    private final int limit;

    private RateLimit(RateLimitType rateLimitType, RateLimitInterval interval, int intervalNum, int limit) {
        this.rateLimitType = rateLimitType;
        this.interval = interval;
        this.intervalNum = intervalNum;
        this.limit = limit;
    }

    public static RateLimit of(JSONObject jsonObject) {
        return new RateLimit(
                RateLimitType.valueOf(jsonObject.getString("rateLimitType")),
                RateLimitInterval.valueOf(jsonObject.getString("interval")),
                jsonObject.getInt("intervalNum"),
                jsonObject.getInt("limit")
        );
    }

    public static List<RateLimit> of(JSONArray jsonArray) {
        List<RateLimit> rateLimits = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject rateLimitJson = jsonArray.getJSONObject(i);
            RateLimit rateLimit = RateLimit.of(rateLimitJson);
            rateLimits.add(rateLimit);
        }
        return rateLimits;
    }

    public RateLimitType getRateLimitType() {
        return rateLimitType;
    }

    public RateLimitInterval getInterval() {
        return interval;
    }

    public int getIntervalNum() {
        return intervalNum;
    }

    public int getLimit() {
        return limit;
    }
}
