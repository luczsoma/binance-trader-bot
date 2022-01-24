package co.lucz.binancetraderbot.strategies;

import co.lucz.binancetraderbot.binance.entities.OrderFill;
import co.lucz.binancetraderbot.binance.entities.OrderResponse;
import co.lucz.binancetraderbot.structures.PriceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy extends TradingStrategy {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Duration priceDecreaseMonitorWindow;
    private final BigDecimal priceDecreaseTriggerRatio;
    private final BigDecimal buySpendAmount;
    private final BigDecimal limitSellPriceRatio;

    private final List<PriceInfo> priceInfos = new ArrayList<>();

    public BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy(String baseAsset,
                                                                      String quoteAsset,
                                                                      AtomicBoolean tradeLock,
                                                                      Duration priceDecreaseMonitorWindow,
                                                                      BigDecimal priceDecreaseTriggerRatio,
                                                                      BigDecimal buySpendAmount,
                                                                      BigDecimal limitSellPriceRatio) {
        super(baseAsset, quoteAsset, tradeLock);
        this.priceDecreaseMonitorWindow = priceDecreaseMonitorWindow;
        this.priceDecreaseTriggerRatio = priceDecreaseTriggerRatio;
        this.buySpendAmount = buySpendAmount;
        this.limitSellPriceRatio = limitSellPriceRatio;
    }

    @Override
    public void handlePriceUpdate(BigDecimal bestBidPrice,
                                  BigDecimal bestBidQuantity,
                                  BigDecimal bestAskPrice,
                                  BigDecimal bestAskQuantity) {
        this.storeLatestPrice(bestAskPrice);
        this.removeExpiredPrices();
        this.buyIfNeeded();
    }

    private void storeLatestPrice(BigDecimal price) {
        this.priceInfos.add(new PriceInfo(price));
    }

    private void removeExpiredPrices() {
        Instant monitorWindowStart = Instant.now().minus(this.priceDecreaseMonitorWindow);
        this.priceInfos.removeIf(priceInfo -> priceInfo.getInstant().isBefore(monitorWindowStart));
    }

    private void buyIfNeeded() {
        if (!this.shouldBuy()) {
            return;
        }

        if (!this.acquireTradeLock()) {
            return;
        }

        if (this.hasOpenOrderOnSymbol()) {
            return;
        }

        if (!this.hasEnoughQuoteBalance()) {
            return;
        }

        this.buyAndSetLimitSell();
        this.clearPriceInfos();

        this.logger.debug("priceInfos.size = {}", this.priceInfos.size());

        this.releaseTradeLock();
    }

    private boolean shouldBuy() {
        PriceInfo latest = this.priceInfos.get(this.priceInfos.size() - 1);
        PriceInfo max = Collections.max(this.priceInfos, Comparator.comparing(PriceInfo::getPrice));
        BigDecimal latestPrice = latest.getPrice();
        BigDecimal maxPrice = max.getPrice();
        BigDecimal priceDecreaseRatio = maxPrice.subtract(latestPrice).divide(maxPrice, RoundingMode.HALF_UP);
        boolean shouldBuy = priceDecreaseRatio.compareTo(this.priceDecreaseTriggerRatio) >= 0;

        this.logger.debug("latestPrice = {}", latestPrice.toPlainString());
        this.logger.debug("maxPrice = {}", maxPrice.toPlainString());
        this.logger.debug("priceDecreaseRatio = {}", priceDecreaseRatio.toPlainString());
        this.logger.debug("shouldBuy = {}", shouldBuy);

        return shouldBuy;
    }

    private boolean hasOpenOrderOnSymbol() {
        return this.getBinanceClient().hasOpenOrders(this.getSymbol().toUpperCase());
    }

    private boolean hasEnoughQuoteBalance() {
        String quoteAsset = this.getQuoteAsset();
        BigDecimal freeBalance = this.getBinanceClient().getFreeBalance(quoteAsset);
        boolean hasEnoughQuoteBalance = freeBalance.compareTo(this.buySpendAmount) > 0;

        this.logger.debug("{} freeBalance = {}", quoteAsset, freeBalance);
        this.logger.debug("hasEnoughQuoteBalance = {}", hasEnoughQuoteBalance);

        return hasEnoughQuoteBalance;
    }

    private void buyAndSetLimitSell() {
        this.logger.debug("symbol = {}", this.getSymbol());
        OrderResponse marketBuyResponse = this.getBinanceClient().marketBuyBySpend(this.getSymbol().toUpperCase(),
                                                                                   this.buySpendAmount);
        this.logger.debug("marketBuyResponse.status = {}", marketBuyResponse.getStatus().toString());
        this.logger.debug("marketBuyResponse.executedQuantity = {}", marketBuyResponse.getExecutedQty());

        OrderFill minFilledPriceOrder = Collections.min(marketBuyResponse.getFills(),
                                                        Comparator.comparing(OrderFill::getPrice));
        BigDecimal minFilledPrice = minFilledPriceOrder.getPrice();
        this.logger.debug("minFilledPrice = {}", minFilledPrice);

        BigDecimal limitSellPriceMultiplier = new BigDecimal(1).add(this.limitSellPriceRatio);
        this.logger.debug("limitSellPriceMultiplier = {}", limitSellPriceMultiplier);
        this.logger.debug("limitSellPriceMultiplier.scale = {}", limitSellPriceMultiplier.scale());

        BigDecimal limitSellPrice = minFilledPrice.multiply(limitSellPriceMultiplier);
        this.logger.debug("limitSellPrice = {}", limitSellPrice);

        OrderResponse limitSellResponse = this.getBinanceClient().limitSellByQuantity(this.getSymbol().toUpperCase(),
                                                                                      marketBuyResponse.getExecutedQty(),
                                                                                      limitSellPrice);
        this.logger.debug("limitSellResponse.status = {}", limitSellResponse.getStatus().toString());
    }

    private void clearPriceInfos() {
        this.priceInfos.clear();
    }
}
