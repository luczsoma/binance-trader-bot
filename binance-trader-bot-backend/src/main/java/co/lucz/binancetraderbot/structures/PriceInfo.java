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

    public Instant getInstant() {
        return instant;
    }

    public BigDecimal getBestBidPrice() {
        return bestBidPrice;
    }

    public BigDecimal getBestBidQuantity() {
        return bestBidQuantity;
    }

    public BigDecimal getBestAskPrice() {
        return bestAskPrice;
    }

    public BigDecimal getBestAskQuantity() {
        return bestAskQuantity;
    }
}
