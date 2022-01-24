package co.lucz.binancetraderbot.binance.entities.filters.symbol;

import co.lucz.binancetraderbot.binance.entities.enums.FilterTypeSymbol;
import co.lucz.binancetraderbot.binance.entities.filters.SymbolFilter;

import java.math.BigDecimal;

public final class LotSize extends SymbolFilter {
    private final BigDecimal minQty;
    private final BigDecimal maxQty;
    private final BigDecimal stepSize;

    public LotSize(BigDecimal minQty, BigDecimal maxQty, BigDecimal stepSize) {
        super(FilterTypeSymbol.LOT_SIZE);
        this.minQty = minQty;
        this.maxQty = maxQty;
        this.stepSize = stepSize;
    }

    public BigDecimal getMinQty() {
        return minQty;
    }

    public BigDecimal getMaxQty() {
        return maxQty;
    }

    public BigDecimal getStepSize() {
        return stepSize;
    }
}
