package sunset.reactive.reactoroperator;

import static sunset.reactive.support.ReactorUtils.ALL_SIGNAL_TYPES;
import java.util.List;
import java.util.logging.Level;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class FluxDoOnNext_테스트 {

    private static final long TIME_ALPHA = 1L;

    @DisplayName("doOnNext 는 전단계에서 진행된 스레드에서 그대로 실행된다.")
    @Timeout(2 + TIME_ALPHA)
    @Test
    public void test1() {
        // given
        List<Integer> numbers = List.of(1, 2);

        Flux<Integer> result = Flux.fromIterable(numbers)
            .log("step1", Level.FINE, ALL_SIGNAL_TYPES)
            .flatMap(Mono::just)
            .doOnNext(next -> {
                log.info("doOnNext: {}", next);
                try {
                    Thread.sleep(1000L);
                } catch (Exception ignored) {}
            })
            .log("step2", Level.FINE, ALL_SIGNAL_TYPES);

        // when
        StepVerifier.create(result)
            .expectNext(1)
            .expectNext(2)
            .verifyComplete();
    }
}
