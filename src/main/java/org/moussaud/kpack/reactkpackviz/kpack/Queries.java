package org.moussaud.kpack.reactkpackviz.kpack;

import java.util.List;
import java.util.Map;

import org.moussaud.kpack.reactkpackviz.DataSet;
import org.moussaud.kpack.reactkpackviz.Link;
import org.moussaud.kpack.reactkpackviz.Node;

import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Queries {

    Logger logger = LoggerFactory.getLogger(Queries.class);

    private static final int LINK_WEIGHT = 10;
    private KubernetesClient client;

    public Queries(KubernetesClient client) {
        this.client = client;
    }

    public void images(DataSet ds) {

        var context = new CustomResourceDefinitionContext.Builder()
                .withVersion("v1alpha2")
                .withGroup("kpack.io")
                .withScope("Namespaced")
                .withPlural("images")
                .build();

        var items = client.genericKubernetesResources(context).list().getItems();

        for (GenericKubernetesResource genericKubernetesResource : items) {
            var name = genericKubernetesResource.getMetadata().getName();
            ds.addNode(new Node(name, "image", 1, isResourceReady(genericKubernetesResource)));
            ds.addLink(
                    new Link(name, getAdditionalProperty(genericKubernetesResource, "spec.builder.name"), LINK_WEIGHT));
        }
    }

    public void builders(DataSet ds) {
        var context = new CustomResourceDefinitionContext.Builder()
                .withVersion("v1alpha2")
                .withGroup("kpack.io")
                .withScope("Namespaced")
                .withPlural("builders")
                .build();

        var items = client.genericKubernetesResources(context).list().getItems();
        for (GenericKubernetesResource genericKubernetesResource : items) {
            var name = genericKubernetesResource.getMetadata().getName();
            ds.addNode(new Node(name, "builder", 2, isResourceReady(genericKubernetesResource)));

            ds.addLink(
                    new Link(name, getAdditionalProperty(genericKubernetesResource, "spec.stack.name"), LINK_WEIGHT));
            ds.addLink(
                    new Link(name, getAdditionalProperty(genericKubernetesResource, "spec.store.name"), LINK_WEIGHT));
        }
    }

    public void clusterbuilders(DataSet ds) {
        var context = new CustomResourceDefinitionContext.Builder()
                .withVersion("v1alpha2")
                .withGroup("kpack.io")
                .withPlural("clusterbuilders")
                .build();

        var items = client.genericKubernetesResources(context).list().getItems();
        for (GenericKubernetesResource genericKubernetesResource : items) {
            var name = genericKubernetesResource.getMetadata().getName();
            ds.addNode(new Node(name, "builder", 2, isResourceReady(genericKubernetesResource)));

            ds.addLink(
                    new Link(name, getAdditionalProperty(genericKubernetesResource, "spec.stack.name"), LINK_WEIGHT));
            ds.addLink(
                    new Link(name, getAdditionalProperty(genericKubernetesResource, "spec.store.name"), LINK_WEIGHT));
        }
    }

    public void clusterstacks(DataSet ds) {
        var context = new CustomResourceDefinitionContext.Builder()
                .withVersion("v1alpha2")
                .withGroup("kpack.io")
                .withPlural("clusterstacks")
                .build();

        var items = client.genericKubernetesResources(context).list().getItems();
        for (GenericKubernetesResource genericKubernetesResource : items) {
            var name = genericKubernetesResource.getMetadata().getName();
            ds.addNode(new Node(name, "clusterstacks", 3, isResourceReady(genericKubernetesResource)));

            var buildImage = getAdditionalProperty(genericKubernetesResource, "spec.buildImage.image");
            ds.addNode(new Node((String) buildImage, "buildImage", 4));
            ds.addLink(new Link(name, (String) buildImage, LINK_WEIGHT));

            var runImage = getAdditionalProperty(genericKubernetesResource, "spec.runImage.image");
            ds.addNode(new Node((String) runImage, "runImage", 5));
            ds.addLink(new Link(name, (String) runImage, LINK_WEIGHT));
        }
    }

    public void clusterstores(DataSet ds) {
        CustomResourceDefinitionContext context = new CustomResourceDefinitionContext.Builder()
                .withVersion("v1alpha2")
                .withGroup("kpack.io")
                .withPlural("clusterstores")
                .build();

        var items = client.genericKubernetesResources(context).list().getItems();
        for (GenericKubernetesResource genericKubernetesResource : items) {
            var name = genericKubernetesResource.getMetadata().getName();
            ds.addNode(new Node(name, "clusterstores", 6, isResourceReady(genericKubernetesResource)));

            List<Map<String, Object>> sources = getAdditionalProperty(genericKubernetesResource, "spec.sources");
            for (Map<String, Object> source : sources) {
                String imageName = (String) source.get("image");
                ds.addNode(new Node(imageName, "buildpack", 7));
                ds.addLink(new Link(name, imageName, LINK_WEIGHT));
            }
        }
    }

    private <T> T getAdditionalProperty(GenericKubernetesResource genericKubernetesResource, String path) {
        var properties = (Map<String, Object>) genericKubernetesResource.getAdditionalProperties();
        Map<String, Object> currentMap = properties;

        var items = path.split("\\.");
        var last = items[items.length - 1];

        for (String item : items) {
            if (last.equals(item)) {
                return (T) currentMap.get(last);
            } else {
                currentMap = (Map<String, Object>) currentMap.get(item);
            }
        }
        throw new RuntimeException(path + "Path not found in " + genericKubernetesResource);
    }

    // .status.conditions[?(@.type=="Ready")].status
    private boolean isResourceReady(GenericKubernetesResource genericKubernetesResource) {
        List<Map<String, Object>> conditions = getAdditionalProperty(genericKubernetesResource, "status.conditions");
        var value = "xxxxx";
        for (Map<String, Object> condition : conditions) {
            if (condition.containsKey("type")) {
                String typeValue = (String) condition.get("type");
                if (typeValue.equals("Ready")) {
                    value = (String) condition.get("status");
                }
            }
        }
        if (logger.isDebugEnabled())
            logger.debug("isResourceReady " + genericKubernetesResource.getMetadata().getName() + ":" + value);
        return value.equals("True");
    }

}
