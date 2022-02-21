# Binance trader bot application

Create a `.env` file with the necessary data, then source it:

```bash
cp .env.example .env
source .env
```

Build a distribution artefact, then run it:

```bash
mvn clean package
java -jar target/binance-trader-bot.jar
```