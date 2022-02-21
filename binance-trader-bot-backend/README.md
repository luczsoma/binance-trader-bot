
# Binance trader bot application

Create a `.env` file with the necessary data, then source it:

```bash
cp .env.example .env
source .env
```

For development, start the development server, which will make the API accessible on http://localhost:8080:

```bash
./mvnw spring-boot:run
```

For production, build a distribution artefact, then run it:

```bash
mvn clean package
java -Dspring.profiles.active=production -jar target/binance-trader-bot.jar
```