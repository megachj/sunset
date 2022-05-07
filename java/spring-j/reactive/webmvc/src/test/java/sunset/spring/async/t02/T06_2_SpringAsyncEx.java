package sunset.spring.async.t02;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Slf4j
@SpringBootApplication
@EnableAsync
public class T06_2_SpringAsyncEx {

    @Component
    public static class MyService {
        @Async("tp")
        public ListenableFuture<String> hello() throws InterruptedException {
            log.debug("hello()");
            Thread.sleep(2000);
            return new AsyncResult<>( "Hello");
        }
    }

    @Bean
    ThreadPoolTaskExecutor tp() {
        ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
        te.setCorePoolSize(10); // 기본적으로 만들어두는 스레드 수. 첫 요청이 들어오면 n개를 만듬.
        te.setQueueCapacity(200); // 코어 풀 사이즈를 넘는 요청이 들어오면 큐를 만들어서 요청을 적재한다.
        te.setMaxPoolSize(100); // 큐까지 다 차면 풀 사이즈를 100개로 늘린다.
        te.setThreadNamePrefix("myThread-");
        te.initialize();
        return te;
    }

    public static void main(String[] args) {
        try(ConfigurableApplicationContext c = SpringApplication.run(T06_2_SpringAsyncEx.class, args)) {
        }
    }

    @Autowired MyService myService;

    @Bean
    ApplicationRunner run() {
        return args -> {
            log.info("run()");
            ListenableFuture<String> f = myService.hello();
            f.addCallback(s -> System.out.println("success: " + s), e -> System.out.println("fail: " + e.getMessage()));
            log.info("exit");

             Thread.sleep(3000); // TODO: 데몬스레드인가.. 끝나버림
        };
    }
}
