import { ApiError } from './apiError';
import { ApiErrorCode } from './apiErrorCode';

export class ApiException extends Error implements ApiError {
  public constructor(public readonly errorCode: ApiErrorCode) {
    super();
    this.message = errorCode;
  }
}
