package co.lucz.binancetraderbot.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ApiKey {
    @Id
    @GeneratedValue
    private long id;

    @Column(unique = true)
    private String apiKey;

    public ApiKey() {
    }

    public ApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
