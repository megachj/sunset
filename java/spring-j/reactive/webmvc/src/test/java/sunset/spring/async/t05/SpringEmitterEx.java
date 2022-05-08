package sunset.spring.async.t05;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.concurrent.Executors;

@Slf4j
@SpringBootApplication
public class SpringEmitterEx {

    @RestController
    public static class MyController {

        @GetMapping("/emitter")
        public ResponseBodyEmitter emitter() {
            ResponseBodyEmitter emitter = new ResponseBodyEmitter();

            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    for (int i = 1; i <= 50; i++) {
                        emitter.send("<p>Stream " + i + "</p>");
                        Thread.sleep(100);
                    }
                    emitter.complete();
                } catch (Exception e) {}
            });

            return emitter;
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringEmitterEx.class, args);
    }
}
