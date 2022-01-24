package co.lucz.binancetraderbot.strategies;

import co.lucz.binancetraderbot.binance.BinanceClient;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class TradingStrategy {
    private final String baseAsset;
    private final String quoteAsset;
    private final String symbol;
    private final AtomicBoolean tradeLock;

    private BinanceClient binanceClient;

    public TradingStrategy(String baseAsset, String quoteAsset, AtomicBoolean tradeLock) {
        this.baseAsset = baseAsset.toLowerCase();
        this.quoteAsset = quoteAsset.toLowerCase();
        this.symbol = this.baseAsset + this.quoteAsset;
        this.tradeLock = tradeLock;
    }

    public String getBaseAsset() {
        return baseAsset;
    }

    public String getQuoteAsset() {
        return quoteAsset;
    }

    public String getSymbol() {
        return symbol;
    }

    protected final BinanceClient getBinanceClient() {
        return binanceClient;
    }

    public final void setBinanceClient(BinanceClient binanceTradeClient) {
        this.binanceClient = binanceTradeClient;
    }

    public abstract void handlePriceUpdate(BigDecimal bestBidPrice,
                                           BigDecimal bestBidQuantity,
                                           BigDecimal bestAskPrice,
                                           BigDecimal bestAskQuantity);

    protected final boolean acquireTradeLock() {
        return this.tradeLock.compareAndSet(false, true);
    }

    protected final void releaseTradeLock() {
        this.tradeLock.set(false);
    }
}
