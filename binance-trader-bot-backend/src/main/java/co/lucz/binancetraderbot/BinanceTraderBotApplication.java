package co.lucz.binancetraderbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BinanceTraderBotApplication {
	public static void main(String[] args) {
		SpringApplication.run(BinanceTraderBotApplication.class, args);
	}
}
