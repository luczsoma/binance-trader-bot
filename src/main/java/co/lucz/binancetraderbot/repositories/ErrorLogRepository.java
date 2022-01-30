package co.lucz.binancetraderbot.repositories;

import co.lucz.binancetraderbot.entities.ErrorLogEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorLogRepository extends CrudRepository<ErrorLogEntry, Long> {
}
