package co.lucz.binancetraderbot.repositories;

import co.lucz.binancetraderbot.entities.TradingConfiguration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradingConfigurationRepository extends CrudRepository<TradingConfiguration, Long> {
}
