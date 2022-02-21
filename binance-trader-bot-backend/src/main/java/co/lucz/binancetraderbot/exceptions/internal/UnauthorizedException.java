package co.lucz.binancetraderbot.exceptions.internal;

import co.lucz.binancetraderbot.exceptions.BusinessException;

public class UnauthorizedException extends BusinessException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
