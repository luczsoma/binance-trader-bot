import { TradingStrategyName } from './tradingStrategyName';

export interface CreateTradingConfigurationRequest {
  symbolId: string;
  tradingStrategyName: TradingStrategyName;
  tradingStrategyConfiguration: string;
  enabled: boolean;
}
