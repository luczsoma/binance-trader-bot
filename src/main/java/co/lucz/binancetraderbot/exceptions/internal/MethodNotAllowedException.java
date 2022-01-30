package co.lucz.binancetraderbot.exceptions.internal;

import co.lucz.binancetraderbot.exceptions.BusinessException;

public class MethodNotAllowedException extends BusinessException {
    public MethodNotAllowedException(String message) {
        super(message);
    }

    public MethodNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }
}
