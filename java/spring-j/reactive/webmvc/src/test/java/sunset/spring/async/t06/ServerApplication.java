package sunset.spring.async.t06;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import sunset.spring.utils.PropertyUtil;

@Slf4j
@SpringBootApplication
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

        @GetMapping("/rest/sync")
        public String restSync(int idx) {
            String res = rt.getForObject(
                "http://localhost:8081/service?req={req}",
                String.class,
                "hello" + idx
            );
            return res;
        }

        @GetMapping("/rest/async/io")
        public ListenableFuture<ResponseEntity<String>> restAsyncIo(int idx) {
            ListenableFuture<ResponseEntity<String>> res = ioArt.getForEntity(
                "http://localhost:8081/service?req={req}",
                String.class,
                "hello" + idx
            );
            return res;
        }

        @GetMapping("/rest/async/nio")
        public ListenableFuture<ResponseEntity<String>> restAsyncNio(int idx) {
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

            ListenableFuture<ResponseEntity<String>> f1 = nioArt.getForEntity(
                "http://localhost:8081/service?req={req}",
                String.class,
                "hello" + idx
            );
            f1.addCallback(
                s -> {
                    ListenableFuture<ResponseEntity<String>> f2 = nioArt.getForEntity(
                        "http://localhost:8081/service2?req={req}",
                        String.class,
                        s.getBody()
                    );
                    f2.addCallback(
                        s2 -> {
                            dr.setResult(s2.getBody());
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

    public static void main(String[] args) {
        System.setProperty(PropertyUtil.PORT, PORT_VALUE);
        System.setProperty(PropertyUtil.TOMCAT_THREADS_MAX, TOMCAT_THREADS_MAX_VALUE);
        PropertyUtil.logProperties(PropertyUtil.PORT, PropertyUtil.TOMCAT_THREADS_MAX);

        SpringApplication.run(ServerApplication.class, args);
    }
}
