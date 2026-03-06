# Estapar tech test

Backend system for parking management. Controls available spots, vehicle entry/exit, dynamic pricing and revenue calculation per sector.

## Tech Stack

- **Java 21**
- **Spring Boot 3.x**
- **MySQL 8.0**
- **Maven**
- **Docker / Docker Compose**
- **Swagger / OpenAPI (springdoc)**
- **Lombok**
- **JPA / Hibernate**

## Prerequisites

- Docker & Docker Compose
- Java 21 (for local development only)

## Getting Started

### 1. Start the infrastructure

**When starting up for the first time**
```bash
docker-compose up -d --build
```
when the image is already built
```bash
docker-compose up -d
```

Then, run the Spring Boot application (e.g., via IDE or command line):

*Note: The application will automatically connect to the MySQL database defined in `docker-compose.yml` and initialize the schema. If the mysql is running alongside with the simuator*
```bash
./mvnw spring-boot:run
```

Application tests can be run with:
```bash
./mvnw test
```

## Structure
Simplified hexagonal architecture approach with port and adapter layers, organized by domain and application logic. The `infrastructure` layer contains adapters for both input (web) and output (persistence, external HTTP client).
```
src/main/java/com/passada/felipe/estapar/
├── domain/
│   ├── model/
│   │   ├── Sector.java
│   │   ├── Spot.java
│   │   ├── ParkingSession.java
│   │   └── RevenueEntry.java
│   ├── repository/
│   │   ├── SectorRepository.java
│   │   ├── SpotRepository.java
│   │   ├── ParkingSessionRepository.java
│   │   └── RevenueEntryRepository.java
│   └── service/
│       ├── PricingService.java
│       └── OccupancyService.java
├── application/
│   ├── usecase/
│   │   ├── ProcessEntryUseCase.java
│   │   ├── ProcessParkedUseCase.java
│   │   ├── ProcessExitUseCase.java
│   │   └── GetRevenueUseCase.java
│   └── service/
│       ├── ProcessEntryService.java
│       ├── ProcessParkedService.java
│       ├── ProcessExitService.java
│       └── GetRevenueService.java
└── infrastructure/
    ├── adapter/
    │   ├── input/web/
    │   │   ├── WebhookController.java
    │   │   ├── RevenueController.java
    │   │   ├── GlobalExceptionHandler.java
    │   │   └── dto/
    │   │       ├── WebhookEventRequest.java
    │   │       ├── RevenueRequest.java
    │   │       ├── RevenueResponse.java
    │   │       └── ErrorResponse.java
    │   └── output/
    │       ├── persistence/
    │       │   ├── entity/
    │       │   │   ├── SectorEntity.java
    │       │   │   ├── SpotEntity.java
    │       │   │   ├── ParkingSessionEntity.java
    │       │   │   └── RevenueEntryEntity.java
    │       │   └── repository/
    │       │       ├── JpaSectorRepository.java
    │       │       ├── JpaSpotRepository.java
    │       │       ├── JpaParkingSessionRepository.java
    │       │       └── JpaRevenueEntryRepository.java
    │       └── http/
    │           └── GarageSimulatorClient.java
    └── configuration/
        ├── GarageInitializer.java
        └── OpenApiConfig.java

```

services in docker-compose: `localhost:PORT`

| Service Name | Description | Port |
|--------------|-------------|------|
|**estapar-backend**| The Spring Boot application that manages parking logic and exposes APIs | **3003**
|**mysql**| MySQL database for storing parking data | **3306**
|**garage-simulator**| Simulates the parking garage and sends events to our application | **3000**
|**grafana**| Grafana for visualizing parking data and revenue metrics | **3004**
|**prometheus**| Prometheus for collecting metrics from the application | **9090**
|**promtail**| Promtail for shipping application logs to Grafana Loki | **9080**
|**loki**| Grafana Loki for log aggregation and querying | **3100**