package org.moussaud.kpack.reactkpackviz;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.moussaud.kpack.reactkpackviz.kpack.Queries;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

@RestController
public class KpackAPIController {

    @GetMapping(value = "/api/kpack", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataSet sayHello() {
        return images();
    }

    @GetMapping(value = "/api/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataSet images() {
        DataSet ds = new DataSet();
        Queries queries = new Queries(autoGetK8SClient());
        queries.images(ds);
        queries.builders(ds);
        queries.clusterbuilders(ds);
        queries.clusterstacks(ds);
        queries.clusterstores(ds);
        return ds;
    }

    KubernetesClient autoGetK8SClient() {
        KubernetesClient client = new DefaultKubernetesClient();
        return client;
    }

}
