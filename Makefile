# Copyright 2021 VMware. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

OCI_IMAGE  := ghcr.io/bmoussaud/kpack-viz

clean:
	-rm -rf target

build: build-webui

deploy: deploy-webui

build-webui: target/kpack-viz.jar

# can't use mvnw (maven wrapper) because of https://github.com/jenv/jenv/issues/232
target/kpack-viz.jar:
	mvn -B -pl org.bmoussaud.kpack:kpack-viz clean package

# can't use mvnw (maven wrapper) because of https://github.com/jenv/jenv/issues/232
deploy-webui: target/kpack-viz.jar
	mvn -B -pl org.bmoussaud.kpack:kpack-viz clean package spring-boot:build-image -DskipTests -Dspring-boot.build-image.imageName=${OCI_IMAGE}
	docker push -q ${OCI_IMAGE}
