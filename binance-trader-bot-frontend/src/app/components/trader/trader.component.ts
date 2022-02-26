import { Component, OnInit } from '@angular/core';
import { MatSlideToggleChange } from '@angular/material/slide-toggle';
import { ApiService } from 'src/app/services/api.service';
import { LoginService } from 'src/app/services/login.service';
import { SymbolService } from 'src/app/services/symbol.service';
import { TradingStrategyService } from 'src/app/services/trading-strategy.service';
import { Balance } from 'src/app/types/balance';
import { Order } from 'src/app/types/order';
import { BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy } from 'src/app/types/strategies/buyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy';
import { TradingConfiguration } from 'src/app/types/tradingConfiguration';

@Component({
  selector: 'binance-trader-bot-frontend-trader',
  templateUrl: './trader.component.html',
  styleUrls: ['./trader.component.scss'],
})
export class TraderComponent implements OnInit {
  private _loaded = false;

  private _balances: Balance[] = [];
  private _openOrders: Order[] = [];
  private _isTradingEnabled = true;
  private _tradingConfigurations: TradingConfiguration[] = [];

  public get loaded(): boolean {
    return this._loaded;
  }

  public get balances(): Balance[] {
    return this._balances;
  }

  public get hasBalances(): boolean {
    return this.balances.length > 0;
  }

  public get balancesColumns(): string[] {
    return ['asset', 'free', 'locked'];
  }

  public get openOrders(): Order[] {
    return this._openOrders;
  }

  public get hasOpenOrders(): boolean {
    return this.openOrders.length > 0;
  }

  public get openOrdersColumns(): string[] {
    return ['pair', 'quantity', 'price'];
  }

  public get isTradingEnabled(): boolean {
    return this._isTradingEnabled;
  }

  public get tradingConfigurations(): TradingConfiguration[] {
    return this._tradingConfigurations;
  }

  public get hasTradingConfigurations(): boolean {
    return this.tradingConfigurations.length > 0;
  }

  public get tradingConfigurationsColumns(): string[] {
    return ['symbol', 'strategy'];
  }

  public constructor(
    private readonly loginService: LoginService,
    private readonly apiService: ApiService,
    private readonly symbolService: SymbolService,
    private readonly tradingStrategyService: TradingStrategyService
  ) {}

  public async ngOnInit(): Promise<void> {
    await Promise.all([
      this.refreshBalances(),
      this.refreshOpenOrders(),
      this.refreshIsTradingEnabled(),
      this.refreshTradingConfigurations(),
    ]);

    this._loaded = true;
  }

  public async refreshData(): Promise<void> {
    this._loaded = false;

    await Promise.all([
      this.refreshBalances(),
      this.refreshOpenOrders(),
      this.refreshIsTradingEnabled(),
      this.refreshTradingConfigurations(),
    ]);

    this._loaded = true;
  }

  public async logout(): Promise<void> {
    await this.loginService.logout();
  }

  public async toggleGlobalTradingLock(
    event: MatSlideToggleChange
  ): Promise<void> {
    const lockTargetValue = !event.checked;
    await this.loginService.withLoginErrorHandling(
      async () =>
        await this.apiService.setGlobalTradingLock({ lockTargetValue })
    );

    await this.refreshIsTradingEnabled();
  }

  private async refreshBalances(): Promise<void> {
    this._balances = await this.loginService.withLoginErrorHandling(
      async () => await this.apiService.getBalances()
    );
  }

  private async refreshOpenOrders(): Promise<void> {
    const openOrders = await this.loginService.withLoginErrorHandling(
      async () => await this.apiService.getCurrentOpenOrders()
    );
    this._openOrders = openOrders.map(
      (orderResponse) =>
        new Order(
          orderResponse.symbol,
          orderResponse.origQty,
          orderResponse.price
        )
    );
  }

  private async refreshIsTradingEnabled(): Promise<void> {
    const getGlobalTradingLockResponse =
      await this.loginService.withLoginErrorHandling(
        async () => await this.apiService.getGlobalTradingLock()
      );
    this._isTradingEnabled = !getGlobalTradingLockResponse.tradingIsLocked;
  }

  private async refreshTradingConfigurations(): Promise<void> {
    const getTradingConfigurationResponse =
      await this.loginService.withLoginErrorHandling(
        async () => await this.apiService.getTradingConfigurations()
      );
    // this._tradingConfigurations = getTradingConfigurationResponse.map(
    //   (tradingConfiguration) => {
    //     const baseAsset = this.symbolService.getBaseAsset(
    //       tradingConfiguration.symbolId
    //     );
    //     const quoteAsset = this.symbolService.getQuoteAsset(
    //       tradingConfiguration.symbolId
    //     );

    //     return {
    //       symbol: `${baseAsset}/${quoteAsset}`,
    //       strategyName: tradingConfiguration.tradingStrategyName,
    //       strategy: this.tradingStrategyService.getTradingStrategy(
    //         tradingConfiguration.tradingStrategyName,
    //         tradingConfiguration.tradingStrategyConfiguration
    //       ),
    //     };
    //   }
    // );

    this._tradingConfigurations = [
      {
        symbol: 'BTC/USDT',
        strategyName: 'BuyOnPercentageDecreaseInTimeframeAndSetLimitOrder',
        strategy:
          new BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy(
            86400,
            0.1,
            100,
            0.1
          ),
      },
    ];
  }
}
