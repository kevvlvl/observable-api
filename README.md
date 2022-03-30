# observable-api

A Spring reactive Kotlin written API to showcase various observability concepts

- Spring boot reactive, built using Gradle
- Written in Kotlin for JVM 17
- Using PostgreSQL

## Observability concepts

### Logs

A few best-practices have been implemented here to help various stakeholders

- Prior to the spring boot server startup, we dump all active configuration properties in DEBUG. This can help developers or operations to confirm the application setup
- We log in INFO general information/steps at key places
- We log in INFO business rules traceability that can help QA/analysts/devs
- We log in INFO audit entries: Who (user) queried what (endpoint) when (log timestamp) 

### Traces

We use Spring Cloud Sleuth with an opentelemetry implementation (instead of opentracing/brave). See [Spring Experimental Projects](https://spring-projects-experimental.github.io/spring-cloud-sleuth-otel/docs/current/reference/html/getting-started.html) for more

In the code, there's an example where we create a new child span (created at the service-layer). The Span ID "112eec40fa03de27" is created to visualize a distinct traceability of the Service layer (CarService)

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

#### 2) Run the API

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