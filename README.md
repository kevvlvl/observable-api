# observable-api

A Spring reactive Kotlin written API to showcase various observability concepts

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
curl -H "X-USER: kevuser" localhost:8080/cars
```

## Deploy using k8s

### Build the API using Maven Jib

```shell
./gradlew jibDockerBuild
```