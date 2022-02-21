package co.lucz.binancetraderbot.exceptions.internal;

import co.lucz.binancetraderbot.exceptions.BusinessException;

public class BadRequestException extends BusinessException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
