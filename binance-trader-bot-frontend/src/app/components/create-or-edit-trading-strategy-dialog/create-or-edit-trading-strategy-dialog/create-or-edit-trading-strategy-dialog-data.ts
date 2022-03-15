import { TradingConfiguration } from 'src/app/types/tradingConfiguration';
import { TradingStrategyName } from 'src/app/types/tradingStrategyName';

export interface CreateOrEditTradingStrategyDialogData {
  state: 'create' | 'editOne' | 'editMultiple';
  tradingConfiguration?: TradingConfiguration;
  tradableSymbols: Set<string>;
  tradingStrategies: Set<TradingStrategyName>;
}
