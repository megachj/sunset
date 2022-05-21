package sunset.reactive.reactor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class T04_에러처리 {

    @DisplayName("just 에서 예외가 던져지면 error 신호가 아니라 그냥 예외가 던져진다.")
    @Test
    public void just_throws() {
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

    @DisplayName("flatMap 에서 예외가 던져지면 바깥 스트림에 error 신호가 발생한다.")
    @Test
    public void flatMap_throws() {
        Flux<Integer> publisher = Flux.<Integer>create(sink -> {
                sink.next(1);
                sink.next(-1);
                sink.next(2);
            })
            .flatMap(integer -> toIntegerSequenceWithThrows(integer)
                .log("InnerFlatMapRxStream")
                .onErrorResume(e -> Mono.empty())
            )
            .log("MainRxStream");

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

    @DisplayName("flatMap 에서 error 신호를 발생시키고 그걸 잡으면, 바깥 스트림에는 해당 error 신호가 발생하지 않게 할 수 있다.")
    @Test
    public void flatMap_error_resume() {
        Flux<Integer> publisher = Flux.<Integer>create(sink -> {
                sink.next(1);
                sink.next(-1);
                sink.next(2);
            })
            .flatMap(integer -> toIntegerSequenceWithErrorSignal(integer)
                .log("InnerFlatMapRxStream")
                .onErrorResume(e -> Mono.empty())
            )
            .log("MainRxStream");

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
