import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Balance } from '../types/balance';
import { CreateTradingStrategyConfiguration } from '../types/createTradingStrategyConfiguration';
import { EditTradingStrategyConfiguration } from '../types/editTradingStrategyConfiguration';
import { OpenOrderResponse } from '../types/openOrderResponse';
import { SetGlobalTradingLockRequest } from '../types/setGlobalTradingLockRequest';
import { LoginService } from './login.service';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  private readonly API_HOST = environment.apiBaseUrl;

  public constructor(
    private readonly httpClient: HttpClient,
    private readonly loginService: LoginService
  ) {}

  public createTradingConfiguration(
    request: CreateTradingStrategyConfiguration
  ): Observable<void> {
    return this.httpClient.post<void>(
      `${this.API_HOST}/api/create-trading-configuration`,
      request,
      {
        headers: {
          Authorization: this.loginService.getKey(),
        },
      }
    );
  }

  public editTradingConfiguration(
    request: EditTradingStrategyConfiguration
  ): Observable<void> {
    return this.httpClient.post<void>(
      `${this.API_HOST}/api/edit-trading-configuration`,
      request,
      {
        headers: {
          Authorization: this.loginService.getKey(),
        },
      }
    );
  }

  public setGlobalTradingLock(
    request: SetGlobalTradingLockRequest
  ): Observable<void> {
    return this.httpClient.post<void>(
      `${this.API_HOST}/api/set-global-trading-lock`,
      request,
      {
        headers: {
          Authorization: this.loginService.getKey(),
        },
      }
    );
  }

  public getCurrentOpenOrders(): Observable<OpenOrderResponse[]> {
    return this.httpClient.get<OpenOrderResponse[]>(
      `${this.API_HOST}/api/get-current-open-orders`,
      {
        headers: {
          Authorization: this.loginService.getKey(),
        },
      }
    );
  }

  public getBalances(): Observable<Balance[]> {
    return this.httpClient.get<Balance[]>(`${this.API_HOST}/api/get-balances`, {
      headers: {
        Authorization: this.loginService.getKey(),
      },
    });
  }
}
