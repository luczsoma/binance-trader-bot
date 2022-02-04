package co.lucz.binancetraderbot.strategies;

import co.lucz.binancetraderbot.binance.BinanceClient;
import co.lucz.binancetraderbot.structures.PriceInfo;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public abstract class TradingStrategy {
    private final Duration priceMonitorWindow;
    private BinanceClient binanceClient;

    protected TradingStrategy(Duration priceMonitorWindow) {
        if (priceMonitorWindow.isZero() || priceMonitorWindow.isNegative()) {
            throw new IllegalArgumentException("price monitor window must be greater than zero");
        }
        this.priceMonitorWindow = priceMonitorWindow;
    }

    public final Duration getPriceMonitorWindow() {
        return this.priceMonitorWindow;
    }

    protected final BinanceClient getBinanceClient() {
        return binanceClient;
    }

    public final void setBinanceClient(BinanceClient binanceTradeClient) {
        this.binanceClient = binanceTradeClient;
    }

    public final void act(String symbolId, List<PriceInfo> priceInfos) {
        this.strategyImpl(symbolId, this.filterExpiredPriceInfos(priceInfos));
    }

    protected abstract void strategyImpl(String symbolId, List<PriceInfo> priceInfosWithinMonitorWindow);

    private List<PriceInfo> filterExpiredPriceInfos(List<PriceInfo> priceInfos) {
        Instant monitorWindowStart = Instant.now().minus(this.priceMonitorWindow);
        List<PriceInfo> filteredPriceInfos = new ArrayList<>(priceInfos);
        filteredPriceInfos.removeIf(priceInfo -> priceInfo.getInstant().isBefore(monitorWindowStart));
        return filteredPriceInfos;
    }
}
