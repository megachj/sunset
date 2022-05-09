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
import sunset.spring.utils.PropertyUtil;

import java.util.concurrent.Callable;

/**
 * TOMCAT_THREADS_MAX_VALUE 값을 바꿔가며 테스트를 해본다.
 *
 * jmc(JDK Mission Control) 또는 VisualVM 을 통해 스레드 과정을 확인할 수 있다.
 */
@Slf4j
@SpringBootApplication
@EnableAsync
public class SpringAsyncWebEx {

    private static final String PORT_VALUE = "8080";
    private static final String TOMCAT_THREADS_MAX_VALUE = "100"; // 100, 20, 1

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
                taskExecutor.setThreadNamePrefix("worker-");
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
         * tomcat-thread-1: 2초, http-nio-8080-exec 1개 와 worker 100개
         * tomcat-thread-20: 2초, http-nio-8080-exec 20개 와 worker 100개
         * tomcat-thread-100: 2초, http-nio-8080-exec 100개 와 worker 가 100개
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
        System.setProperty(PropertyUtil.PORT, PORT_VALUE);
        System.setProperty(PropertyUtil.TOMCAT_THREADS_MAX, TOMCAT_THREADS_MAX_VALUE);
        PropertyUtil.logProperties(PropertyUtil.PORT, PropertyUtil.TOMCAT_THREADS_MAX);

        SpringApplication.run(SpringAsyncWebEx.class, args);
    }
}
