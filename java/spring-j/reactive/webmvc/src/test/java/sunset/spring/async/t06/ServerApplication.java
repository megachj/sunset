package sunset.spring.async.t06;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import sunset.spring.utils.PropertyUtil;

@Slf4j
@SpringBootApplication
public class ServerApplication {

    private static final String PORT_VALUE = "8080";
    private static final String TOMCAT_THREADS_MAX_VALUE = "1";

    @RestController
    public static class MyController {
        RestTemplate rt = new RestTemplate(); // blocking
        AsyncRestTemplate art = new AsyncRestTemplate(); // non-blocking

        @GetMapping("/rest/sync")
        public String restSync(int idx) {
            String res = rt.getForObject("http://localhost:8081/service?req={req}", String.class, "hello" + idx);
            return res;
        }

        @GetMapping("/rest/async")
        public ListenableFuture<ResponseEntity<String>> restAsync(int idx) {
            ListenableFuture<ResponseEntity<String>> res = art.getForEntity("http://localhost:8081/service?req={req}", String.class, "hello" + idx);
            return res;
        }
    }

    public static void main(String[] args) {
        System.setProperty(PropertyUtil.PORT, PORT_VALUE);
        System.setProperty(PropertyUtil.TOMCAT_THREADS_MAX, TOMCAT_THREADS_MAX_VALUE);
        PropertyUtil.logProperties(PropertyUtil.PORT, PropertyUtil.TOMCAT_THREADS_MAX);

        SpringApplication.run(ServerApplication.class, args);
    }
}
