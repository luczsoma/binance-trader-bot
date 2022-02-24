import { Component, OnInit } from '@angular/core';
import { MatSlideToggleChange } from '@angular/material/slide-toggle';
import { ApiService } from 'src/app/services/api.service';
import { LoginService } from 'src/app/services/login.service';
import { Balance } from 'src/app/types/balance';
import { Order } from 'src/app/types/order';

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

  public constructor(
    private readonly loginService: LoginService,
    private readonly apiService: ApiService
  ) {}

  public async ngOnInit(): Promise<void> {
    await Promise.all([
      this.refreshBalances(),
      this.refreshOpenOrders(),
      this.refreshIsTradingEnabled(),
    ]);

    this._loaded = true;
  }

  public async refreshData(): Promise<void> {
    this._loaded = false;

    await Promise.all([
      this.refreshBalances(),
      this.refreshOpenOrders(),
      this.refreshIsTradingEnabled(),
    ]);

    this._loaded = true;
  }

  public async toggleGlobalTradingLock(
    event: MatSlideToggleChange
  ): Promise<void> {
    const lockTargetValue = !event.checked;
    await this.loginService.withLoginErrorHandling(() =>
      this.apiService.setGlobalTradingLock({ lockTargetValue })
    );

    await this.refreshIsTradingEnabled();
  }

  private async refreshBalances(): Promise<void> {
    this._balances = await this.loginService.withLoginErrorHandling(() =>
      this.apiService.getBalances()
    );
  }

  private async refreshOpenOrders(): Promise<void> {
    const openOrders = await this.loginService.withLoginErrorHandling(() =>
      this.apiService.getCurrentOpenOrders()
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
      await this.loginService.withLoginErrorHandling(() =>
        this.apiService.getGlobalTradingLock()
      );
    this._isTradingEnabled = !getGlobalTradingLockResponse.tradingIsLocked;
  }
}
