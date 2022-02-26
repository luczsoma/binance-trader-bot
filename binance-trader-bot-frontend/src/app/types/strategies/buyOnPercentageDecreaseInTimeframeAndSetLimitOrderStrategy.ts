import { Duration } from 'luxon';
import { TradingStrategy } from './tradingStrategy';

export class BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy extends TradingStrategy {
  public readonly priceDecreaseTriggerRatio: number;
  public readonly buySpendAmount: number;
  public readonly limitSellPriceRatio: number;

  public constructor(
    priceMonitorWindowSeconds: number,
    priceDecreaseTriggerRatio: number,
    buySpendAmount: number,
    limitSellPriceRatio: number
  ) {
    super(priceMonitorWindowSeconds);

    this.priceDecreaseTriggerRatio = priceDecreaseTriggerRatio;
    this.buySpendAmount = buySpendAmount;
    this.limitSellPriceRatio = limitSellPriceRatio;
  }

  public static ofTradingStrategyConfigurationJson(
    tradingStrategyConfigurationJson: string
  ): BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy {
    const tradingStrategyConfiguration = JSON.parse(
      tradingStrategyConfigurationJson
    ) as BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy;
    return new BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy(
      tradingStrategyConfiguration.priceMonitorWindowSeconds,
      tradingStrategyConfiguration.priceDecreaseTriggerRatio,
      tradingStrategyConfiguration.buySpendAmount,
      tradingStrategyConfiguration.limitSellPriceRatio
    );
  }

  public getFriendlyDescription(): string {
    const friendlyPriceMonitorWindowSeconds = Duration.fromObject(
      { seconds: this.priceMonitorWindowSeconds },
      { locale: 'hu' }
    )
      .shiftTo('days', 'hours', 'minutes', 'seconds')
      .toHuman({ listStyle: 'short' });
    const priceDecreaseTriggerPercentage = this.priceDecreaseTriggerRatio * 100;
    const limitSellPricePercentage = this.limitSellPriceRatio * 100;
    return `${friendlyPriceMonitorWindowSeconds} időn belüli ${priceDecreaseTriggerPercentage}%-os árfolyamcsökkenés esetén ${this.buySpendAmount} egység vétele, majd ${limitSellPricePercentage}%-os nyereséggel limitáras eladási megbízás feladása`;
  }
}
