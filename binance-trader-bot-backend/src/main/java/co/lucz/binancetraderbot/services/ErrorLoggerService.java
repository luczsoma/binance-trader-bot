package co.lucz.binancetraderbot.services;

import co.lucz.binancetraderbot.entities.ErrorLogEntry;
import co.lucz.binancetraderbot.repositories.ErrorLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
                stackTrace,
                Instant.now()
        ));
    }

    public List<ErrorLogEntry> getErrorLogs() {
        List<ErrorLogEntry> errorLogs = new ArrayList<>();
        this.errorLogRepository.findAll().forEach(errorLogs::add);
        return errorLogs;
    }

    public void deleteAllErrorLogs() {
        this.errorLogRepository.deleteAll();
    }
}
