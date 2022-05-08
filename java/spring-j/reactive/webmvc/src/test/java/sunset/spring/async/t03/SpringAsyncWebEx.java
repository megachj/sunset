package sunset.spring.async.t03;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@SpringBootApplication
@EnableAsync
public class SpringAsyncWebEx {

    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(100);
        taskExecutor.setQueueCapacity(1000);
        taskExecutor.setMaxPoolSize(200);
        taskExecutor.setThreadNamePrefix("Executor-");
        taskExecutor.initialize();
        return taskExecutor;
    }

    @RestController
    public static class MyController {

        @GetMapping("/callable")
        public Callable<String> callable() throws InterruptedException {
            log.info("callable");
            return () -> {
                log.info("async");
                Thread.sleep(2000);
                return "hello";
            };
        }

        @GetMapping("/notcallable")
        public String notCallable() throws InterruptedException {
            log.info("not callable");
            Thread.sleep(2000);
            return "hello";
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringAsyncWebEx.class, args);
    }
}
