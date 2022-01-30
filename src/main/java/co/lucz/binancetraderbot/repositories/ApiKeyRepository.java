package co.lucz.binancetraderbot.repositories;

import co.lucz.binancetraderbot.entities.ApiKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiKeyRepository extends CrudRepository<ApiKey, Long> {
    Optional<ApiKey> findByApiKey(String apiKey);
}
