# Copyright 2021 VMware. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

OCI_IMAGE  := ghcr.io/bmoussaud/kpack-viz

ENV := dev

KAPP_FLAGS:= --diff-changes --yes 

APP:= kpack-viz-$(ENV)

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


deploy-k8s-$(ENV): .generated/$(ENV)
	@printf "`tput bold`= Apply $(ENV) Kubernetes configuration $@`tput sgr0`\n"	
	kapp deploy $(KAPP_FLAGS) -a $(APP) -f $<
	

undeploy-k8s-$(ENV): 
	@printf "`tput bold`= Delete Kubernetes configuration $@`tput sgr0`\n"	
	kapp delete  $(KAPP_FLAGS) -a $(APP)

.generated/$(ENV): config-$(ENV)/values.yaml
	@printf "`tput bold`= Generate $(ENV) Kubernetes configuration $@`tput sgr0`\n"
	ytt --ignore-unknown-comments -f config -f config-$(ENV) -f config-access --output-files $@


	
