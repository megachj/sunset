package sunset.spring.webfluxserver01;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
@Slf4j
public class MonoServerApplication {

    // 로그를 보면 스프링이 리턴받은 Mono 를 구독하는 것으로 확인할 수 있다.
    @GetMapping("/hello")
    Mono<String> hello() {
        log.info("pos1");
        String msg = generateHello();
        // Mono.just() 안에 들어가는 것이 먼저 실행되는 것을 알 수 있다.
        Mono<String> m = Mono.just(msg)
            .doOnNext(c -> log.info(c))
            .log();
        log.info("pos2");
        return m;
    }

    @GetMapping("/hello2")
    Mono<String> hello2() {
        log.info("pos1");
        Mono<String> m = Mono.fromSupplier(() -> generateHello())
            .doOnNext(c -> log.info(c))
            .log();
        log.info("pos2");
        return m;
    }

    // Publisher 1개에 Subscriber 는 여러개 있을 수 있다.
    // Publisher 는 cold source, hot source 타입이 있다.
    @GetMapping("/hello3")
    Mono<String> hello3() {
        log.info("pos1");
        Mono<String> m = Mono.fromSupplier(() -> generateHello())
            .doOnNext(c -> log.info(c))
            .log();
        m.subscribe();
        log.info("pos2");
        return m;
    }

    // 스프링에 리턴하는 Mono 객체를 block 하면 IllegalStateException 이 발생한다.
    @GetMapping("/hello4")
    Mono<String> hello4() {
        log.info("pos1");
        Mono<String> m = Mono.just(generateHello())
            .doOnNext(c -> log.info(c))
            .log();
        String msg2 = m.block();
        log.info("pos2" + msg2);
        return m;
    }

    // block() 은 사용하지 않는게 좋은데, 써야한다면 새로운 Mono 객체를 넘겨준다.
    @GetMapping("/hello5")
    Mono<String> hello5() {
        log.info("pos1");
        Mono<String> m = Mono.just(generateHello())
            .doOnNext(c -> log.info(c))
            .log();
        String msg2 = m.block();
        log.info("pos2" + msg2);
        return Mono.just(msg2);
    }

    private String generateHello() {
        log.info("method generateHello()");
        return "Hello Mono";
    }

    public static void main(String[] args) {
        SpringApplication.run(MonoServerApplication.class, args);
    }
}
