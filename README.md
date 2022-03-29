# observable-api

A Spring reactive Kotlin written API to showcase various observability concepts

- Logging using Spring's default logging configuration (logback)
- Tracing using Spring Cloud Sleuth + OpenTelemetry
- Metrics exposed for Prometheus using micrometer

## Deploy using Docker

### Run Postgres

```shell
docker container run -d --name observableapidb -p 5432:5432 -e POSTGRES_PASSWORD=db1234 postgres:alpine
```

### Run the API using gradlew

```shell
./gradlew bootRun
```

### API Calls

```shell
curl -H "X-USER: kevsuperduperuser" -H "Content-Type: application/json" localhost:8080/cars
```

#### Logs

#### Traces

Example where we create a new child span (created at the service-layer). The Span ID "112eec40fa03de27" is created and ended in the CarService class

```shell
2022-03-29 01:02:52.488  INFO [observable-api,6b3fe68273bc26e602da135f5b9098f9,542fe1039875d6fe] 9411 --- [or-http-epoll-2] c.k.observableapi.audit.AuditRequest     : User [kevsuperduperuser] Queried /cars
2022-03-29 01:02:52.488  INFO [observable-api,6b3fe68273bc26e602da135f5b9098f9,542fe1039875d6fe] 9411 --- [or-http-epoll-2] c.k.observableapi.handler.CarsHandler    : About to query for all cars from the DB
2022-03-29 01:02:52.488 DEBUG [observable-api,6b3fe68273bc26e602da135f5b9098f9,542fe1039875d6fe] 9411 --- [or-http-epoll-2] c.k.observableapi.handler.CarsHandler    : cars() START - About to call CarService
2022-03-29 01:02:52.489 DEBUG [observable-api,6b3fe68273bc26e602da135f5b9098f9,112eec40fa03de27] 9411 --- [or-http-epoll-2] c.k.observableapi.service.CarService     : getAllCars() START - About to call CarRepository
2022-03-29 01:02:52.602  INFO [observable-api,6b3fe68273bc26e602da135f5b9098f9,112eec40fa03de27] 9411 --- [or-http-epoll-2] c.k.observableapi.service.CarService     : Number of cars returned from DB 4
2022-03-29 01:02:52.602 DEBUG [observable-api,6b3fe68273bc26e602da135f5b9098f9,112eec40fa03de27] 9411 --- [or-http-epoll-2] c.k.observableapi.service.CarService     : Cars entities returned:
2022-03-29 01:02:52.603 DEBUG [observable-api,6b3fe68273bc26e602da135f5b9098f9,112eec40fa03de27] 9411 --- [or-http-epoll-2] c.k.observableapi.service.CarService     :    Car = Car(id=1, name='Audi A4', msrp=44000, inventory=1000)
2022-03-29 01:02:52.606 DEBUG [observable-api,6b3fe68273bc26e602da135f5b9098f9,112eec40fa03de27] 9411 --- [or-http-epoll-2] c.k.observableapi.service.CarService     :    Car = Car(id=2, name='Audi S3', msrp=48000, inventory=2000)
2022-03-29 01:02:52.606 DEBUG [observable-api,6b3fe68273bc26e602da135f5b9098f9,112eec40fa03de27] 9411 --- [or-http-epoll-2] c.k.observableapi.service.CarService     :    Car = Car(id=3, name='Audi S4', msrp=61000, inventory=1800)
2022-03-29 01:02:52.606 DEBUG [observable-api,6b3fe68273bc26e602da135f5b9098f9,112eec40fa03de27] 9411 --- [or-http-epoll-2] c.k.observableapi.service.CarService     :    Car = Car(id=4, name='Audi RS6', msrp=125000, inventory=20)
2022-03-29 01:02:52.606 DEBUG [observable-api,6b3fe68273bc26e602da135f5b9098f9,112eec40fa03de27] 9411 --- [or-http-epoll-2] c.k.observableapi.service.CarService     : getAllCars() END - Return Iterable carsEntities
2022-03-29 01:02:52.606 DEBUG [observable-api,6b3fe68273bc26e602da135f5b9098f9,542fe1039875d6fe] 9411 --- [or-http-epoll-2] c.k.observableapi.handler.CarsHandler    : cars() END - CarService called
2022-03-29 01:02:52.607  INFO [observable-api,6b3fe68273bc26e602da135f5b9098f9,542fe1039875d6fe] 9411 --- [or-http-epoll-2] c.k.observableapi.handler.CarsHandler    : Done querying for all cars.
```

### Actuator Metrics

| Page        | URL                                       |
|:------------|:------------------------------------------|
| Actuator    | http://localhost:8080/actuator/           |
 | Prometheus  | http://localhost:8080/actuator/prometheus |

### Build the API using Maven Jib

```shell
./gradlew jibDockerBuild
```

## TODO

- add businessrules to apply to the entities/DTO. Add "business rule" logs