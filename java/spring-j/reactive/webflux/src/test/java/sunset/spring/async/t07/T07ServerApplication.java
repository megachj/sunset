package sunset.spring.async.t07;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import sunset.springutils.PropertyUtil;

import java.util.concurrent.CompletableFuture;

@Slf4j
@SpringBootApplication
@EnableAsync
public class T07ServerApplication {

    private static final String PORT_VALUE = "8080";

    // webmvc t06 에 있는 RemoteService 를 띄우자.
    private static final String REMOTE_URL1 = "http://localhost:8081/service?req={req}";
    private static final String REMOTE_URL2 = "http://localhost:8081/service2?req={req}";

    @RestController
    public static class MyController {

        @Autowired
        MyService myService;
        WebClient client = WebClient.create();

        @GetMapping("/rest")
        public Mono<String> rest(int idx) {
            return client.get().uri(REMOTE_URL1, idx)
                .retrieve().bodyToMono(String.class)
                .doOnNext(c -> log.info("url1: {}", c))
                .flatMap(res1 ->
                    client.get().uri(REMOTE_URL2, res1)
                        .retrieve().bodyToMono(String.class)
                )
                .doOnNext(c -> log.info("url2: {}", c))
                .flatMap(res2 -> Mono.fromCompletionStage(myService.work(res2)))
                .doOnNext(c -> log.info("myService: {}", c));
        }
    }

    @Component
    public static class MyService {
        @Async
        public CompletableFuture<String> work(String req) {
            return CompletableFuture.completedFuture(req + "/work");
        }
    }

    public static void main(String[] args) {
        System.setProperty(PropertyUtil.PORT, PORT_VALUE);
        PropertyUtil.logProperties(PropertyUtil.PORT);

        SpringApplication.run(T07ServerApplication.class, args);
    }
}
