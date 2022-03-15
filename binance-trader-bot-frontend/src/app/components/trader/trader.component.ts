import { Component, OnInit } from '@angular/core';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialog } from '@angular/material/dialog';
import { MatSlideToggleChange } from '@angular/material/slide-toggle';
import { ApiService } from 'src/app/services/api.service';
import { LoginService } from 'src/app/services/login.service';
import { SymbolService } from 'src/app/services/symbol.service';
import { TradingStrategyService } from 'src/app/services/trading-strategy.service';
import { Balance } from 'src/app/types/balance';
import { Order } from 'src/app/types/order';
import { TradingConfiguration } from 'src/app/types/tradingConfiguration';
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
  private _selectedTradingConfigurations: Set<string> = new Set();

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
    return ['select', 'status', 'symbol', 'strategy', 'modify', 'delete'];
  }

  public get selectedTradingConfigurations(): Set<string> {
    return this._selectedTradingConfigurations;
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
        state: 'create',
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
      CreateOrEditTradingStrategyDialogData,
      TradingConfiguration
    >(CreateOrEditTradingStrategyDialogComponent, {
      data: {
        state: 'editOne',
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

  public async modifySelectedTradingConfigurations(): Promise<void> {
    if (this.selectedTradingConfigurations.size === 1) {
      const selectedTradingConfiguration = this.tradingConfigurations.find(
        (tradingConfiguration) =>
          tradingConfiguration.symbol ===
          Array.from(this.selectedTradingConfigurations)[0]
      );
      if (selectedTradingConfiguration) {
        this.modifyTradingConfiguration(selectedTradingConfiguration);
      }
      return;
    }

    const dialogRef = this.dialog.open<
      CreateOrEditTradingStrategyDialogComponent,
      CreateOrEditTradingStrategyDialogData,
      TradingConfiguration
    >(CreateOrEditTradingStrategyDialogComponent, {
      data: {
        state: 'editMultiple',
        tradableSymbols: this.tradableSymbols,
        tradingStrategies: this.tradingStrategyService.getTradingStrategies(),
      },
      autoFocus: 'dialog',
    });

    dialogRef.afterClosed().subscribe(async (tradingConfiguration) => {
      if (tradingConfiguration) {
        await Promise.all(
          Array.from(this.selectedTradingConfigurations).map((symbol) =>
            this.editTradingConfiguration({ ...tradingConfiguration, symbol })
          )
        );
        await this.refreshTradingConfigurations();
      }
    });
  }

  public async confirmDeleteSelectedTradingConfigurations(): Promise<void> {
    await this.confirmDeleteTradingConfigurations(
      Array.from(this.selectedTradingConfigurations)
    );
  }

  public async confirmDeleteTradingConfigurations(
    tradingConfigurations: string[]
  ): Promise<void> {
    const dialogRef = this.dialog.open<
      ApprovalDialogComponent,
      ApprovalDialogData,
      ApprovalDialogResult
    >(ApprovalDialogComponent, {
      data: {
        title: 'Biztos benne?',
        description: `Biztosan törölni szeretné a(z) ${tradingConfigurations.join(
          ', '
        )} kereskedési pár(ok)ra vonatkozó konfiguráció(ka)t?`,
        okButtonLabel: 'Igen',
        cancelButtonLabel: 'Nem',
      },
      autoFocus: 'dialog',
    });

    dialogRef.afterClosed().subscribe(async (result) => {
      if (result === 'ok') {
        await Promise.all(
          tradingConfigurations.map((tradingConfiguration) => {
            const symbolId =
              this.symbolService.getSymbolIdFromFriendlyName(
                tradingConfiguration
              );
            return this.deleteTradingConfiguration(symbolId);
          })
        );
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
    await this.refreshTradingConfigurations();
    this._tradableSymbols = new Set(
      tradableSymbolsResponse
        .map((symbolId) =>
          this.symbolService.getFriendlyNameFromSymbolId(symbolId)
        )
        .filter(
          (symbol) =>
            this.tradingConfigurations.filter(
              (tradingConfiguration) => tradingConfiguration.symbol === symbol
            ).length === 0
        )
        .sort((a, b) => a.localeCompare(b))
    );

    if (blockUi) {
      this._loaded = true;
    }
  }

  public toggleTradingConfigurationSelection(
    change: MatCheckboxChange,
    tradingConfiguration: TradingConfiguration
  ): void {
    if (change.checked) {
      this.selectedTradingConfigurations.add(tradingConfiguration.symbol);
    } else {
      this.selectedTradingConfigurations.delete(tradingConfiguration.symbol);
    }
  }

  public isTradingConfigurationSelected(
    tradingConfiguration: TradingConfiguration
  ): boolean {
    return this.selectedTradingConfigurations.has(tradingConfiguration.symbol);
  }

  public toggleAllTradingConfigurationSelection(
    change: MatCheckboxChange
  ): void {
    if (change.checked) {
      this.tradingConfigurations.forEach((tradingConfiguration) =>
        this.selectedTradingConfigurations.add(tradingConfiguration.symbol)
      );
    } else {
      this.selectedTradingConfigurations.clear();
    }
  }

  public someTradingConfigurationsAreSelected(): boolean {
    return this.selectedTradingConfigurations.size > 0;
  }

  public allTradingConfigurationsAreSelected(): boolean {
    return (
      this.selectedTradingConfigurations.size ===
      this.tradingConfigurations.length
    );
  }

  private async refreshBalances(): Promise<void> {
    const balances = await this.loginService.withLoginErrorHandling(
      async () => await this.apiService.getBalances()
    );
    this._balances = balances.sort((a, b) => a.asset.localeCompare(b.asset));
  }

  private async refreshOpenOrders(): Promise<void> {
    const openOrders = await this.loginService.withLoginErrorHandling(
      async () => await this.apiService.getCurrentOpenOrders()
    );
    this._openOrders = openOrders
      .map(
        (orderResponse) =>
          new Order(
            orderResponse.symbol,
            orderResponse.origQty,
            orderResponse.price
          )
      )
      .sort((a, b) => a.pair.localeCompare(b.pair));
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
    this._tradingConfigurations = getTradingConfigurationResponse
      .map((getTradingConfigurationResponse) => ({
        symbol: this.symbolService.getFriendlyNameFromSymbolId(
          getTradingConfigurationResponse.symbolId
        ),
        strategy: this.tradingStrategyService.getTradingStrategy(
          getTradingConfigurationResponse.tradingStrategyName,
          getTradingConfigurationResponse.tradingStrategyConfiguration
        ),
        enabled: getTradingConfigurationResponse.enabled,
      }))
      .sort((a, b) => a.symbol.localeCompare(b.symbol));

    this.selectedTradingConfigurations.clear();
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

  private async deleteTradingConfiguration(symbolId: string): Promise<void> {
    await this.loginService.withLoginErrorHandling(
      async () =>
        await this.apiService.deleteTradingConfiguration({
          symbolId,
        })
    );
  }
}
