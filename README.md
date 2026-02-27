# Estapar tech test

## How to run
Start the docker-compose in order to use mysql:
```bash
docker-compose up -d
```

Then, run the Spring Boot application (e.g., via IDE or command line):
```bash
./mvnw spring-boot:run
```

## Structure
```
src/main/java/com/estapar/management
├── domain/                         # O Coração (Business Logic)
│   ├── model/                      # Entities (Garage, Sector, Transaction, Spot)
│   ├── service/                    # Business Logic (ParkingService, PricingStrategy)
│   └── repository/                 # Ports (Interfaces output/persistence)
├── application/                    # Usecases (Orquestration)
│   ├── usecase/                    # Interfaces (ProcessEntry, ProcessExit)
│   └── service/                    # Usecases implementation
├── infrastructure/                 # Adapters
│   ├── adapter/
│   │   ├── input/web/              # WebhookController, RevenueController
│   │   ├── output/persistence/     # JpaRepositories e Jpa Entities
│   │   └── output/http/            # SimulatorClient (RestClient do Java 21)
│   └── configuration/              # Beans, Security, Mock/Simulador Config
```