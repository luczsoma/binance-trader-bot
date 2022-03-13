
# Binance trader bot application

Fill `application.properties` with the necessary data for connecting to Binance.

For development, start the development server, which will make the API accessible on http://localhost:8080:

```bash
./mvnw spring-boot:run
```

For production, build a distribution artefact, then run it:

```bash
mvn clean package
java -Dspring.profiles.active=production -jar target/binance-trader-bot.jar
```