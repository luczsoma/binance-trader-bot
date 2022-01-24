package co.lucz.binancetraderbot.structures;

import java.math.BigDecimal;
import java.time.Instant;

public class PriceInfo {
    private final Instant instant;
    private final BigDecimal price;

    public PriceInfo(BigDecimal price) {
        this.instant = Instant.now();
        this.price = price;
    }

    public Instant getInstant() {
        return instant;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
