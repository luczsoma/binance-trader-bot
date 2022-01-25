package co.lucz.binancetraderbot.services;

import co.lucz.binancetraderbot.binance.BinanceClient;
import co.lucz.binancetraderbot.strategies.BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy;
import co.lucz.binancetraderbot.strategies.TradingStrategy;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class TraderService {
    @Autowired
    private BinanceClient binanceClient;

    private final AtomicBoolean tradeLock = new AtomicBoolean();

    private final Set<TradingStrategy> tradingStrategies = Set.of(
            new BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy("BTC",
                                                                           "USDT",
                                                                           this.tradeLock,
                                                                           Duration.ofDays(1),
                                                                           new BigDecimal("0.1"),
                                                                           new BigDecimal(100),
                                                                           new BigDecimal("0.1"))
    );

    @PostConstruct
    private void doTrading() {
        Set<String> symbols = this.tradingStrategies.stream()
                .map(TradingStrategy::getSymbol)
                .map(String::toUpperCase)
                .collect(Collectors.toUnmodifiableSet());
        this.binanceClient.cacheExchangeInfo(symbols);
        this.subscribeToSymbols();
    }

    @Scheduled(fixedDelay = 23, timeUnit = TimeUnit.HOURS)
    private void renewBookTickerSubscriptions() {
        this.binanceClient.closeAllWebsocketConnections();
        this.subscribeToSymbols();
    }

    private void subscribeToSymbols() {
        this.tradingStrategies.forEach(strategy -> {
            strategy.setBinanceClient(this.binanceClient);
            this.binanceClient.bookTicker(strategy.getSymbol().toLowerCase(), response -> {
                JSONObject responseJson = new JSONObject(response);
                BigDecimal bestBidPrice = new BigDecimal(responseJson.getString("b"));
                BigDecimal bestBidQuantity = new BigDecimal(responseJson.getString("B"));
                BigDecimal bestAskPrice = new BigDecimal(responseJson.getString("a"));
                BigDecimal bestAskQuantity = new BigDecimal(responseJson.getString("A"));
                strategy.handlePriceUpdate(bestBidPrice, bestBidQuantity, bestAskPrice, bestAskQuantity);
            });
        });
    }
}
