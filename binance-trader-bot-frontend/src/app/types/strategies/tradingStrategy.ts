export abstract class TradingStrategy {
  public readonly priceMonitorWindowSeconds: number;

  protected constructor(priceMonitorWindowsSeconds: number) {
    this.priceMonitorWindowSeconds = priceMonitorWindowsSeconds;
  }

  public abstract getFriendlyDescription(): string;
}
