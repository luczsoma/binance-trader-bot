import { TradingStrategyName } from './tradingStrategyName';

export interface EditTradingConfigurationRequest {
  symbolId: string;
  tradingStrategyName: TradingStrategyName;
  tradingStrategyConfiguration: string;
  enabled: boolean;
}
