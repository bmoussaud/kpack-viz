package org.moussaud.kpack.reactkpackviz;

import java.util.Map;

import org.apache.catalina.core.ApplicationContext;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReactKpackVizController {
    
    @GetMapping("/")
    String index(Map<String, Object> model) {
        model.put("message", "a message");
        model.put("title", "a title");
        model.put("java", "Java " + System.getProperty("java.version"));
        model.put("spring.boot", "Spring Boot " + SpringBootApplication.class.getPackage().getImplementationVersion());
        model.put("spring", "Spring " + ApplicationContext.class.getPackage().getImplementationVersion());
        model.put("os", System.getProperty("os.name") + " " + System.getProperty("os.version"));
        return "index";
    }
}
