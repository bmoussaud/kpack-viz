# Copyright 2021 VMware. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

APP:=kpack-viz
APP_VERSION := 0.1.0-dev
APP_IMAGE  := ghcr.io/bmoussaud/$(APP)
PKG_IMAGE := ghcr.io/bmoussaud/$(APP)-package
REPO_IMAGE := ghcr.io/bmoussaud/$(APP)-repo

ENV := dev
SOURCE_REV := $(shell git rev-parse --short HEAD)
SOURCE_URL := $(shell git remote get-url origin)
BUILD_DATE := $(shell date +"%Y-%m-%dT%TZ")


KAPP_FLAGS:= --diff-changes --yes 

APP:= $(APP)-$(ENV)

CARVEL_BINARIES := ytt kbld imgpkg kapp

run-spike:
	python3 -m http.server

clean:
	rm -rf target pkg/.imgpkg pkg/config pkg/package.yaml repo


build: target/kpack-viz.jar
	ytt -f package.tpl.yaml -v app.version=${APP_VERSION} -v "releaseDate=${BUILD_DATE}" > pkg/package.yaml
	rm -rf pkg/config pkg/.imgpkg && cp -a config pkg/
	mvn -B -pl org.bmoussaud.kpack:kpack-viz clean package spring-boot:build-image -DskipTests -Dspring-boot.build-image.imageName=${OCI_IMAGE}	

deploy: deploy-webui

build-webui: target/kpack-viz.jar

# can't use mvnw (maven wrapper) because of https://github.com/jenv/jenv/issues/232
target/kpack-viz.jar:
	mvn -B -pl org.bmoussaud.kpack:kpack-viz clean package

# can't use mvnw (maven wrapper) because of https://github.com/jenv/jenv/issues/232
deploy-webui: target/kpack-viz.jar
	mvn -B -pl org.bmoussaud.kpack:kpack-viz clean package spring-boot:build-image -DskipTests -Dspring-boot.build-image.imageName=${OCI_IMAGE}
	

deploy-k8s-$(ENV): .generated/$(ENV)
	@printf "`tput bold`= Apply $(ENV) Kubernetes configuration $@`tput sgr0`\n"	
	kapp deploy $(KAPP_FLAGS) -a $(APP) -f $<
	

undeploy-k8s-$(ENV): 
	@printf "`tput bold`= Delete Kubernetes configuration $@`tput sgr0`\n"	
	kapp delete  $(KAPP_FLAGS) -a $(APP)

.generated/$(ENV): config/*.yaml config-$(ENV)/values.yaml 	
	@printf "`tput bold`= Generate $(ENV) Kubernetes configuration $@`tput sgr0`\n"
	ytt --ignore-unknown-comments -f config -f config-$(ENV) -f config-access --output-files $@


check-carvel:
	$(foreach exec,$(CARVEL_BINARIES),\
		$(if $(shell which $(exec)),,$(error "'$(exec)' not found. Carvel toolset is required. See instructions at https://carvel.dev/#install")))


push: check-carvel build # Push packages.
	docker push -q ${APP_IMAGE}:latest
	docker tag  ${APP_IMAGE}:latest ${APP_IMAGE}:${APP_VERSION}
	docker push ${APP_IMAGE}:${APP_VERSION}	

	mkdir pkg/.imgpkg && ytt -f pkg/config | kbld -f- --imgpkg-lock-output pkg/.imgpkg/images.yml && \
		imgpkg push --bundle ${PKG_IMAGE}:${APP_VERSION} --file pkg/

	mkdir -p repo/packages && ytt -f pkg/package.yaml -f pkg/metadata.yaml -v app.version=${APP_VERSION} -v "releaseDate=${BUILD_DATE}" > repo/packages/$(APP).yaml

	rm -rf repo/.imgpkg && mkdir repo/.imgpkg && kbld -f repo/packages --imgpkg-lock-output repo/.imgpkg/images.yml && \
		imgpkg push --bundle ${REPO_IMAGE} --file repo/


	
