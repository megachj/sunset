package sunset.reactive.reactoroperator;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

@Slf4j
public class FluxMap_테스트 {

    @DisplayName("메인스레드에서 map 적용")
    @Test
    public void test1() throws InterruptedException {
        // given
        Flux<Integer> squareFlux = Flux.fromIterable(List.of(1, 2))
            .log("step1")
            .map(i -> i * i)
            .log("step2");

        // when
        StepVerifier.create(squareFlux)
            .expectNext(1)
            .expectNext(4)
            .verifyComplete();
    }

    @DisplayName("스레드1개 스케줄러에서 publishOn으로 map 적용")
    @Test
    public void test2() throws InterruptedException {
        // given
        Flux<Integer> squareFlux = Flux.fromIterable(List.of(1, 2))
            .log("step0")
            .publishOn(Schedulers.newSingle("pub"))
            .log("step1")
            .map(i -> i * i)
            .log("step2");

        // when
        StepVerifier.create(squareFlux)
            .expectNext(1)
            .expectNext(4)
            .verifyComplete();
    }

    @DisplayName("스레드1개 스케줄러에서 subscribeOn으로 map 적용")
    @Test
    public void test3() throws InterruptedException {
        // given
        Flux<Integer> squareFlux = Flux.fromIterable(List.of(1, 2))
            .log("step0")
            .subscribeOn(Schedulers.newSingle("pub"))
            .log("step1")
            .map(i -> i * i)
            .log("step2");

        // when
        StepVerifier.create(squareFlux)
            .expectNext(1)
            .expectNext(4)
            .verifyComplete();
    }

    @DisplayName("스레드2개 스케줄러에서 publishOn으로 map 적용")
    @Test
    public void test4() throws InterruptedException {
        // given
        Flux<Integer> squareFlux = Flux.fromIterable(List.of(1, 2, 3, 4))
            .log("step0")
            .publishOn(Schedulers.newParallel("pub", 2))
            .log("step1")
            .map(i -> i * i)
            .log("step2");

        // when
        StepVerifier.create(squareFlux)
            .expectNext(1)
            .expectNext(4)
            .expectNext(9)
            .expectNext(16)
            .verifyComplete();
    }

    @DisplayName("스레드2개 스케줄러에서 subscribeOn으로 map 적용")
    @Test
    public void test5() throws InterruptedException {
        // given
        Flux<Integer> squareFlux = Flux.fromIterable(List.of(1, 2, 3, 4))
            .log("step0")
            .subscribeOn(Schedulers.newParallel("pub", 2))
            .log("step1")
            .map(i -> i * i)
            .log("step2");

        // when
        StepVerifier.create(squareFlux)
            .expectNext(1)
            .expectNext(4)
            .expectNext(9)
            .expectNext(16)
            .verifyComplete();
    }
}
