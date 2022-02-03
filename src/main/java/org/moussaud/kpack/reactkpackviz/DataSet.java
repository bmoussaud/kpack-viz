package org.moussaud.kpack.reactkpackviz;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSet {

    static final Logger logger = LoggerFactory.getLogger(DataSet.class);

    private final TreeSet<Node> nodes = new TreeSet<>();
    private final List<Link> links = new ArrayList<>();

    public DataSet addNode(Node node) {
        if (logger.isDebugEnabled())
            logger.debug("add " + node);
        this.nodes.add(node);
        return this;
    }

    public DataSet addLink(Link link) {
        if (logger.isDebugEnabled())
            logger.debug("add " + link);
        this.links.add(link);
        return this;
    }

    public List<Node> getNodes() {
        List<Node> list_of_nodes = new ArrayList<>();
        list_of_nodes.addAll(nodes);
        return list_of_nodes;
    }

    public List<Link> getLinks() {
        return links;
    }

    public static DataSet getDS1() {
        DataSet ds = new DataSet();
        ds
                .addNode(new Node("cnb-springboot-image", "image", 1))

                .addNode(new Node("springboot-builder-11.0.10", "builder", 2))
                .addNode(new Node("springboot-builder-11.0.12", "builder", 2))
                .addNode(new Node("springboot-builder-11.0.12-cve", "builder", 2))

                .addNode(new Node("awesomedemo-stack", "stack", 3))
                .addNode(new Node("awesomedemo-store-v1", "store", 4))
                .addNode(new Node("awesomedemo-store-v2", "store", 4))
                .addLink(new Link("cnb-springboot-image", "springboot-builder-11.0.10", 1))
                .addLink(new Link("springboot-builder-11.0.10", "awesomedemo-stack", 1))
                .addLink(new Link("springboot-builder-11.0.10", "awesomedemo-store-v1", 1))
                .addLink(new Link("springboot-builder-11.0.12", "awesomedemo-stack", 1))
                .addLink(new Link("springboot-builder-11.0.12", "awesomedemo-store-v2", 1))
                .addLink(new Link("springboot-builder-11.0.12-cve", "awesomedemo-stack", 1))
                .addLink(new Link("springboot-builder-11.0.12-cve", "awesomedemo-store-v2", 1));
        return ds;
    }
}
