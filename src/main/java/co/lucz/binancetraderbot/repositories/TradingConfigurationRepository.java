package co.lucz.binancetraderbot.repositories;

import co.lucz.binancetraderbot.entities.TradingConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradingConfigurationRepository extends JpaRepository<TradingConfiguration, Long> {
}
