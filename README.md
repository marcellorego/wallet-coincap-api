# wallet-coincap-api

## Description
Application provides a REST API to return the updated total financial value of the wallet at any given time (current and past).

Application uses Coincap API (https://docs.coincap.io/) to fetch latest prices of crypto assets.

When given a wallet of crypto assets with their positions (symbol, quantity and price) fetch latest prices from the Coincap API.

The API should return the total financial value of the wallet, the best and worst performing assets and their performance in percentage.

### Endpoints:
- POST /wallet?start={start}&end={end}
    - start: start date in UNIX time in milliseconds format.
    - end: end date in UNIX time in milliseconds format.
    - Request body: wallet
    - Response body: wallet with updated total financial value
    - Omitting will return the most recent asset history in the last minute. If start is supplied, end is required and vice versa.

Input (wallet):

    REST API receives a wallet like the following example:

symbol|quantity|price
BTC,0.12345,37870.5058
ETH,4.89532,2004.9774

Output (wallet-update):

    Database with correct data filled.
    REST API returning a JSON with:
        total={X},best_asset={X},best_performance={X},worst_asset={X},worst_performance= {X}
        Where:
            total: total financial value in USD of the entire wallet
            best_asset: which asset had the best performance (value increase) the wallet compared to the latest price retrieved from the API
            best_performance: percentage of the performance of the best_asset
            worst_asset: which asset had the worst performance (value decrease) from the wallet compared to the latest price retrieved from the API
            worst_performance: percentage of the performance of the worst_asset
            Values rounded to 2 decimal places, HALF_UP

### Headers
- Accept-Encoding: gzip or Accept-Encoding: deflate
- Content-Type: text/plain
- Accept: application/json

### Status Codes and Error Response
- 200: Successful - this is the data you were looking for
- 400: Bad Request - the request was malformed
- 500: Internal Server Error - something went wrong on our end


## Technologies
Application uses Spring Data Reactive (R2DBC) to interact with SQL database and Spring WebFlux for Reactive Rest API.

## Running the application
Project comes with 2 profiles: dev and prod.

Dev mode will provide a database connection to H2 in-memory database. 
Prod mode will provide a database connection to a PostgreSQL database.

Running the application in dev mode:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Running the application in prod mode:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```
