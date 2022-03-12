import { TradingStrategyName } from '../tradingStrategyName';

export abstract class TradingStrategy {
  public readonly priceMonitorWindowSeconds: number;

  protected constructor(priceMonitorWindowsSeconds: number) {
    this.priceMonitorWindowSeconds = priceMonitorWindowsSeconds;
  }

  public abstract toTradingStrategyConfigurationJson(): string;
  public abstract getTradingStrategyName(): TradingStrategyName;
  public abstract getFriendlyDescription(): string;
}
