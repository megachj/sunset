package sunset.spring.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class PropertyUtil {
    // Spring Boot 2.6.4 기준
    // application.yaml 로 설정해도 되고, System.setProperty() 로 설정할 수 도 있다.
    public static final String PORT = "server.port";
    public static final String TOMCAT_THREADS_MAX = "server.tomcat.threads.max";

    public static final void logProperties(String... propertyKeys) {
        List<String> properties = Stream.of(propertyKeys)
            .map(key -> String.format("%s: %s", key, System.getProperty(key)))
            .collect(Collectors.toList());

        log.info("System Properties: {}", properties);
    }
}
