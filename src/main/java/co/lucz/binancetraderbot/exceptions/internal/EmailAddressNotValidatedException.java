package co.lucz.binancetraderbot.exceptions.internal;

import co.lucz.binancetraderbot.exceptions.BusinessException;

public class EmailAddressNotValidatedException extends BusinessException {
    public EmailAddressNotValidatedException(String message) {
        super(message);
    }

    public EmailAddressNotValidatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
