import { TradingStrategy } from './strategies/tradingStrategy';

export interface TradingConfiguration {
  symbol: string;
  strategy: TradingStrategy;
  enabled: boolean;
}
