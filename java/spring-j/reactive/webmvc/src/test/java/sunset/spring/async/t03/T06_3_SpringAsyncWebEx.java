package sunset.spring.async.t03;

import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@SpringBootApplication
//@EnableWebMvc
@EnableAsync
public class T06_3_SpringAsyncWebEx {

    @RestController
    public static class MyController {

        @GetMapping("/async")
        public Callable<String> async() throws InterruptedException {
            log.info("callable");
            return () -> {
                log.info("async");
                Thread.sleep(2000);
                return "hello";
            };
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(T06_3_SpringAsyncWebEx.class, args);
    }
}
