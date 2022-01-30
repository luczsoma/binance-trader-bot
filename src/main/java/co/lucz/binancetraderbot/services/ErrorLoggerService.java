package co.lucz.binancetraderbot.services;

import co.lucz.binancetraderbot.entities.ErrorLogEntry;
import co.lucz.binancetraderbot.repositories.ErrorLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;

@Service
public class ErrorLoggerService {
    @Autowired
    private ErrorLogRepository errorLogRepository;

    public void logThrowable(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        String stackTrace = stringWriter.toString();

        this.errorLogRepository.save(new ErrorLogEntry(
                throwable.getClass().getName(),
                throwable.getMessage(),
                throwable.getLocalizedMessage(),
                stackTrace
        ));
    }
}
