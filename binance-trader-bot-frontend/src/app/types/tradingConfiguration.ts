import { TradingStrategy } from './strategies/tradingStrategy';
import { TradingStrategyName } from './tradingStrategyName';

export interface TradingConfiguration {
  symbol: string;
  strategyName: TradingStrategyName;
  strategy: TradingStrategy;
}
