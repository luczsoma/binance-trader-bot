
# Binance trader bot backend

Create `application-development.properties` and `application-production.properties` files (they are ignored by Git), and fill them with the necessary data for connecting to Binance:

```
binance.rest-api-base-url=
binance.ws-api-base-url=
binance.api-key=
binance.secret-key=
```

For development, start the development server, which will make the API accessible on http://localhost:8080 with settings specified in `application-development.properties`:

```bash
./mvnw spring-boot:run
```

For production, build a distribution artefact, then run it with `application-production.properties` available next to the JAR:

```bash
mvn clean package
cp application-production.properties target
java -Dspring.profiles.active=production -jar target/binance-trader-bot.jar
```