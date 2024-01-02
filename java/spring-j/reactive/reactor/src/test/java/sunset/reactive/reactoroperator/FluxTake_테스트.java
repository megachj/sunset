package sunset.reactive.reactoroperator;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class FluxTake_테스트 {

    @DisplayName("async_infinite Flux 2개를 merge")
    @Test
    public void asyncInfiniteFlux_merge() throws InterruptedException {
        log.info("main: start");
        Flux.range(0, 1_000)
            .delayElements(Duration.ofMillis(100), Schedulers.newParallel("delayTimer"))
            .take(Duration.ofSeconds(2), Schedulers.newParallel("takeTimer"))
            .log("main sub") // onSubscribe, request: [Test worker], onNext: [delayTimer], onComplete: [takeTimer]
            .subscribe();

        Thread.sleep(5_000);
        log.info("main: end");
    }
}
