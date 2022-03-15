import { Duration } from 'luxon';
import { TradingStrategy } from './tradingStrategy';

export class BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy extends TradingStrategy {
  public static readonly NAME =
    'BuyOnPercentageDecreaseInTimeframeAndSetLimitOrder';
  public static readonly DESCRIPTION =
    'Megadott árfolyamfigyelési időablakon belüli megadott %-os árfolyamcsökkenés esetén vásárlás megadott értékben, majd megadott %-os nyereséggel limitáras eladási megbízás feladása';

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

  public toTradingStrategyConfigurationJson(): string {
    return JSON.stringify({
      priceMonitorWindowSeconds: this.priceMonitorWindowSeconds,
      priceDecreaseTriggerRatio: this.priceDecreaseTriggerRatio.toString(10),
      buySpendAmount: this.buySpendAmount.toString(10),
      limitSellPriceRatio: this.limitSellPriceRatio.toString(10),
    });
  }

  public getFriendlyDescription(): string {
    const friendlyPriceMonitorWindowDuration = Duration.fromObject(
      { seconds: this.priceMonitorWindowSeconds },
      { locale: 'hu' }
    )
      .shiftTo('days', 'hours', 'minutes', 'seconds')
      .toHuman({ listStyle: 'long' });
    const priceDecreaseTriggerPercentage = this.priceDecreaseTriggerRatio * 100;
    const limitSellPricePercentage = this.limitSellPriceRatio * 100;
    return `${friendlyPriceMonitorWindowDuration} időn belüli ${priceDecreaseTriggerPercentage}%-os árfolyamcsökkenés esetén ${this.buySpendAmount} egység vásárlása, majd ${limitSellPricePercentage}%-os nyereséggel limitáras eladási megbízás feladása`;
  }
}
