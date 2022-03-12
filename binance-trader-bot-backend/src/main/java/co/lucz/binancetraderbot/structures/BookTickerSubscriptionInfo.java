package co.lucz.binancetraderbot.structures;

import java.time.Instant;

public class BookTickerSubscriptionInfo {
    private final int connectionId;
    private final Instant subscribedAt;

    public BookTickerSubscriptionInfo(int connectionId, Instant subscribedAt) {
        this.connectionId = connectionId;
        this.subscribedAt = subscribedAt;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public Instant getSubscribedAt() {
        return subscribedAt;
    }
}
