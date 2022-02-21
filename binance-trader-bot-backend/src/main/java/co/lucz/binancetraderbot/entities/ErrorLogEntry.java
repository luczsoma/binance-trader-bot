package co.lucz.binancetraderbot.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ErrorLogEntry {
    @Id
    @GeneratedValue
    private long id;

    private String name;

    private String message;

    private String localizedMessage;

    @Column(columnDefinition = "CLOB DEFAULT NULL")
    private String stackTrace;

    public ErrorLogEntry() {
    }

    public ErrorLogEntry(String name, String message, String localizedMessage, String stackTrace) {
        this.name = name;
        this.message = message;
        this.localizedMessage = localizedMessage;
        this.stackTrace = stackTrace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLocalizedMessage() {
        return localizedMessage;
    }

    public void setLocalizedMessage(String localizedMessage) {
        this.localizedMessage = localizedMessage;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}
