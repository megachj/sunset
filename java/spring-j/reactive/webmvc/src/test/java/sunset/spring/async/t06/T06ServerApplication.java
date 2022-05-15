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

import java.util.concurrent.CompletableFuture;

@Slf4j
@SpringBootApplication
@EnableAsync
public class T06ServerApplication {

    private static final String PORT_VALUE = "8080";
    private static final String TOMCAT_THREADS_MAX_VALUE = "1";

    private static final String REMOTE_URL1 = "http://localhost:8081/service?req={req}";
    private static final String REMOTE_URL2 = "http://localhost:8081/service2?req={req}";

    @RestController
    public static class MyController {
        // sync, blocking io
        RestTemplate syncRt = new RestTemplate(); // blocking

        // async, blocking io
        // 내부가 요청당 스레드를 1개 생성해서 처리하는 구조, 즉 blocking-io
        // ex) 요청이 동시에 100개 들어오면, 대기 스레드가 100개 만들어짐
        AsyncRestTemplate ioAsyncRt = new AsyncRestTemplate();

        // async, non-blocking io
        // 내부를 nio 네티로 구성, 즉 nonblocking-io
        // ex) 요청이 동시에 100개 들어와도 처리 스레드는 1개임
        AsyncRestTemplate nioAsyncRt = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));

        @Autowired
        MyService myService;

        @GetMapping("/rest/sync")
        public String restSync(int idx) {
            // 2초
            String res = syncRt.getForObject(
                REMOTE_URL1,
                String.class,
                "hello" + idx
            );
            return res;
        }

        @GetMapping("/rest/async/io")
        public ListenableFuture<ResponseEntity<String>> restAsyncIo(int idx) {
            // 2초
            ListenableFuture<ResponseEntity<String>> res = ioAsyncRt.getForEntity(
                REMOTE_URL1,
                String.class,
                "hello" + idx
            );
            return res;
        }

        @GetMapping("/rest/async/nio")
        public ListenableFuture<ResponseEntity<String>> restAsyncNio(int idx) {
            // 2초
            ListenableFuture<ResponseEntity<String>> res = nioAsyncRt.getForEntity(
                REMOTE_URL1,
                String.class,
                "hello" + idx
            );
            return res;
        }

        @GetMapping("/rest/async/nio/deferred")
        public DeferredResult<String> restAsyncNioDeferred(int idx) {
            DeferredResult<String> dr = new DeferredResult<>();

            // 2초
            ListenableFuture<ResponseEntity<String>> f1 = nioAsyncRt.getForEntity(
                REMOTE_URL1,
                String.class,
                "hello" + idx
            );
            f1.addCallback(s -> {
                    dr.setResult(s.getBody() + "/work");
                }, e -> {
                    dr.setErrorResult(e.getMessage());
                }
            );

            return dr;
        }

        @GetMapping("/rest/async/nio/deferred/callback-hell")
        public DeferredResult<String> restAsyncNioDeferredCallbackHell(int idx) {
            DeferredResult<String> dr = new DeferredResult<>();

            // 2초
            ListenableFuture<ResponseEntity<String>> f1 = nioAsyncRt.getForEntity(
                REMOTE_URL1,
                String.class,
                "hello" + idx
            );
            f1.addCallback(s -> {
                    ListenableFuture<ResponseEntity<String>> f2 = nioAsyncRt.getForEntity(
                        REMOTE_URL2,
                        String.class,
                        s.getBody()
                    );
                    f2.addCallback(s2 -> {
                            ListenableFuture<String> f3 = myService.asyncWork(s2.getBody());
                            f3.addCallback(s3 -> {
                                    dr.setResult(s3);
                                }, e3 -> {
                                    dr.setErrorResult(e3.getMessage());
                                }
                            );
                        }, e2 -> {
                            dr.setErrorResult(e2.getMessage());
                        }
                    );
                }, e -> {
                    dr.setErrorResult(e.getMessage());
                }
            );

            return dr;
        }

        @GetMapping("/rest/completion")
        public DeferredResult<String> restCompletion(int idx) {
            DeferredResult<String> dr = new DeferredResult<>();

            Completion.from(nioAsyncRt.getForEntity(REMOTE_URL1, String.class, "hello" + idx))
                .andApply(s -> nioAsyncRt.getForEntity(REMOTE_URL2, String.class, s.getBody()))
                .andApply(s -> myService.asyncWork(s.getBody()))
                .andError(e -> dr.setErrorResult(e.toString()))
                .andAccept(s -> dr.setResult(s));

            return dr;
        }

        @GetMapping("/rest/completable-future")
        public DeferredResult<String> restCompletableFuture(int idx) {
            DeferredResult<String> dr = new DeferredResult<>();

            toCompletableFuture(nioAsyncRt.getForEntity(REMOTE_URL1, String.class, "hello" + idx))
                .thenCompose(s -> {
                    if (s.getBody().contains("10")) {
                        throw new RuntimeException(String.format("ERROR: %s", s.getBody()));
                    }

                    return toCompletableFuture(nioAsyncRt.getForEntity(REMOTE_URL2, String.class, s.getBody()));
                })
                .thenApplyAsync(s2 -> myService.syncWork(s2.getBody()))
                .thenAccept(s3 -> dr.setResult(s3))
                .exceptionally(e -> {
                    dr.setErrorResult(e.getMessage());
                    return null;
                });

            return dr;
        }

        private <T> CompletableFuture<T> toCompletableFuture(ListenableFuture<T> lf) {
            CompletableFuture<T> cf = new CompletableFuture<>();
            lf.addCallback(s -> cf.complete(s), e -> cf.completeExceptionally(e));
            return cf;
        }
    }

    @Component
    public static class MyService {
        @Async("tp")
        public ListenableFuture<String> asyncWork(String req) {
            return new AsyncResult<>(req + "/asyncWork");
        }

        public String syncWork(String req) {
            return req + "/syncWork";
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

        SpringApplication.run(T06ServerApplication.class, args);
    }
}
