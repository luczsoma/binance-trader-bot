package co.lucz.binancetraderbot.services;

import co.lucz.binancetraderbot.exceptions.internal.UnauthorizedException;
import co.lucz.binancetraderbot.repositories.ApiKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    @Autowired
    private ApiKeyRepository apiKeyRepository;

    public void verifyApiKey(String apiKey) {
        this.apiKeyRepository.findByApiKey(apiKey).orElseThrow(() -> new UnauthorizedException("API key not found"));
    }
}
