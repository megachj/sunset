package sunset.reactive.reactoroperator;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

@Slf4j
public class SyncBlocking작업_비동기_테스트 {

    private static final long TIME_ALPHA = 1L;

    // AsyncNonBlocking 작업을 처리하는 스케줄러
    private static final Scheduler BLOCKING_SCHEDULER = Schedulers.newBoundedElastic(10, Integer.MAX_VALUE,
        "blocking");
    private static final Scheduler NONBLOCKING_SCHEDULER = Schedulers.newParallel("nonBlocking", 10);

    @DisplayName("블로킹 작업은 publishOn + map 으로 다른 스케줄러에서 처리")
    @Timeout(value = 3 + TIME_ALPHA)
    @Test
    public void test1() {
        // when
        log.info("result 스트림 생성 시작");

        Mono<Integer> result = Mono.just(1)
            .log("step1") // Test worker
            .publishOn(BLOCKING_SCHEDULER)
            .log("step2") // blocking
            .map(num -> doSyncBlocking(num, 1000L))
            .log("step3") // blocking
            .flatMap(num -> doAsyncNonblocking(num, 2000L))
            .log("step4"); // nonBlocking

        log.info("result 스트림 생성 완료");

        // then
        StepVerifier.create(result)
            .expectNext(1)
            .verifyComplete();
    }

    @DisplayName("블로킹 작업은 publishOn + map 으로 다른 스케줄러에서 처리 / map 을 쓰든, flatMap 을 쓰든 블로킹 작업은 직렬처리 된다.")
    @Timeout(value = 7 + TIME_ALPHA)
    @Test
    public void test2() {
        // when
        log.info("result 스트림 생성 시작");

        Flux<Integer> result = Flux.just(1, 2, 3, 4, 5)
            .log("step1") // Test worker
            .publishOn(BLOCKING_SCHEDULER)
            .log("step2") // blocking
            .map(num -> doSyncBlocking(num, 1000L)) // 블로킹 작업은 map, flatMap 어떤걸 써도 직렬처리 된다.
            .log("step3") // blocking
            .flatMap(num -> doAsyncNonblocking(num, 2000L))
            .log("step4"); // nonBlocking

        log.info("result 스트림 생성 완료");

        // then
        StepVerifier.create(result)
            .expectNext(1, 2, 3, 4, 5)
            .verifyComplete();
    }

    private Integer doSyncBlocking(int num, long delayMs) {
        log.info("syncBlocking start");
        try {
            Thread.sleep(delayMs);
        } catch (Exception ignored) {
        }

        log.info("syncBlocking end");
        return num;
    }

    private Mono<Integer> doAsyncNonblocking(int num, long delayMs) {
        return Mono.just(num)
            .delayElement(Duration.ofMillis(delayMs), NONBLOCKING_SCHEDULER);
    }

}
