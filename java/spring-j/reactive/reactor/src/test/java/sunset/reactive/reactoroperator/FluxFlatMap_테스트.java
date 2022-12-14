package sunset.reactive.reactoroperator;

import static sunset.reactive.support.ReactorUtils.ALL_SIGNAL_TYPES;
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
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

@Slf4j
public class FluxFlatMap_테스트 {

    private static final long TIME_ALPHA = 1L;

    // 비동기논블로킹 작업 시간을 숫자마다 다르게 주기 위함
    private static final Map<Integer, Duration> DELAY_42513 = Map.of(
        1, Duration.ofMillis(3_000),
        2, Duration.ofMillis(1_000),
        3, Duration.ofMillis(4_000),
        4, Duration.ZERO,
        5, Duration.ofMillis(2_000)
    );

    // AsyncNonBlocking 작업을 처리하는 스케줄러
    private static final Scheduler A_SCHEDULER = Schedulers.newParallel("AAA", 5);

    @DisplayName("flatMap 처리는 순서대로 된다.")
    @Timeout(TIME_ALPHA)
    @Test
    public void test1() {
        // given
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        // when
        Flux<Integer> result = Flux.fromIterable(numbers)
            .log("step1", Level.FINE, ALL_SIGNAL_TYPES)
            .flatMap(Mono::just)
            .log("step2", Level.FINE, ALL_SIGNAL_TYPES);

        // then
        StepVerifier.create(result)
            .expectNext(1, 2, 3, 4, 5)
            .verifyComplete();
    }

    @DisplayName("flatMap 처리는 순서대로 하지만 먼저 받은 응답부터 다음단계로 넘겨준다.")
    @Timeout(4 + TIME_ALPHA)
    @Test
    public void test2() throws InterruptedException {
        // given
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        // when
        Flux<Integer> result = Flux.fromIterable(numbers)
            .log("step1", Level.FINE, ALL_SIGNAL_TYPES)
            .flatMap(i -> Mono.just(i)
                .log("nested1", Level.FINE, ALL_SIGNAL_TYPES)
                .delayElement(DELAY_42513.get(i), A_SCHEDULER)
                .log("nested2", Level.FINE, ALL_SIGNAL_TYPES)
            )
            .log("step2", Level.FINE, ALL_SIGNAL_TYPES);

        // then
        StepVerifier.create(result)
            .expectNext(4, 2, 5, 1, 3)
            .verifyComplete();
    }
}
