# Kubernetes setup

## Local setup

In my case, I need to tell minikube to use KVM as the virtualization. By default, minikube uses the virtualbox driver (which is also less headache to configure than kvm).

### Install Minikube locally using KVM

```shell
$ minikube start --driver=kvm2 --kvm-network=vnet --addons=ingress
minikube v1.25.1 on Opensuse-Tumbleweed
...
...
Done! kubectl is now configured to use "minikube" cluster and "default" namespace by default
$ eval $(minikube docker-env)
```

### List services

Following the deployment of applications, list your exposed services using minikube cli

```shell
~ minikube service list
|---------------|------------------------------------|--------------|-----------------------------|
|   NAMESPACE   |                NAME                | TARGET PORT  |             URL             |
|---------------|------------------------------------|--------------|-----------------------------|
| default       | kubernetes                         | No node port |
| default       | simplejaeger-agent                 | No node port |
| default       | simplejaeger-collector             | No node port |
| default       | simplejaeger-collector-headless    | No node port |
| default       | simplejaeger-query                 | No node port |
| ingress-nginx | ingress-nginx-controller           | http/80      | http://192.168.39.195:31185 |
|               |                                    | https/443    | http://192.168.39.195:31621 |
| ingress-nginx | ingress-nginx-controller-admission | No node port |
| kube-logging  | elasticsearch                      | No node port |
| kube-logging  | kibana                             |         5601 | http://192.168.39.195:30222 |
| kube-system   | kube-dns                           | No node port |
| observability | jaeger-operator-metrics            | No node port |
|---------------|------------------------------------|--------------|-----------------------------|
```

## Install Jaeger as a Kubernetes Operator

```shell
kubectl create -f https://github.com/jaegertracing/jaeger-operator/releases/download/v1.30.0/jaeger-operator.yaml -n observability
kubectl apply -f jaeger-all-in-one.yaml
```

## Install ElasticSearch + FluentD + Kibana (EFK)

```shell
$ kubectl apply -f efk-stack.yaml
```