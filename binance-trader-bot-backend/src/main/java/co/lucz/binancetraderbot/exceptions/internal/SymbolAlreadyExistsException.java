package co.lucz.binancetraderbot.exceptions.internal;

import co.lucz.binancetraderbot.exceptions.BusinessException;

public class SymbolAlreadyExistsException extends BusinessException {
    public SymbolAlreadyExistsException(String message) {
        super(message);
    }

    public SymbolAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
