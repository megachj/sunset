package sunset.reactive.reactoroperator;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

/**
 * 다건 원소(ex: 리스트)에 대해서 각각의 원소를 아래 작업을 병렬적으로 진행하는 것을 보이는 테스트를 설계한다.<br>
 *     1. AsyncNonBlocking 작업(실제 예: webClient 로 API 호출)<br>
 *     2. SyncBlocking 작업(실제 예: JDBC DB 작업)<br>
 */
@Slf4j
public class SyncBlocking작업_병렬실행_테스트 {

    private static final long TIME_ALPHA = 1L;

    // api(비동기논블로킹 작업) 응답시간을 숫자마다 다르게 주기 위함
    private static final Map<Integer, Duration> DELAY_42513 = Map.of(
        1, Duration.ofMillis(3_000),
        2, Duration.ofMillis(1_000),
        3, Duration.ofMillis(4_000),
        4, Duration.ZERO,
        5, Duration.ofMillis(2_000)
    );

    // AsyncNonBlocking 작업을 처리하는 스케줄러
    private static final Scheduler API_SCHEDULER = Schedulers.newParallel("api", 5);
    // SyncBlocking 작업을 처리하는 스케줄러
    private static final Scheduler DB_SCHEDULER = Schedulers.newParallel("db", 5);

    @DisplayName("논블로킹 작업을 하고, 동기블로킹 작업 전에 parallel().runOn() 을 적용하면 병렬로 동작한다.")
    @Timeout(6 + TIME_ALPHA) // 가장 오래걸리는 숫자가 3으로 수행되기까지 6초(api 응답 4초 + db 쿼리 2초) 소요
    @Test
    public void 병렬_실행__when__동기블로킹_작업_전에_parallel을_적용() {
        // chore
        // SignalType[] signalTypes = ReactorLogUtils.ALL_SIGNAL_TYPES;
        SignalType[] signalTypes = {SignalType.ON_NEXT};

        // given
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        // when
        ParallelFlux<Integer> result = Flux.fromIterable(numbers)
            .flatMap(this::requestApiWithAsyncNonBlocking)
            .log("afterApi", Level.FINE, signalTypes) // api 스케줄러
            .parallel(numbers.size())
            .runOn(DB_SCHEDULER)
            .log("beforeDb", Level.FINE, signalTypes) // db 스케줄러
            .flatMap(num -> requestDbWithSyncBlocking(num, 2_000L))
            .log("afterDb", Level.FINE, signalTypes) // db 스케줄러
            ;

        // then
        StepVerifier.create(result)
            .expectNext(4, 2, 5, 1, 3)
            .verifyComplete();
    }

    @DisplayName("논블로킹 작업을 하고, 동기블로킹 작업을 map 으로 연결하면 직렬로 동작한다.")
    @Timeout(10 + TIME_ALPHA) // 동기블로킹 작업이 직렬로 수행되므로 10초(2초씩 5번) 소요
    @Test
    public void 직렬_실행__when__동기블로킹_작업을_map_적용() {
        // chore
        // SignalType[] signalTypes = ReactorLogUtils.ALL_SIGNAL_TYPES;
        SignalType[] signalTypes = {SignalType.ON_NEXT};

        // given
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        // when
        Flux<Integer> result = Flux.fromIterable(numbers)
            .flatMap(this::requestApiWithAsyncNonBlocking)
            .log("afterApi", Level.FINE, signalTypes) // api 스케줄러
            .publishOn(DB_SCHEDULER)
            .log("beforeDb", Level.FINE, signalTypes) // db 스케줄러
            .map(num -> requestDbWithSyncBlocking(num, 2_000L).block())
            .log("afterDb", Level.FINE, signalTypes) // db 스케줄러
            ;

        // then
        StepVerifier.create(result)
            .expectNext(4, 2, 5, 1, 3)
            .verifyComplete();
    }

    @DisplayName("논블로킹 작업을 하고, 동기블로킹 작업을 flatMap 으로 연결하면 직렬로 동작한다.")
    @Timeout(10 + TIME_ALPHA) // 동기블로킹 작업이 직렬로 수행되므로 10초(2초씩 5번) 소요
    @Test
    public void 직렬_실행__when__동기블로킹_작업을_flatMap_적용() {
        // chore
        // SignalType[] signalTypes = ReactorLogUtils.ALL_SIGNAL_TYPES;
        SignalType[] signalTypes = {SignalType.ON_NEXT};

        // given
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        // when
        Flux<Integer> result = Flux.fromIterable(numbers)
            .flatMap(this::requestApiWithAsyncNonBlocking)
            .log("afterApi", Level.FINE, signalTypes) // api 스케줄러
            .publishOn(DB_SCHEDULER)
            .log("beforeDb", Level.FINE, signalTypes) // db 스케줄러
            .flatMap(num -> requestDbWithSyncBlocking(num, 2_000L))
            .log("afterDb", Level.FINE, signalTypes) // db 스케줄러
            ;

        // then
        StepVerifier.create(result)
            .expectNext(4, 2, 5, 1, 3)
            .verifyComplete();
    }

    private Mono<Integer> requestApiWithAsyncNonBlocking(int num) {
        return Mono.just(num)
            .delayElement(DELAY_42513.get(num), API_SCHEDULER);
    }

    private Mono<Integer> requestDbWithSyncBlocking(int num, long queryDelayMs) {
        try {
            Thread.sleep(queryDelayMs);
        } catch (Exception ignored) {
        }

        return Mono.just(num);
    }
}
