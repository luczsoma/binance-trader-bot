export interface EditTradingConfigurationRequest {
  tradingConfigurationId: number;
  symbolId: string;
  tradingStrategyIdentifier: string;
  tradingStrategyConfiguration: string;
}
