package sunset.spring.async.t06;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import sunset.spring.utils.PropertyUtil;

@Slf4j
@SpringBootApplication
@EnableAsync
public class ServerApplication {

    private static final String PORT_VALUE = "8080";
    private static final String TOMCAT_THREADS_MAX_VALUE = "1";

    @RestController
    public static class MyController {
        RestTemplate rt = new RestTemplate(); // blocking

        // 내부가 요청당 스레드를 1개 생성해서 처리하는 구조, 즉 blocking-io
        // ex) 요청이 동시에 100개 들어오면, 대기 스레드가 100개 만들어짐
        AsyncRestTemplate ioArt = new AsyncRestTemplate();

        // 내부를 nio 네티로 구성, 즉 nonblocking-io
        // ex) 요청이 동시에 100개 들어와도 처리 스레드는 1개임
        AsyncRestTemplate nioArt = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));

        @Autowired
        MyService myService;

        @GetMapping("/rest/sync")
        public String restSync(int idx) {
            // 2초
            String res = rt.getForObject(
                "http://localhost:8081/service?req={req}",
                String.class,
                "hello" + idx
            );
            return res;
        }

        @GetMapping("/rest/async/io")
        public ListenableFuture<ResponseEntity<String>> restAsyncIo(int idx) {
            // 2초
            ListenableFuture<ResponseEntity<String>> res = ioArt.getForEntity(
                "http://localhost:8081/service?req={req}",
                String.class,
                "hello" + idx
            );
            return res;
        }

        @GetMapping("/rest/async/nio")
        public ListenableFuture<ResponseEntity<String>> restAsyncNio(int idx) {
            // 2초
            ListenableFuture<ResponseEntity<String>> res = nioArt.getForEntity(
                "http://localhost:8081/service?req={req}",
                String.class,
                "hello" + idx
            );
            return res;
        }

        @GetMapping("/rest/async/nio/deferred")
        public DeferredResult<String> restAsyncNioDeferred(int idx) {
            DeferredResult<String> dr = new DeferredResult<>();

            // 2초
            ListenableFuture<ResponseEntity<String>> f1 = nioArt.getForEntity(
                "http://localhost:8081/service?req={req}",
                String.class,
                "hello" + idx
            );
            f1.addCallback(
                s -> {
                    dr.setResult(s.getBody() + "/work");
                },
                e -> {
                    dr.setErrorResult(e.getMessage());
                }
            );

            return dr;
        }

        @GetMapping("/rest/async/nio/deferred/callback-hell")
        public DeferredResult<String> restAsyncNioDeferredCallbackHell(int idx) {
            DeferredResult<String> dr = new DeferredResult<>();

            // 2초
            ListenableFuture<ResponseEntity<String>> f1 = nioArt.getForEntity(
                "http://localhost:8081/service?req={req}",
                String.class,
                "hello" + idx
            );
            f1.addCallback(
                s -> {
                    // 2초
                    ListenableFuture<ResponseEntity<String>> f2 = nioArt.getForEntity(
                        "http://localhost:8081/service2?req={req}",
                        String.class,
                        s.getBody()
                    );
                    f2.addCallback(
                        s2 -> {
                            ListenableFuture<String> f3 = myService.work(s2.getBody());
                            f3.addCallback(
                                s3 -> {
                                    dr.setResult(s3);
                                },
                                e3 -> {
                                    dr.setErrorResult(e3.getMessage());
                                }
                            );
                        },
                        e2 -> {
                            dr.setErrorResult(e2.getMessage());
                        }
                    );
                },
                e -> {
                    dr.setErrorResult(e.getMessage());
                }
            );

            return dr;
        }
    }

    @Component
    public static class MyService {
        @Async("tp")
        public ListenableFuture<String> work(String req) {
            return new AsyncResult<>(req + "/asyncwork");
        }
    }

    @Bean
    ThreadPoolTaskExecutor tp() {
        ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
        te.setCorePoolSize(1);
        te.setMaxPoolSize(1);
        te.setThreadNamePrefix("myThread-");
        te.initialize();
        return te;
    }

    public static void main(String[] args) {
        System.setProperty(PropertyUtil.PORT, PORT_VALUE);
        System.setProperty(PropertyUtil.TOMCAT_THREADS_MAX, TOMCAT_THREADS_MAX_VALUE);
        PropertyUtil.logProperties(PropertyUtil.PORT, PropertyUtil.TOMCAT_THREADS_MAX);

        SpringApplication.run(ServerApplication.class, args);
    }
}
