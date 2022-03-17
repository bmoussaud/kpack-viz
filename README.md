
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
3. Run `make deploy-k8s-<env> ENV=<env>` to deploy the application
4. Run `make undeploy-k8s-<env> ENV=<env>` to deploy the application


### Repository install with Internet connectivity

First, let's add a new repository:

```shell
tanzu package repository add kpack-viz-repo --url ghcr.io/bmoussaud/kpack-viz-repo:latest -n tanzu-package-repo-global 
```

Wait a few minutes until the repository gets reconciled.
Use this command to get reconciliation status:

```shell
tanzu package repository list -n tanzu-package-repo-global
tanzu package repository get kpack-viz-repo -n tanzu-package-repo-global
```

### Package install

Check that this app is available as a package:

```shell
tanzu package available list kpack-viz.bmoussaud.github.com -n tanzu-package-repo-global
| Retrieving package versions for kpack-viz.bmoussaud.github.com...
  NAME                            VERSION    RELEASED-AT  
  kpack-viz.bmoussaud.github.com  0.1.0-dev  2022-03-09 14:43:23 +0100 CET
```

Keep the package version handy - you'll need it when it comes to package deployment.

Create file `my-values.yml`:

```yaml
#! Set target namespace.
NAMESPACE: kpack-viz
#! Set the public-facing domain used for accessing the application.
DOMAIN: kpack-viz.tools.mytanzu.xyz
```

Edit this file accordingly.

Deploy the package, using the version you have installed:

```shell
tanzu package install kpack-viz --package-name kpack-viz.bmoussaud.github.com --version 0.1.0-dev  -n tanzu-package-repo-global -f my-values.yml
```

Check the status of the deployed package
```shell
tanzu package installed get kpack-viz -n tanzu-package-repo-global
```


When the package install is done, note there's a new namespace accordingly to the `my-values.yaml` file


## Uninstall

```shell
tanzu package installed delete kpack-viz -n tanzu-package-repo-global
tanzu package repository delete kpack-viz-repo -n tanzu-package-repo-global
```

## Dependencies:

* [Bootstrap](https://getbootstrap.com/)
* [vasturiano/force-graph](https://github.com/vasturiano/force-graph)
* [Spring Boot](https://spring.io/projects/spring-boot)


-----------------------

### Repository install with airgapped environment

What if you are have no Internet connection? We've got you covered.

Using [imgpkg](https://carvel.dev/imgpkg/) from the Carvel tools,
you can copy the repository as a TAR file:

```shell
imgpkg copy -b ghcr.io/bmoussaud/kpack-viz-repo --to-tar kpack-viz-repo.tar
```

You end up with a TAR archive on your disk, including all container images
and Kubernetes manifest files you need to deploy this app:
then you are free to copy this archive to your airgapped environment.

Run this command to deploy the repository into your private container registry:

```shell
imgpkg copy --tar kpack-viz-repo.tar --to-repo myregistry.corp.internal/custom/kpack-viz-repo --lock-output bundle.lock
```

A file `bundle.lock` is created: it contains the reference to the relocated package
repository:

```yaml
---
apiVersion: imgpkg.carvel.dev/v1alpha1
kind: BundleLock
bundle:
  image: myregistry.corp.internal/custom/kpack-viz-repo@sha256:809953020331b52264ca01450de568cac0133addc5d1e1620919b528ea0c776a
```

From now on, you must use this new image, which is hosted on your private
container registry. Let's install the package repository:

```shell
tanzu package repository add kpack-viz --url myregistry.corp.internal/custom/kpack-viz-repo@sha256:809953020331b52264ca01450de568cac0133addc5d1e1620919b528ea0c776a -n tanzu-package-repo-global
```

Wait for the repository reconciliation.