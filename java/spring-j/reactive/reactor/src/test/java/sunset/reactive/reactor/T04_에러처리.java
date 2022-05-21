package sunset.reactive.reactor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class T04_에러처리 {

    @Test
    public void just_내부에서_예외가_발생했을때() {
        String exceptionMessage = "just는 퍼블리셔를 생성할 때 데이터를 만든다.";
        Throwable thrown = catchThrowable(() -> {
            Flux.just(throwException(exceptionMessage));
        });

        assertThat(thrown).isInstanceOf(IllegalStateException.class)
            .hasMessage(exceptionMessage);
    }

    private Flux<Integer> throwException(String exceptionMessage) {
        throw new IllegalStateException(exceptionMessage);
    }

    @Test
    public void flatMap에서_예외가_던져졌을때() {
        Flux<Integer> publisher = Flux.<Integer>create(sink -> {
                sink.next(1);
                sink.next(-1);
                sink.next(2);
            })
            .flatMap(integer -> toIntegerSequenceWithThrows(integer)
                .log("FlatMap 내부 Flux")
                .onErrorResume(e -> Mono.empty())
            )
            .log("Main Flux");

        StepVerifier.create(publisher)
            .expectNext(1)
            .expectError(IllegalArgumentException.class)
            .verify();
    }

    private Flux<Integer> toIntegerSequenceWithThrows(Integer i) {
        if (i < 0) {
            throw new IllegalArgumentException("음수는 처리할 수 없습니다.");
        }
        return Flux.range(1, i);
    }

    @Test
    public void flatMap에서_에러신호가_발생했을때() {
        Flux<Integer> publisher = Flux.<Integer>create(sink -> {
                sink.next(1);
                sink.next(-1);
                sink.next(2);
            })
            .flatMap(integer -> toIntegerSequenceWithErrorSignal(integer)
                .log("FlatMap 내부 Flux")
                .onErrorResume(e -> Mono.empty())
            )
            .log("Main Flux");

        StepVerifier.create(publisher)
            .expectNext(1)
            .expectNext(2)
            .expectNext(3)
            .expectNext(1)
            .expectNext(2)
            .expectNext(3)
            .expectNext(4)
            .verifyTimeout(Duration.ofSeconds(1));
    }

    private Flux<Integer> toIntegerSequenceWithErrorSignal(Integer i) {
        if (i < 0) {
            return Flux.error(new IllegalArgumentException("음수는 처리할 수 없습니다."));
        }
        return Flux.range(1, i);
    }
}
