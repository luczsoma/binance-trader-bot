# Binance trader bot application

This repository contains a pluggable trader bot application developed for the Binance cryptocurrency exchange.

## Setup

1. Create an API key on Binance.
2. Set up the backend application according to `binance-trader-bot-backend/README.md`. Set up the backend as a background service.
3. Set up the frontend application according to the `binance-trader-bot-frontend/README.md`.
4. Set up a web server proxying `/api` calls to the backend, and serving the frontend application.

## Develop trading strategies

You can develop trading strategies. A trading strategy watches incoming live price information from Binance, and acts upon it.
