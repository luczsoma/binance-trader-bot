import { Injectable } from '@angular/core';
import { BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy } from '../types/strategies/buyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy';
import { TradingStrategy } from '../types/strategies/tradingStrategy';
import { TradingStrategyName } from '../types/tradingStrategyName';

@Injectable({
  providedIn: 'root',
})
export class TradingStrategyService {
  public getTradingStrategies(): Set<TradingStrategyName> {
    return new Set([
      BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy.NAME,
    ]);
  }

  public getTradingStrategyName(
    tradingStrategy: TradingStrategy
  ): TradingStrategyName {
    if (
      tradingStrategy instanceof
      BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy
    ) {
      return BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy.NAME;
    }

    throw new Error('unknown trading strategy');
  }

  public getTradingStrategyDesciption(
    tradingStrategyName: TradingStrategyName
  ): string {
    switch (tradingStrategyName) {
      case BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy.NAME:
        return BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy.DESCRIPTION;
    }
  }

  public getTradingStrategy(
    tradingStrategyName: TradingStrategyName,
    tradingStrategyConfiguration: string
  ): TradingStrategy {
    switch (tradingStrategyName) {
      case BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy.NAME:
        return BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy.ofTradingStrategyConfigurationJson(
          tradingStrategyConfiguration
        );
    }
  }
}
