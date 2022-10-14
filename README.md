# Backend assigment

## Overview
Application implemented with usage of Spring Boot, as a database I used H2.
Three-tier architecture is used, api, business logic and database layers are isolated from each other.
For database layer, I chosed pure sql, without any ORM framework.
Database populated at application startup from csv file(file contains GPS coordinates and IATA names).
Flight number is generated for each day starting from today till 2023-01-01.
I assumed some rules for price calculation: price = {distance_between_airports}*{weekend_multiplier}*{last_minute_multiplier}+{minimum_price}.

Api documentation is available through http://localhost:8080/swagger-ui/#/flights-controller/, you can also send test request from there.

## Project structure

com.emirates.flights.config - common configuration for project.

com.emirates.flights.db - database layer.

com.emirates.flights.controller - api layer.

com.emirates.flights.service - business logic layer.

## How to run application
Additional configuration not needed, database will be populated automaticaly on application startup with Flyway.
Just start com.emirates.flights.BankingApplication

## Examples
Better to use swagger, it is located here: http://127.0.0.1:8080/banking/swagger-ui/, but just in case here is some request and response examples:


### Get flight

Request:
```json
curl -X GET "http://localhost:8080/api/v1/flights?departureAirport=LED&destinationAirport=DXB&departureDate=2022-10-21" -H "accept: */*"
```
Response:
```json
{
  "flightNumber": "FLIGHT_6018_2701_2022-10-21"
}
```
### Get prices

Request:
```jsoncurl 
 -X GET "http://localhost:8080/api/v1/price?flightNumber=FLIGHT_6018_2701_2022-10-23&departureDate=2022-10-23" -H "accept: */*"
```
Response:
```json
{
  "price": 378.0240714831327
}
```

### What can be improved in real application
In 'real' life integration tests with good code coverage are required.
Currency should be introduced.
Instead of in-memory database it's better to use no-sql db, instead of guava cache - hazelcast.
Obviously there should be a way to update flight details, for now it is assumed that it is done by third party service.
Exception handling and input validation(I've added a couple of checks, but not a proper handling).
Security, scalability etc, etc. 

