import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment.dev';
import { ApiException } from '../types/apiException';
import { Balance } from '../types/balance';
import { CreateTradingConfigurationRequest } from '../types/createTradingConfigurationRequest';
import { EditTradingConfigurationRequest } from '../types/editTradingConfigurationRequest';
import { OpenOrderResponse } from '../types/openOrderResponse';
import { SetGlobalTradingLockRequest } from '../types/setGlobalTradingLockRequest';
import { LoginService } from './login.service';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  private readonly API_BASE_URL = environment.apiBaseUrl;

  public constructor(private readonly loginService: LoginService) {}

  public createTradingConfiguration(
    createTradingConfigurationRequest: CreateTradingConfigurationRequest
  ): Promise<void> {
    return this.call<void>(
      'create-trading-configuration',
      'POST',
      createTradingConfigurationRequest
    );
  }

  public editTradingConfiguration(
    editTradingStrategyConfigurationRequest: EditTradingConfigurationRequest
  ): Promise<void> {
    return this.call<void>(
      'edit-trading-configuration',
      'POST',
      editTradingStrategyConfigurationRequest
    );
  }

  public setGlobalTradingLock(
    setGlobalTradingLockRequest: SetGlobalTradingLockRequest
  ): Promise<void> {
    return this.call<void>(
      'set-global-trading-lock',
      'POST',
      setGlobalTradingLockRequest
    );
  }

  public getCurrentOpenOrders(): Promise<OpenOrderResponse[]> {
    return this.call<OpenOrderResponse[]>('get-current-open-orders', 'GET');
  }

  public getBalances(): Promise<Balance[]> {
    return this.call<Balance[]>('get-balances', 'GET');
  }

  private async call<ResponseType>(
    endpointPath: string,
    method: 'GET' | 'POST',
    requestPayload?: unknown
  ): Promise<ResponseType> {
    const request = this.createRequest(endpointPath, method, requestPayload);
    const response = await fetch(request);

    if (!response.ok) {
      try {
        const apiErrorJson = await response.json();
        throw new ApiException(apiErrorJson.errorCode ?? 'UnknownError');
      } catch (ex) {
        if (ex instanceof ApiException) {
          throw ex;
        } else {
          throw new ApiException('UnknownError');
        }
      }
    }

    try {
      return response.json();
    } catch (ex) {
      throw new ApiException('UnknownError');
    }
  }

  private createRequest(
    endpointPath: string,
    method: 'GET' | 'POST',
    requestPayload: unknown
  ): Request {
    const requestUri = `${this.API_BASE_URL}/${endpointPath}`;
    const headers = {
      Authorization: this.loginService.getLoginKey(),
    };
    const body = JSON.stringify(requestPayload);
    return new Request(requestUri, {
      method,
      headers,
      body,
      mode: environment.fetchRequestMode,
      credentials: 'omit',
      cache: 'no-store',
    });
  }
}
