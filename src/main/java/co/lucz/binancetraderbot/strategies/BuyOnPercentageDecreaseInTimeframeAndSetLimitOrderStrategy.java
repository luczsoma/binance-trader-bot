package co.lucz.binancetraderbot.strategies;

import co.lucz.binancetraderbot.binance.entities.Balance;
import co.lucz.binancetraderbot.binance.entities.NewOrderResponse;
import co.lucz.binancetraderbot.binance.entities.OrderFill;
import co.lucz.binancetraderbot.helpers.SymbolHelpers;
import co.lucz.binancetraderbot.structures.PriceInfo;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy extends TradingStrategy {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BigDecimal priceDecreaseTriggerRatio;
    private final BigDecimal buySpendAmount;
    private final BigDecimal limitSellPriceRatio;

    private BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy(Duration priceMonitorWindow,
                                                                       BigDecimal priceDecreaseTriggerRatio,
                                                                       BigDecimal buySpendAmount,
                                                                       BigDecimal limitSellPriceRatio) {
        super(priceMonitorWindow);

        if (priceDecreaseTriggerRatio.signum() <= 0) {
            throw new IllegalArgumentException("price decrease trigger ratio must be greater than zero");
        }
        this.priceDecreaseTriggerRatio = priceDecreaseTriggerRatio;

        if (buySpendAmount.signum() <= 0) {
            throw new IllegalArgumentException("buy spend amount must be greater than zero");
        }
        this.buySpendAmount = buySpendAmount;

        if (limitSellPriceRatio.signum() <= 0) {
            throw new IllegalArgumentException("limit sell price ratio must be greater than zero");
        }
        this.limitSellPriceRatio = limitSellPriceRatio;
    }

    public static BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy ofTradingStrategyConfigurationJson(
            JSONObject tradingStrategyConfigurationJson) {
        Duration priceMonitorWindow = Duration.ofSeconds(tradingStrategyConfigurationJson.getLong(
                "priceMonitorWindowSeconds"));
        BigDecimal priceDecreaseTriggerRatio = new BigDecimal(tradingStrategyConfigurationJson.getString(
                "priceDecreaseTriggerRatio"));
        BigDecimal buySpendAmount = new BigDecimal(tradingStrategyConfigurationJson.getString("buySpendAmount"));
        BigDecimal limitSellPriceRatio = new BigDecimal(tradingStrategyConfigurationJson.getString("limitSellPriceRatio"));

        return new BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy(
                priceMonitorWindow,
                priceDecreaseTriggerRatio,
                buySpendAmount,
                limitSellPriceRatio
        );
    }

    @Override
    protected void strategyImpl(String symbolId, List<PriceInfo> priceInfos) {
        if (!this.shouldBuy(priceInfos)) {
            return;
        }

        if (this.hasOpenOrderOnSymbol(symbolId)) {
            return;
        }

        if (!this.hasEnoughQuoteBalance(symbolId)) {
            return;
        }

        this.buyAndSetLimitSell(symbolId);
    }

    private boolean shouldBuy(List<PriceInfo> priceInfos) {
        PriceInfo latest = priceInfos.get(priceInfos.size() - 1);
        PriceInfo max = Collections.max(priceInfos, Comparator.comparing(PriceInfo::getBestAskPrice));
        BigDecimal latestPrice = latest.getBestAskPrice();
        BigDecimal maxPrice = max.getBestAskPrice();
        BigDecimal priceDecreaseRatio = maxPrice.subtract(latestPrice).divide(maxPrice, RoundingMode.HALF_UP);
        boolean shouldBuy = priceDecreaseRatio.compareTo(this.priceDecreaseTriggerRatio) >= 0;

        this.logger.debug("latestPrice = {}", latestPrice.toPlainString());
        this.logger.debug("maxPrice = {}", maxPrice.toPlainString());
        this.logger.debug("priceDecreaseRatio = {}", priceDecreaseRatio.toPlainString());
        this.logger.debug("shouldBuy = {}", shouldBuy);

        return shouldBuy;
    }

    private boolean hasOpenOrderOnSymbol(String symbolId) {
        String symbol = SymbolHelpers.getSymbol(symbolId);
        return this.getBinanceClient().hasOpenOrders(symbol.toUpperCase());
    }

    private boolean hasEnoughQuoteBalance(String symbolId) {
        String quoteAsset = SymbolHelpers.getQuoteAsset(symbolId);
        Balance balance = this.getBinanceClient().getBalance(quoteAsset);
        boolean hasEnoughQuoteBalance = balance != null && balance.getFree().compareTo(this.buySpendAmount) > 0;

        this.logger.debug("hasEnoughQuoteBalance = {}", hasEnoughQuoteBalance);

        return hasEnoughQuoteBalance;
    }

    private void buyAndSetLimitSell(String symbolId) {
        String symbol = SymbolHelpers.getSymbol(symbolId);

        this.logger.debug("symbol = {}", symbol);
        NewOrderResponse marketBuyResponse = this.getBinanceClient().marketBuyBySpend(symbol.toUpperCase(),
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

        NewOrderResponse limitSellResponse = this.getBinanceClient().limitSellByQuantity(symbol.toUpperCase(),
                                                                                         marketBuyResponse.getExecutedQty(),
                                                                                         limitSellPrice);
        this.logger.debug("limitSellResponse.status = {}", limitSellResponse.getStatus().toString());
    }
}
