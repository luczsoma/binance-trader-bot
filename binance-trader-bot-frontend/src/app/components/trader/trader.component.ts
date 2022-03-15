import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSlideToggleChange } from '@angular/material/slide-toggle';
import { ApiService } from 'src/app/services/api.service';
import { LoginService } from 'src/app/services/login.service';
import { SymbolService } from 'src/app/services/symbol.service';
import { TradingStrategyService } from 'src/app/services/trading-strategy.service';
import { Balance } from 'src/app/types/balance';
import { Order } from 'src/app/types/order';
import { TradingConfiguration } from 'src/app/types/tradingConfiguration';
import { TradingStrategyName } from 'src/app/types/tradingStrategyName';
import { ApprovalDialogData } from '../approval-dialog/approval-dialog-data';
import { ApprovalDialogResult } from '../approval-dialog/approval-dialog-result';
import { ApprovalDialogComponent } from '../approval-dialog/approval-dialog.component';
import { CreateOrEditTradingStrategyDialogData } from '../create-or-edit-trading-strategy-dialog/create-or-edit-trading-strategy-dialog/create-or-edit-trading-strategy-dialog-data';
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
    return ['status', 'symbol', 'strategy', 'modify', 'delete'];
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
    await this.editTradingConfiguration(tradingConfiguration);

    this.refreshTradingConfigurations();
  }

  public async addTradingConfiguration(): Promise<void> {
    const dialogRef = this.dialog.open<
      CreateOrEditTradingStrategyDialogComponent,
      CreateOrEditTradingStrategyDialogData,
      TradingConfiguration
    >(CreateOrEditTradingStrategyDialogComponent, {
      data: {
        tradableSymbols: this.tradableSymbols,
        tradingStrategies: this.tradingStrategyService.getTradingStrategies(),
      },
      autoFocus: 'dialog',
    });

    dialogRef.afterClosed().subscribe(async (tradingConfiguration) => {
      if (tradingConfiguration) {
        await this.createTradingConfiguration(tradingConfiguration);
        this.refreshTradingConfigurations();
      }
    });
  }

  public async modifyTradingConfiguration(
    tradingConfiguration: TradingConfiguration
  ): Promise<void> {
    const dialogRef = this.dialog.open<
      CreateOrEditTradingStrategyDialogComponent,
      {
        tradingConfiguration: TradingConfiguration;
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
        await this.editTradingConfiguration(tradingConfiguration);
        this.refreshTradingConfigurations();
      }
    });
  }

  public async confirmDeleteTradingConfiguration(
    tradingConfiguration: TradingConfiguration
  ): Promise<void> {
    const dialogRef = this.dialog.open<
      ApprovalDialogComponent,
      ApprovalDialogData,
      ApprovalDialogResult
    >(ApprovalDialogComponent, {
      data: {
        title: 'Biztos benne?',
        description: `Biztosan törölni szeretné a ${tradingConfiguration.symbol} kereskedési párra vonatkozó konfigurációt?`,
        okButtonLabel: 'Igen',
        cancelButtonLabel: 'Nem',
      },
      autoFocus: 'dialog',
    });

    dialogRef.afterClosed().subscribe(async (result) => {
      if (result === 'ok') {
        await this.deleteTradingConfiguration(tradingConfiguration);
        this.refreshTradingConfigurations();
      }
    });
  }

  public async refreshTradableSymbols(
    force = false,
    blockUi = false
  ): Promise<void> {
    if (blockUi) {
      this._loaded = false;
    }

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

    if (blockUi) {
      this._loaded = true;
    }
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
  }

  private async createTradingConfiguration(
    tradingConfiguration: TradingConfiguration
  ): Promise<void> {
    await this.loginService.withLoginErrorHandling(
      async () =>
        await this.apiService.createTradingConfiguration({
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

  private async editTradingConfiguration(
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

  private async deleteTradingConfiguration(
    tradingConfiguration: TradingConfiguration
  ): Promise<void> {
    await this.loginService.withLoginErrorHandling(
      async () =>
        await this.apiService.deleteTradingConfiguration({
          symbolId: this.symbolService.getSymbolIdFromFriendlyName(
            tradingConfiguration.symbol
          ),
        })
    );
  }
}
