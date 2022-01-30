package co.lucz.binancetraderbot.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;

import java.util.Optional;

public final class ApiError {
    public static final HttpStatus API_ERROR_STATUS_CODE = HttpStatus.BAD_REQUEST;

    public enum ApiErrorCode {
        BadRequest,
        ClockDiscrepancy,
        InternalServerError,
        MethodNotAllowed,
        NotAcceptable,
        NotFound,
        Unauthorized,
        UnsupportedMediaType,
    }

    private ApiError.ApiErrorCode errorCode;

    @JsonIgnore
    private Throwable cause;

    public ApiError() {
        super();
    }

    public ApiError(ApiErrorCode errorCode, Throwable cause) {
        this.errorCode = Optional.ofNullable(errorCode).orElse(ApiErrorCode.InternalServerError);
        this.cause = cause;
    }

    public ApiErrorCode getErrorCode() {
        return errorCode;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setErrorCode(ApiErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }
}
