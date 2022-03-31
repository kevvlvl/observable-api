# Operational Guide - Api Unavailable

## Audience

This section answers the "Who"

The audience of this guide are the following:
- devs
- devops
- ops

These roles may need to investigate why the API is unavailable/unresponsive

## Context

### API

This section answers the "What": Describe functionally what is this API

| Service        | Description                                                |
|:---------------|:-----------------------------------------------------------|
 | Observable-api | REST API that returns the inventory of cars for each model |

This service is integrated with the front-end system of each dealership to globally have visibility on total and available inventory

### Infrastructure

This section answers the "Where": List all components that can help give a quick overview of the dependent architecture that supports this service which is currently under investigation

Deployed on kubernetes cluster

| K8S object            | Namespace   | Details                           |
|:----------------------|:------------|:----------------------------------|
| observable-api-deploy | appli-space | API exposes port 8080             |
 | observable-db-deploy  | appli-space | API backing-DB, exposes port 5432 | 

### Potential causes

This section answers the "When": List likely suspects that can cause this issue under investigation to happen

Components that can impact this API:
- Postgres Database: Down/unavailable/non-existent/ACL/etc.
- Kubernetes cluster: namespaces security network policies etc.

Errors within the API that can impact this API:
- Unexpected application configuration
- Code bug 

### Why

**Potential Global Outage** to all consumers of this API: All Dealers nationwide

This service is required for all dealers to query the global inventory. Without this, they cannot secure sales with customers who want to order/reserve a specific model unit.

## Troubleshoot Steps

This section answers the "How" to troubleshoot step by step.
Start the investigation "Backwards": Start the API -> Infrastructure

### 1. Verify application availability

Verify that the application is up by testing a GET endpoint:

```shell
curl -H "X-USER: opsAdmin" -H "Content-Type: application/json" localhost:8080/cars
```

### 2. Verify API logs

- Validate that we don't have any ERROR logs
  - Likely culprit of an ERROR here: Database connection (changed username/password, connection string, etc.)
- Can you find the "Started ObservableApiApplication in .* seconds" log entry
  - This means the API's Spring Boot server is up (from an HTTP 8080 standpoint)
- Can you find any logged business rules validation logs? (log messages containing the rule codes "CD01" and "CD02" )
  - This means there's business rules applied to cars returned by the inventory database
  

### 3. Verify Database availability

Launch DataGrip to connect to the instance. Query one of the tables to confirm availability of the table and data:

```sql
SELECT * FROM car
```

### 4. Verify deployments on kubernetes

Connect to the cluster, verify namespace
```shell
$ k8s-connect # a fictitious CLI the ops uses to connect to the cluster 
...
$ kubectl get ns # List the namespaces
...
$ kubectl get deploy -n appli-space # List the deployments that they are up
...
```

