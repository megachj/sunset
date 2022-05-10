package sunset.spring.async.t06;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sunset.spring.utils.PropertyUtil;

@SpringBootApplication
public class RemoteService {

    private static final String PORT_VALUE = "8081";
    private static final String TOMCAT_THREADS_MAX_VALUE = "1000";

    @RestController
    public static class MyController {
        @GetMapping("/service")
        public String service(String req) throws InterruptedException {
            Thread.sleep(2000);
            return req + "/service";
        }

        @GetMapping("/service2")
        public String service2(String req) throws InterruptedException {
            Thread.sleep(2000);
            return req + "/service2";
        }
    }

    public static void main(String[] args) {
        System.setProperty(PropertyUtil.PORT, PORT_VALUE);
        System.setProperty(PropertyUtil.TOMCAT_THREADS_MAX, TOMCAT_THREADS_MAX_VALUE);
        PropertyUtil.logProperties(PropertyUtil.PORT, PropertyUtil.TOMCAT_THREADS_MAX);

        SpringApplication.run(RemoteService.class, args);
    }
}
