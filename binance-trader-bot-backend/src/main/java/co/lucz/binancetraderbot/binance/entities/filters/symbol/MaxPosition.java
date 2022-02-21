package co.lucz.binancetraderbot.binance.entities.filters.symbol;

import co.lucz.binancetraderbot.binance.entities.enums.FilterTypeSymbol;
import co.lucz.binancetraderbot.binance.entities.filters.SymbolFilter;

import java.math.BigDecimal;

public final class MaxPosition extends SymbolFilter {
    private final BigDecimal maxPosition;

    public MaxPosition(BigDecimal maxPosition) {
        super(FilterTypeSymbol.MAX_POSITION);
        this.maxPosition = maxPosition;
    }

    public BigDecimal getMaxPosition() {
        return maxPosition;
    }
}
