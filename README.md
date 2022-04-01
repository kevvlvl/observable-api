# observable-api

This repo showcases various observability concepts using a Spring Boot based application

## Tech Stack

- REST service using Spring boot reactive + PostgreSQL
- Written in Kotlin for JVM 17
- Distributed tracing with OpenTracing + Jaeger
- Metrics exposed using micrometer

## Observability concepts

### Logs

A few best-practices have been implemented here to help various stakeholders

- Prior to the spring boot server startup, we dump all active configuration properties in DEBUG. This can help developers or operations to confirm the application setup
- We log in INFO general information/steps at key places
- We log in INFO business rules traceability that can help QA/analysts/devs
- We log in INFO audit entries: Who (user) queried what (endpoint) when (log timestamp) 

### Traces

We use Spring Cloud Sleuth with an opentelemetry implementation (instead of opentracing/brave). See [Spring Experimental Projects](https://spring-projects-experimental.github.io/spring-cloud-sleuth-otel/docs/current/reference/html/getting-started.html) for more

In the code, there's an example where we create a new child span (```Span reported: e965ef21f008a949:d68675a8ac3fe2c2:e965ef21f008a949:1 - car-svc-getAllCars```). The Span ID "112eec40fa03de27" is created to visualize a distinct traceability of the Service layer (CarService)

```shell
2022-04-01 14:39:33.828  INFO 18393 --- [or-http-epoll-2] c.k.observableapi.audit.AuditRequest     : User [kevsuperduperuser] Queried /cars
2022-04-01 14:39:33.829  INFO 18393 --- [or-http-epoll-2] c.k.observableapi.handler.CarsHandler    : About to query for all cars from the DB
2022-04-01 14:39:33.829 DEBUG 18393 --- [or-http-epoll-2] c.k.observableapi.handler.CarsHandler    : cars() START - About to call CarService
2022-04-01 14:39:33.829 DEBUG 18393 --- [or-http-epoll-2] c.k.observableapi.service.CarService     : getAllCars() START - About to call CarRepository
2022-04-01 14:39:33.928  INFO 18393 --- [or-http-epoll-2] i.j.internal.reporters.LoggingReporter   : Span reported: e965ef21f008a949:6c94ee4d37383003:e965ef21f008a949:1 - Query
2022-04-01 14:39:33.950  INFO 18393 --- [or-http-epoll-2] c.k.observableapi.service.CarService     :    Number of cars returned from DB 4
2022-04-01 14:39:33.950 DEBUG 18393 --- [or-http-epoll-2] c.k.observableapi.service.CarService     :    Cars entities returned:
2022-04-01 14:39:33.950 DEBUG 18393 --- [or-http-epoll-2] c.k.observableapi.service.CarService     :    Car = Car(id=1, name='Audi A4', msrp=44000, inventory=1000)
2022-04-01 14:39:33.954 DEBUG 18393 --- [or-http-epoll-2] c.k.observableapi.service.CarService     :    Car = Car(id=2, name='Audi S3', msrp=48000, inventory=2000)
2022-04-01 14:39:33.954 DEBUG 18393 --- [or-http-epoll-2] c.k.observableapi.service.CarService     :    Car = Car(id=3, name='Audi S4', msrp=61000, inventory=1800)
2022-04-01 14:39:33.954 DEBUG 18393 --- [or-http-epoll-2] c.k.observableapi.service.CarService     :    Car = Car(id=4, name='Audi RS6', msrp=125000, inventory=20)
2022-04-01 14:39:33.955 DEBUG 18393 --- [or-http-epoll-2] c.k.observableapi.service.CarService     :    Obtained carsEntities. Prepare entities into DTO
2022-04-01 14:39:33.955 DEBUG 18393 --- [or-http-epoll-2] c.k.observableapi.delivery.CarDelivery   : getPreparedData() START - Prepare entities into DTOs
2022-04-01 14:39:33.955  INFO 18393 --- [or-http-epoll-2] c.k.observableapi.delivery.CarDelivery   : CD01 - Begin reviewing cars inventory
2022-04-01 14:39:33.955  INFO 18393 --- [or-http-epoll-2] c.k.observableapi.delivery.CarDelivery   : CD01 - Reviewed
2022-04-01 14:39:33.955  INFO 18393 --- [or-http-epoll-2] c.k.observableapi.delivery.CarDelivery   : CD02 - Begin adding supplementary info
2022-04-01 14:39:33.955  INFO 18393 --- [or-http-epoll-2] c.k.observableapi.delivery.CarDelivery   : CD02 - Done adding supplementary info
2022-04-01 14:39:33.956 DEBUG 18393 --- [or-http-epoll-2] c.k.observableapi.delivery.CarDelivery   : getPreparedData() END - Return carsDtos. Count = 4
2022-04-01 14:39:33.956 DEBUG 18393 --- [or-http-epoll-2] c.k.observableapi.service.CarService     : getAllCars() END - Return DTOs
2022-04-01 14:39:33.956  INFO 18393 --- [or-http-epoll-2] i.j.internal.reporters.LoggingReporter   : Span reported: e965ef21f008a949:d68675a8ac3fe2c2:e965ef21f008a949:1 - car-svc-getAllCars
2022-04-01 14:39:33.956 DEBUG 18393 --- [or-http-epoll-2] c.k.observableapi.handler.CarsHandler    : cars() END - CarService called
2022-04-01 14:39:33.956  INFO 18393 --- [or-http-epoll-2] c.k.observableapi.handler.CarsHandler    : Done querying for all cars.
2022-04-01 14:39:34.017  INFO 18393 --- [or-http-epoll-2] i.j.internal.reporters.LoggingReporter   : Span reported: e965ef21f008a949:e965ef21f008a949:0:1 - /cars
```

### Metrics

Prometheus-compatible metrics are implemented using micrometer and exposed using an actuator endpoint:

| Page        | URL                                       |
|:------------|:------------------------------------------|
| Actuator    | http://localhost:8080/actuator/           |
| Prometheus  | http://localhost:8080/actuator/prometheus |

## Build and Deploy

### Locally (using Docker)

#### 1) Run Postgres Database

```shell
docker container run -d --name observableapidb -p 5432:5432 -e POSTGRES_PASSWORD=db1234 postgres:alpine
```

#### 2) Run Jaeger

From https://www.jaegertracing.io/docs/1.32/getting-started/

```shell
docker run -d --name jaeger \
  -e COLLECTOR_ZIPKIN_HOST_PORT=:9411 \
  -p 5775:5775/udp \
  -p 6831:6831/udp \
  -p 6832:6832/udp \
  -p 5778:5778 \
  -p 16686:16686 \
  -p 14250:14250 \
  -p 14268:14268 \
  -p 14269:14269 \
  -p 9411:9411 \
  jaegertracing/all-in-one:1.32
```

Jaeger UI: http://localhost:16686/

#### 3) Run the API

The API will pre-populate the database (DDL + DML) using flyway

```shell
./gradlew bootRun
```

### on Kubernetes

#### 1) Build the API using Maven Jib

```shell
./gradlew jibDockerBuild
```

TBD (k8s API manifests + steps)

## API calls

### Actuator

```shell
http://localhost:8080/actuator
```

Here, we can see results of our custom implementation of the Postgres Health Check
```shell
http://localhost:8080/actuator/health
```

### API

Pass the notion of a "user" using the header key "X-USER"

```shell
curl -H "X-USER: kevsuperduperuser" -H "Content-Type: application/json" localhost:8080/cars
```