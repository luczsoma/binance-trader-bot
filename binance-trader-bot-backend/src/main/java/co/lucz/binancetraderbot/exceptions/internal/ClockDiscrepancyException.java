package co.lucz.binancetraderbot.exceptions.internal;

import co.lucz.binancetraderbot.exceptions.BusinessException;

public class ClockDiscrepancyException extends BusinessException {
    public ClockDiscrepancyException(String message) {
        super(message);
    }

    public ClockDiscrepancyException(String message, Throwable cause) {
        super(message, cause);
    }
}
