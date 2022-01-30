package co.lucz.binancetraderbot.exceptions;

import co.lucz.binancetraderbot.exceptions.internal.BadRequestException;
import co.lucz.binancetraderbot.exceptions.internal.ClockDiscrepancyException;
import co.lucz.binancetraderbot.exceptions.internal.MethodNotAllowedException;
import co.lucz.binancetraderbot.exceptions.internal.UnauthorizedException;
import co.lucz.binancetraderbot.services.ErrorLoggerService;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Autowired
    private ErrorLoggerService errorLoggerService;

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<Object> handleBadRequestException(
            BadRequestException exception,
            WebRequest request
    ) {
        ApiError apiError = new ApiError(
                ApiError.ApiErrorCode.BadRequest,
                exception
        );
        return handleApiError(apiError);
    }

    @ExceptionHandler({ClockDiscrepancyException.class})
    public ResponseEntity<Object> handleClockDiscrepancyException(
            ClockDiscrepancyException exception,
            WebRequest request
    ) {
        ApiError apiError = new ApiError(
                ApiError.ApiErrorCode.ClockDiscrepancy,
                exception
        );
        return handleApiError(apiError);
    }

    @ExceptionHandler({MethodNotAllowedException.class})
    public ResponseEntity<Object> handleMethodNotAllowedException(
            MethodNotAllowedException exception,
            WebRequest request
    ) {
        ApiError apiError = new ApiError(
                ApiError.ApiErrorCode.MethodNotAllowed,
                exception
        );
        return handleApiError(apiError);
    }

    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException exception, WebRequest request) {
        ApiError apiError = new ApiError(
                ApiError.ApiErrorCode.Unauthorized,
                exception
        );
        return handleApiError(apiError);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleUnknownException(Exception exception, WebRequest request) {
        ApiError apiError = new ApiError(
                ApiError.ApiErrorCode.InternalServerError,
                exception
        );
        return handleApiError(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpHeaders headers, HttpStatus status,
            WebRequest request
    ) {
        ApiError apiError = new ApiError(
                ApiError.ApiErrorCode.MethodNotAllowed,
                exception
        );
        return handleApiError(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException exception,
            HttpHeaders headers, HttpStatus status,
            WebRequest request
    ) {
        ApiError apiError = new ApiError(
                ApiError.ApiErrorCode.UnsupportedMediaType,
                exception
        );
        return handleApiError(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
            HttpMediaTypeNotAcceptableException exception,
            HttpHeaders headers, HttpStatus status,
            WebRequest request
    ) {
        ApiError apiError = new ApiError(
                ApiError.ApiErrorCode.NotAcceptable,
                exception
        );
        return handleApiError(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(
            MissingPathVariableException exception,
            HttpHeaders headers,
            HttpStatus status, WebRequest request
    ) {
        ApiError apiError = new ApiError(
                ApiError.ApiErrorCode.BadRequest,
                exception
        );
        return handleApiError(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException exception,
            HttpHeaders headers, HttpStatus status,
            WebRequest request
    ) {
        ApiError apiError = new ApiError(
                ApiError.ApiErrorCode.BadRequest,
                exception
        );
        return handleApiError(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(
            ServletRequestBindingException exception,
            HttpHeaders headers, HttpStatus status,
            WebRequest request
    ) {
        ApiError apiError = new ApiError(
                ApiError.ApiErrorCode.BadRequest,
                exception
        );
        return handleApiError(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleConversionNotSupported(
            ConversionNotSupportedException exception,
            HttpHeaders headers, HttpStatus status,
            WebRequest request
    ) {
        ApiError apiError = new ApiError(
                ApiError.ApiErrorCode.InternalServerError,
                exception
        );
        return handleApiError(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException exception, HttpHeaders headers,
            HttpStatus status, WebRequest request
    ) {
        ApiError apiError = new ApiError(
                ApiError.ApiErrorCode.BadRequest,
                exception
        );
        return handleApiError(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception,
            HttpHeaders headers, HttpStatus status,
            WebRequest request
    ) {
        ApiError apiError = new ApiError(
                ApiError.ApiErrorCode.BadRequest,
                exception
        );
        return handleApiError(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(
            HttpMessageNotWritableException exception,
            HttpHeaders headers, HttpStatus status,
            WebRequest request
    ) {
        ApiError apiError = new ApiError(
                ApiError.ApiErrorCode.InternalServerError,
                exception
        );
        return handleApiError(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers, HttpStatus status,
            WebRequest request
    ) {
        ApiError apiError = new ApiError(
                ApiError.ApiErrorCode.BadRequest,
                exception
        );
        return handleApiError(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(
            MissingServletRequestPartException exception,
            HttpHeaders headers, HttpStatus status,
            WebRequest request
    ) {
        ApiError apiError = new ApiError(
                ApiError.ApiErrorCode.BadRequest,
                exception
        );
        return handleApiError(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(
            BindException exception, HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        ApiError apiError = new ApiError(
                ApiError.ApiErrorCode.BadRequest,
                exception
        );
        return handleApiError(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException exception,
            HttpHeaders headers,
            HttpStatus status, WebRequest request
    ) {
        ApiError apiError = new ApiError(ApiError.ApiErrorCode.NotFound, exception);
        return handleApiError(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(
            AsyncRequestTimeoutException exception,
            HttpHeaders headers, HttpStatus status,
            WebRequest request
    ) {
        ApiError apiError = new ApiError(
                ApiError.ApiErrorCode.InternalServerError,
                exception
        );
        return handleApiError(apiError);
    }

    protected ResponseEntity<Object> handleApiError(ApiError apiError) {
        logger.warn(apiError.getErrorCode(), apiError.getCause());
        this.errorLoggerService.logThrowable(apiError.getCause());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(apiError, headers, ApiError.API_ERROR_STATUS_CODE);
    }
}
