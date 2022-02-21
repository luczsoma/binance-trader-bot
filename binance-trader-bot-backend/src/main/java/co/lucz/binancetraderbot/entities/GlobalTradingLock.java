package co.lucz.binancetraderbot.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class GlobalTradingLock {
    @Id
    @GeneratedValue
    private long id;
}
