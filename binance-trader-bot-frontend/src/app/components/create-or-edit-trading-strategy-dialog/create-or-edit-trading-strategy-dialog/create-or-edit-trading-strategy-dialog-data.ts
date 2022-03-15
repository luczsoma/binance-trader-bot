import { TradingConfiguration } from 'src/app/types/tradingConfiguration';
import { TradingStrategyName } from 'src/app/types/tradingStrategyName';

export interface CreateOrEditTradingStrategyDialogData {
  tradingConfiguration?: TradingConfiguration;
  tradableSymbols: Set<string>;
  tradingStrategies: Set<TradingStrategyName>;
}
