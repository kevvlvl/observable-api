# observable-api

This repo showcases observability concepts using a Spring Boot based application

## Tech Stack

| Concept  | Tech                                       |
|:---------|:-------------------------------------------|
| Language | Kotlin + JVM 17                            |
| REST     | Spring Boot Reactive                       |
| DB       | Postgresql + FlywayDB                      |
| Tracing  | Opentracing + Jaeger                       |
| Metrics  | Micrometer                                 |
 | Logging  | Spring Boot default (logback) + Spring AOP |

## Observability concepts

### Logs

A few best-practices have been implemented here to help various stakeholders

- Prior to the spring boot server startup, we dump all active configuration properties in DEBUG. This can help developers or operations to confirm the application setup
- We log in INFO general information/steps at key places to indicate flow progression
- We log in INFO business rules traceability that can help QA/analysts/devs
- We log in INFO audit entries: Who (user) queried what (endpoint) when (log timestamp)

- We log in DEBUG using Spring AOP (Aspect-Oriented Programming = cross-cutting) to log the call flow

### Traces

Note that opentracing is _deprecated_ and effort is being put to evolve OpenTelemetry. For the purpose of this example, opentracing and jaeger is a mature integration while the spring opentelemetry project is currently in experimental phase. For more: [Spring Experimental Projects](https://spring-projects-experimental.github.io/spring-cloud-sleuth-otel/docs/current/reference/html/getting-started.html)

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

#### Working (HTTP 200) GET Call

Pass the notion of a "user" using the header key "X-USER"

- showcases logging for various stakeholders (audit/security, analysts, devs/ops)
- showcases custom spans

```shell
curl -v -H "X-USER: kevsuperduperuser" localhost:8080/cars
```

After every call, see metrics: http://localhost:8080/actuator/prometheus

```
http_server_requests_seconds_count{exception="None",method="GET",outcome="SUCCESS",status="200",uri="/cars",} 1.0
http_server_requests_seconds_sum{exception="None",method="GET",outcome="SUCCESS",status="200",uri="/cars",} 0.131502888
# HELP http_server_requests_seconds_max  
# TYPE http_server_requests_seconds_max gauge
http_server_requests_seconds_max{exception="None",method="GET",outcome="SUCCESS",status="200",uri="/cars",} 0.131502888
```

Metrics of interest here:

| Metric                             | Description                                       |
|:-----------------------------------|:--------------------------------------------------|
| http_server_requests_seconds_count | Sum of requests at this endpoint                  |
| http_server_requests_seconds_max   | Sum of duration of every request at this endpoint |

#### Non-Working (HTTP 5xx) POST call

```shell
curl -v -X POST -H "X-USER: kevsuperduperuser" localhost:8080/cars/reserve
```

- showcases tracing visualisation
- showcases metrics (http5xx)

After every call, see metrics: http://localhost:8080/actuator/prometheus

```
http_server_requests_seconds_count{exception="None",method="POST",outcome="SERVER_ERROR",status="500",uri="/cars/reserve",} 3.0
http_server_requests_seconds_sum{exception="None",method="POST",outcome="SERVER_ERROR",status="500",uri="/cars/reserve",} 0.003941968
# HELP http_server_requests_seconds_max  
# TYPE http_server_requests_seconds_max gauge
http_server_requests_seconds_max{exception="None",method="POST",outcome="SERVER_ERROR",status="500",uri="/cars/reserve",} 0.001958607
```