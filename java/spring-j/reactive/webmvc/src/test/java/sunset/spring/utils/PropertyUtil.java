package sunset.spring.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Spring Boot v2.6.4 기준
 *
 * 설정하는 법
 *  1. application.yaml 에서 설정
 *  2. main 메서드에서 System.setProperty() 로 설정
 */
@Slf4j
public class PropertyUtil {
    public static final String PORT = "server.port";
    public static final String TOMCAT_THREADS_MAX = "server.tomcat.threads.max";

    public static final void logProperties(String... propertyKeys) {
        List<String> properties = Stream.of(propertyKeys)
            .map(key -> String.format("%s: %s", key, System.getProperty(key)))
            .collect(Collectors.toList());

        log.info("System Properties: {}", properties);
    }
}
