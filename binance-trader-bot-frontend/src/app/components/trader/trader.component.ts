import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSlideToggleChange } from '@angular/material/slide-toggle';
import { ApiService } from 'src/app/services/api.service';
import { LoginService } from 'src/app/services/login.service';
import { SymbolService } from 'src/app/services/symbol.service';
import { TradingStrategyService } from 'src/app/services/trading-strategy.service';
import { Balance } from 'src/app/types/balance';
import { Order } from 'src/app/types/order';
import { BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy } from 'src/app/types/strategies/buyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy';
import { TradingConfiguration } from 'src/app/types/tradingConfiguration';
import { TradingStrategyName } from 'src/app/types/tradingStrategyName';
import { CreateOrEditTradingStrategyDialogComponent } from '../create-or-edit-trading-strategy-dialog/create-or-edit-trading-strategy-dialog/create-or-edit-trading-strategy-dialog.component';

@Component({
  selector: 'binance-trader-bot-frontend-trader',
  templateUrl: './trader.component.html',
  styleUrls: ['./trader.component.scss'],
})
export class TraderComponent implements OnInit {
  private _loaded = false;

  private _tradableSymbols = new Set<string>();
  private _balances: Balance[] = [];
  private _openOrders: Order[] = [];
  private _isTradingEnabled = true;
  private _tradingConfigurations: TradingConfiguration[] = [];

  public get loaded(): boolean {
    return this._loaded;
  }

  public get tradableSymbols(): Set<string> {
    return this._tradableSymbols;
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
    return ['status', 'symbol', 'strategy', 'edit'];
  }

  public constructor(
    private readonly loginService: LoginService,
    private readonly apiService: ApiService,
    private readonly symbolService: SymbolService,
    private readonly tradingStrategyService: TradingStrategyService,
    private readonly dialog: MatDialog
  ) {}

  public async ngOnInit(): Promise<void> {
    await Promise.all([
      this.refreshTradableSymbols(),
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
      this.refreshTradableSymbols(),
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

  public async toggleTradingConfiguration(
    event: MatSlideToggleChange,
    tradingConfiguration: TradingConfiguration
  ): Promise<void> {
    const enabled = event.checked;
    tradingConfiguration.enabled = enabled;
    await this.updateTradingConfiguration(tradingConfiguration);

    this.refreshTradingConfigurations();
  }

  public async editTradingConfiguration(
    tradingConfiguration: TradingConfiguration
  ): Promise<void> {
    const dialogRef = this.dialog.open<
      CreateOrEditTradingStrategyDialogComponent,
      {
        tradingConfiguration: TradingConfiguration | undefined;
        tradableSymbols: Set<string>;
        tradingStrategies: Set<TradingStrategyName>;
      },
      TradingConfiguration
    >(CreateOrEditTradingStrategyDialogComponent, {
      data: {
        tradingConfiguration,
        tradableSymbols: this.tradableSymbols,
        tradingStrategies: this.tradingStrategyService.getTradingStrategies(),
      },
      autoFocus: 'dialog',
    });

    dialogRef.afterClosed().subscribe(async (tradingConfiguration) => {
      if (tradingConfiguration) {
        await this.updateTradingConfiguration(tradingConfiguration);
        this.refreshTradingConfigurations();
      }
    });
  }

  public async refreshTradableSymbols(force = false): Promise<void> {
    if (force) {
      await this.loginService.withLoginErrorHandling(
        async () => await this.apiService.refreshTradableSymbols()
      );
    }

    const tradableSymbolsResponse =
      await this.loginService.withLoginErrorHandling(
        async () => await this.apiService.getTradableSymbols()
      );
    this._tradableSymbols = new Set(
      tradableSymbolsResponse.map((symbolId) =>
        this.symbolService.getFriendlyNameFromSymbolId(symbolId)
      )
    );
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
    this._tradingConfigurations = getTradingConfigurationResponse.map(
      (getTradingConfigurationResponse) => ({
        symbol: this.symbolService.getFriendlyNameFromSymbolId(
          getTradingConfigurationResponse.symbolId
        ),
        strategy: this.tradingStrategyService.getTradingStrategy(
          getTradingConfigurationResponse.tradingStrategyName,
          getTradingConfigurationResponse.tradingStrategyConfiguration
        ),
        enabled: getTradingConfigurationResponse.enabled,
      })
    );

    this._tradingConfigurations = [
      {
        symbol: 'BTC/USDT',
        strategy:
          new BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy(
            86400,
            0.1,
            100,
            0.1
          ),
        enabled: true,
      },
    ];
  }

  private async updateTradingConfiguration(
    tradingConfiguration: TradingConfiguration
  ): Promise<void> {
    await this.loginService.withLoginErrorHandling(
      async () =>
        await this.apiService.editTradingConfiguration({
          symbolId: this.symbolService.getSymbolIdFromFriendlyName(
            tradingConfiguration.symbol
          ),
          tradingStrategyName:
            this.tradingStrategyService.getTradingStrategyName(
              tradingConfiguration.strategy
            ),
          tradingStrategyConfiguration:
            tradingConfiguration.strategy.toTradingStrategyConfigurationJson(),
          enabled: tradingConfiguration.enabled,
        })
    );
  }
}
