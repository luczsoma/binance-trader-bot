import { TradingStrategyName } from './tradingStrategyName';

export interface GetTradingConfigurationResponse {
  symbolId: string;
  tradingStrategyName: TradingStrategyName;
  tradingStrategyConfiguration: string;
  enabled: boolean;
}
