import { Injectable } from '@angular/core';
import { BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy } from '../types/strategies/buyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy';
import { TradingStrategy } from '../types/strategies/tradingStrategy';
import { TradingStrategyName } from '../types/tradingStrategyName';

@Injectable({
  providedIn: 'root',
})
export class TradingStrategyService {
  public getTradingStrategy(
    tradingStrategyName: TradingStrategyName,
    tradingStrategyConfiguration: string
  ): TradingStrategy {
    switch (tradingStrategyName) {
      case 'BuyOnPercentageDecreaseInTimeframeAndSetLimitOrder':
        return BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy.ofTradingStrategyConfigurationJson(
          tradingStrategyConfiguration
        );
    }
  }
}
