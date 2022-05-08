package sunset.spring.async.t03;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Callable;

/**
 * 프로필을 주어서 실행하고, 서버 스레드를 관찰한다.
 * - profiles: tomcat-thread-100, tomcat-thread-20, tomcat-thread-1
 */
@Slf4j
@SpringBootApplication
@EnableAsync
public class SpringAsyncWebEx {

    @Bean
    WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            // 서블릿 비동기 워커스레드 풀 설정
            @Override
            public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
                ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
                taskExecutor.setCorePoolSize(100);
                taskExecutor.setQueueCapacity(50);
                taskExecutor.setMaxPoolSize(200);
                taskExecutor.setThreadNamePrefix("workerThread-");
                taskExecutor.initialize();
                configurer.setTaskExecutor(taskExecutor);
            }
        };
    }

    @RestController
    public static class MyController {

        /**
         * tomcat-thread-20: 10초, http-nio-8080-exec 가 20개 만들어져서 20개 req 씩 처리
         * tomcat-thread-100: 2초, http-nio-8080-exec 가 100개 만들어져서 한번에 처리
         */
        @GetMapping("/sync")
        public String sync() throws InterruptedException {
            log.info("/sync");
            Thread.sleep(2000);
            return "hello";
        }

        /**
         * tomcat-thread-1: 2초, http-nio-8080-exec 1개 와 workerThread 100개
         * tomcat-thread-20: 2초, http-nio-8080-exec 20개 와 workerThread 100개
         * tomcat-thread-100: 2초, http-nio-8080-exec 100개 와 workerThread 가 100개
         */
        @GetMapping("/async/callable")
        public Callable<String> asyncCallable() {
            log.info("/async/callable");
            return () -> {
                log.info("callable");
                Thread.sleep(2000);
                return "hello";
            };
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringAsyncWebEx.class, args);
    }
}
