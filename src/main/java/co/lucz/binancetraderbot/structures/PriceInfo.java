package co.lucz.binancetraderbot.structures;

import java.math.BigDecimal;
import java.time.Instant;

public final class PriceInfo {
    private final Instant instant = Instant.now();
    private final BigDecimal bestBidPrice;
    private final BigDecimal bestBidQuantity;
    private final BigDecimal bestAskPrice;
    private final BigDecimal bestAskQuantity;

    public PriceInfo(BigDecimal bestBidPrice,
                     BigDecimal bestBidQuantity,
                     BigDecimal bestAskPrice,
                     BigDecimal bestAskQuantity) {
        this.bestBidPrice = bestBidPrice;
        this.bestBidQuantity = bestBidQuantity;
        this.bestAskPrice = bestAskPrice;
        this.bestAskQuantity = bestAskQuantity;
    }

    public final Instant getInstant() {
        return instant;
    }

    public final BigDecimal getBestBidPrice() {
        return bestBidPrice;
    }

    public final BigDecimal getBestBidQuantity() {
        return bestBidQuantity;
    }

    public final BigDecimal getBestAskPrice() {
        return bestAskPrice;
    }

    public final BigDecimal getBestAskQuantity() {
        return bestAskQuantity;
    }
}
