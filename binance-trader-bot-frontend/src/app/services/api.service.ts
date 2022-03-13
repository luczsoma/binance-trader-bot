import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment.dev';
import { ApiException } from '../types/apiException';
import { Balance } from '../types/balance';
import { CreateTradingConfigurationRequest } from '../types/createTradingConfigurationRequest';
import { EditTradingConfigurationRequest } from '../types/editTradingConfigurationRequest';
import { GetGlobalTradingLockResponse } from '../types/getGlobalTradingLockResponse';
import { GetTradingConfigurationResponse } from '../types/getTradingConfigurationResponse';
import { OpenOrderResponse } from '../types/openOrderResponse';
import { SetGlobalTradingLockRequest } from '../types/setGlobalTradingLockRequest';
import { LoginService } from './login.service';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  private readonly API_BASE_URL = environment.apiBaseUrl;

  private readonly responseProcessors: {
    readonly void: (response: Response) => Promise<void>;
    readonly json: <ResponseType>(response: Response) => Promise<ResponseType>;
  } = {
    void: async (_response: Response) => undefined,
    json: async (response: Response) => await response.json(),
  };

  public constructor(private readonly loginService: LoginService) {}

  public getTradableSymbols(): Promise<string[]> {
    return this.call<string[]>(
      'get-tradable-symbols',
      'GET',
      this.responseProcessors.json
    );
  }

  public refreshTradableSymbols(): Promise<void> {
    return this.call<void>(
      'refresh-tradable-symbols',
      'POST',
      this.responseProcessors.void
    );
  }

  public getTradingConfigurations(): Promise<
    GetTradingConfigurationResponse[]
  > {
    return this.call<GetTradingConfigurationResponse[]>(
      'get-trading-configurations',
      'GET',
      this.responseProcessors.json
    );
  }

  public createTradingConfiguration(
    createTradingConfigurationRequest: CreateTradingConfigurationRequest
  ): Promise<void> {
    return this.call<void>(
      'create-trading-configuration',
      'POST',
      this.responseProcessors.void,
      createTradingConfigurationRequest
    );
  }

  public editTradingConfiguration(
    editTradingStrategyConfigurationRequest: EditTradingConfigurationRequest
  ): Promise<void> {
    return this.call<void>(
      'edit-trading-configuration',
      'POST',
      this.responseProcessors.void,
      editTradingStrategyConfigurationRequest
    );
  }

  public getGlobalTradingLock(): Promise<GetGlobalTradingLockResponse> {
    return this.call<GetGlobalTradingLockResponse>(
      'get-global-trading-lock',
      'GET',
      this.responseProcessors.json
    );
  }

  public setGlobalTradingLock(
    setGlobalTradingLockRequest: SetGlobalTradingLockRequest
  ): Promise<void> {
    return this.call<void>(
      'set-global-trading-lock',
      'POST',
      this.responseProcessors.void,
      setGlobalTradingLockRequest
    );
  }

  public getCurrentOpenOrders(): Promise<OpenOrderResponse[]> {
    return this.call<OpenOrderResponse[]>(
      'get-current-open-orders',
      'GET',
      this.responseProcessors.json
    );
  }

  public getBalances(): Promise<Balance[]> {
    return this.call<Balance[]>(
      'get-balances',
      'GET',
      this.responseProcessors.json
    );
  }

  private async call<ResponseType>(
    endpointPath: string,
    method: 'GET' | 'POST',
    responseProcessor: (response: Response) => Promise<ResponseType>,
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
      return await responseProcessor(response);
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

    const headers: { [key: string]: string } = {
      Authorization: this.loginService.getLoginKey(),
      'Content-Type': 'application/json',
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
