
# KPack Viz

This project offers a graphical user interface allowing to display the CRD Resources and their relationships managed by the [kpack Project](https://github.com/pivotal/kpack) defined in a Kubernetes cluster 
* image
* builder
* clusterstack
* clusterstore
* clusterbuilder
* buildpacks.

![kpack-viz screenshot](images/app.png)

## Running the app

### Java

````
git clone <repo>
mvn spring-booot:run
````
note: 
* requires Java 17+
* Use the `KUBECONFIG` environmnet variable

### Docker

````
docker run -p 8080:8080 -e KUBECONFIG=/.kube/config -v <path/to/your/kubeconfig/file.yml>:/.kube/config ghcr.io/bmoussaud/kpack-viz
````

### Kubernetes

* Edit `config-dev/values.yaml` to set your values 
* Run `make deploy-k8s-dev` to deploy `kpack-viz`.
* To undeploy the application, run `make undeploy-k8s-dev`

Note: the makefile relies on `ytt` and `kapp` from [Carvel.Dev](https://carvel.dev/)

if you need to manage other environments (ex `uat`, `prod`),
1. Create a new directory `config-<env>`
2. Add a ``config-<env>/values.yaml` file
3. Run `make deploy-k8s-<env>` to deploy the application
4. Run `make undeploy-k8s-<env>` to deploy the application


## Dependencies:

* [Bootstrap](https://getbootstrap.com/)
* [vasturiano/force-graph](https://github.com/vasturiano/force-graph)
* [Spring Boot](https://spring.io/projects/spring-boot)