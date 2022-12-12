package sunset.reactive.reactoroperator;

import static sunset.reactive.support.ReactorLogUtils.SIGNAL_TYPES;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

@Slf4j
public class FluxFlatMap_테스트 {

    private static final Map<Integer, Duration> DURATION_MS_MAP = Map.of(
        1, Duration.ofMillis(400),
        2, Duration.ofMillis(200),
        3, Duration.ofMillis(600),
        4, Duration.ZERO
    );

    @DisplayName("메인스레드에서 flatMap")
    @Test
    public void test1() throws InterruptedException {
        // given
        Flux<Integer> squareFlux = Flux.fromIterable(List.of(1, 2))
            .log("step1", Level.FINE, SIGNAL_TYPES)
            .flatMap(i -> Mono.just(i * i)
                .log("nested step1", Level.FINE, SIGNAL_TYPES)
            )
            .log("step2", Level.FINE, SIGNAL_TYPES);

        // when
        StepVerifier.create(squareFlux)
            .expectNext(1)
            .expectNext(4)
            .verifyComplete();
    }

    @DisplayName("parallel_2 스케줄러에서 publishOn + flatMap + delayElements")
    @Test
    public void test2() throws InterruptedException {
        // given
        Scheduler parallelScheduler = Schedulers.newParallel("pub", 4);

        Flux<Integer> squareFlux = Flux.fromIterable(List.of(1, 2, 3, 4))
            .log("step0", Level.FINE, SIGNAL_TYPES)
            .publishOn(parallelScheduler)
            .log("step1", Level.FINE, SIGNAL_TYPES)
            .flatMap(i -> Mono.just(i * i)
                .log("nested step1", Level.FINE, SIGNAL_TYPES)
                .delayElement(DURATION_MS_MAP.get(i), parallelScheduler)
                //.log("nested step2", Level.FINE, SIGNAL_TYPES)
                , 2
            )
            .log("step2", Level.FINE, SIGNAL_TYPES);

        // when
        StepVerifier.create(squareFlux)
            .expectNext(16)
            .expectNext(4)
            .expectNext(1)
            .expectNext(9)
            .verifyComplete();
    }
}
