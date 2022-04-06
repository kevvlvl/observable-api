# Operational Guide - Api Unavailable

This document quickly outlines how to troubleshoot one specific issue for a given service.

Following an issue in a monitored environment such as production (as part of post-mortem), it's a good idea to make sure we don't lose this knowledge. It can be wiki pages, doc as code (such as here), or a full-blown knowledge base platform that others can refer to in the future.

This document is divided into a few sections covering the 5W's & 1H: who, what, where, when, why and how

## Audience

This section answers the "Who"

The audience of this guide are the following stakeholders:
- developers
- devops (tooling, cloud ops, etc.)
- ops (sys ops, devs devops in "ops" mode, etc.)

The unavailability of this API may be of interest to any of the above

## Architecture

### The App

This section answers the "What": Name and describe functionally what is this API

| Service        | Description                                                |
|:---------------|:-----------------------------------------------------------|
| Observable-api | REST API that returns the inventory of cars for each model |

This service is integrated with the front-end system of each dealership to globally have visibility on total and available inventory

### Infrastructure

This section answers the "Where": List all information/components to help locate the application (VMs, cloud, containers, serverless, etc.) as well as the dependencies needed to support the API

Deployed on the EKS kubernetes cluster. Details:

| K8S object            | Namespace   | Details                           |
|:----------------------|:------------|:----------------------------------|
| observable-api-deploy | appli-space | API exposes port 8080             |
| observable-db-deploy  | appli-space | API backing-DB, exposes port 5432 |

### Potential causes

This section answers the "When": List likely suspects that can cause this issue under investigation to happen.

Components that can impact this API:
- Postgres Database: Down/unavailable/non-existent/ACL/etc.
- Kubernetes cluster: namespaces security network policies etc.

Errors within the API that can impact this API:
- Unexpected application configuration
- Code bug

### Service Impact

This section answers the "Why": Why is this important to investigate? Gives the Ops a quick impact assessment

**Potential Global Outage** to all consumers of this API: All Dealers nationwide. This service is required for all dealers to query the global inventory. Without this, they cannot secure sales with customers who want to order/reserve a specific model unit.

## Troubleshoot Steps

This section answers the "How" to troubleshoot step by step.
Start the investigation "Backwards": Start the API -> Infrastructure

### 1. Verify application health

Query the GET endpoint to confirm if the API can communicate with the Database and return data

```shell
curl -H "X-USER: opsAdmin" -H "Content-Type: application/json" localhost:8080/cars
```

Is the inventory of cars returned? If not, then verify the health of the API:

```shell
http://localhost:8080/actuator/health
```

We expect these property values in a healthy case

| Property                           | Expected Value | Investigate                                                                          |
|:-----------------------------------|:---------------|:-------------------------------------------------------------------------------------|
| components.readinessState          | UP             | Is PostgreSQL available with the Car table populated, and connectivity with the API? |
| components.livenessState           | UP             | The application context, configuration, beans instantiation could be the issue       |
| components.postgres.status         | UP             | Is PostgreSQL available with the Car table populated, and connectivity with the API? |
| components.postgres.details.result | > 0            | * Verify the count of entries in the Car table                                       |

_Note_: A value of 0 may not be an issue, but this would also mean that there's zero cars inventory in the database!)

### 2. Verify API logs

- Validate that we don't have any ERROR logs
  - Likely culprit of an ERROR: Database connection (changed username/password, connection string, etc.), Application properties values missing or unexpected
- Can you find the "Started ObservableApiApplication in .* seconds" log entry
  - This means the API's Spring Boot server is up (and getting ready to receive traffic based on readinessState)
- Can you find any logged business rules validation logs? (log messages containing the rule codes "CD01" and "CD02" )
  - This means there's business rules applied to the inventory of cars returned

### 3. Verify Database availability

Launch an RDBMS tool (such as pgAdmin, DataGrip, etc.) to connect to the instance. Query one of the tables to confirm availability of the table and data:

```sql
SELECT * FROM car
```

### 4. Verify instances on Kubernetes 

Connect to the cluster, verify namespace
```shell
$ k8s-connect # a fictitious CLI the ops uses to connect to the cluster, setup kube context etc.
...
$ kubectl get ns # List the namespaces
...
$ kubectl get deploy -n appli-space # List the deployments that they are up
...
```
