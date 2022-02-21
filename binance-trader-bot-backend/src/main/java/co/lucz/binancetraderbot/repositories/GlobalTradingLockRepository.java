package co.lucz.binancetraderbot.repositories;

import co.lucz.binancetraderbot.entities.GlobalTradingLock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalTradingLockRepository extends CrudRepository<GlobalTradingLock, Long> {
}
