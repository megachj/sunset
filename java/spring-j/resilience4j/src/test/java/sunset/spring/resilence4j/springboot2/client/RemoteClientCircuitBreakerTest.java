package sunset.spring.resilence4j.springboot2.client;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpServerErrorException;
import sunset.spring.resilence4j.springboot2.Resilience4jTestApplication;
import sunset.spring.resilience4j.springboot2.internal.circuitbreaker.CircuitBreakerUtils;
import sunset.spring.resilience4j.springboot2.internal.circuitbreaker.MyCircuitBreakerConfig;
import sunset.spring.resilience4j.springboot2.internal.client.RemoteClient;
import sunset.spring.resilience4j.springboot2.internal.client.RemoteClientBreakable;
import sunset.spring.resilience4j.springboot2.internal.exception.IgnoredException;
import sunset.spring.resilience4j.springboot2.internal.exception.RecordedException;

import java.time.Duration;

import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {Resilience4jTestApplication.class})
@Import(RemoteClientCircuitBreakerTest.TestCircuitBreakerConfig.class)
public class RemoteClientCircuitBreakerTest {

    private static final int SLOW_THRESHOLD_MILLIS = 50;

    @TestConfiguration
    public static class TestCircuitBreakerConfig {
        @Bean(name = MyCircuitBreakerConfig.CONFIG_BEAN_NAME)
        public CircuitBreakerConfig testCircuitBreakerConfig(CircuitBreakerRegistry circuitBreakerRegistry) {
            CircuitBreakerConfig circuitBreakerConfig =
                    CircuitBreakerConfig.from(circuitBreakerRegistry.getDefaultConfig())
                            .slidingWindow(4, 4, CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                            .slowCallDurationThreshold(Duration.ofMillis(50)) // ???????????? ??????
                            .waitDurationInOpenState(Duration.ofSeconds(60)) // OPEN ?????? HALF_OPEN ?????? ????????? ?????? ??????
                            .ignoreExceptions(IgnoredException.class) // ignoreExceptions ??? ?????? ??????????????? ??????.
                            .recordException(ex -> false) // ??? ????????? ????????? recordExceptions ??? record(fail) ????????????. ???????????? ?????? success ??????.
                            .recordExceptions(RecordedException.class)
                            .build();
            return circuitBreakerConfig;
        }
    }

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    private RemoteClientBreakable remoteClientBreakable;
    @MockBean
    private RemoteClient remoteClient;

    private CircuitBreaker circuitBreaker;
    private CircuitBreaker.Metrics metrics;

    @BeforeEach
    public void initStubbing() {
        circuitBreaker = circuitBreakerRegistry.circuitBreaker(MyCircuitBreakerConfig.CIRCUIT_BREAKER_NAME);
        circuitBreaker.reset(); // ?????? ????????? ?????? ?????? ???????????? ??????, ????????? ?????????
        metrics = circuitBreaker.getMetrics();

        // given
        when(remoteClient.doSuccess()).thenReturn("OK");
        when(remoteClient.doException(400)).thenThrow(new IgnoredException());
        when(remoteClient.doException(500)).thenThrow(new RecordedException());
        when(remoteClient.doException(502)).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        when(remoteClient.doLatency()).thenAnswer(invocation -> {
            try {
                Thread.sleep(SLOW_THRESHOLD_MILLIS + 50);
            } catch (Exception ignored) {}
            return "LATENCY OK";
        });
    }

    @Test
    public void openStateFallback_exception_test() {
        // when: ???????????? ?????? ??????
        Assertions.catchThrowable(() -> remoteClientBreakable.doException(400)); // ignore, CLOSED
        Assertions.catchThrowable(() -> remoteClientBreakable.doException(400)); // ignore, CLOSED
        remoteClientBreakable.doSuccess(); // success, CLOSED
        remoteClientBreakable.doSuccess(); // success, CLOSED
        Assertions.catchThrowable(() -> remoteClientBreakable.doException(502)); // success, CLOSED
        Assertions.catchThrowable(() -> remoteClientBreakable.doException(502)); // success, CLOSED
        Assertions.catchThrowable(() -> remoteClientBreakable.doException(500)); // fail, CLOSED
        Assertions.catchThrowable(() -> remoteClientBreakable.doException(500)); // fail, CLOSED -> OPEN
        remoteClientBreakable.doSuccess(); // not permitted, OPEN
        CircuitBreakerUtils.printStatusInfo(circuitBreaker);

        // then
        Assertions.assertThat(metrics.getNumberOfBufferedCalls()).isEqualTo(4);
        Assertions.assertThat(metrics.getNumberOfSuccessfulCalls()).isEqualTo(2);
        Assertions.assertThat(metrics.getNumberOfFailedCalls()).isEqualTo(2);
        Assertions.assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
        Assertions.assertThat(metrics.getNumberOfNotPermittedCalls()).isEqualTo(1); // ????????? ???????????? ??????.
    }

    @Test
    public void openStateFallback_latency_test() {
        // when: ???????????? ?????? ??????
        remoteClientBreakable.doSuccess(); // success, CLOSED
        remoteClientBreakable.doSuccess(); // success, CLOSED
        remoteClientBreakable.doLatency(); // slow success, CLOSED
        remoteClientBreakable.doLatency(); // slow success, CLOSED -> OPEN
        remoteClientBreakable.doSuccess(); // not permitted, OPEN
        CircuitBreakerUtils.printStatusInfo(circuitBreaker);

        // then
        Assertions.assertThat(metrics.getNumberOfSuccessfulCalls()).isEqualTo(4);
        Assertions.assertThat(metrics.getNumberOfSlowSuccessfulCalls()).isEqualTo(2);
        Assertions.assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    @Test
    public void decoratorPatter_test() {
        // when
        remoteClientBreakable.twiceDoSuccess();
        CircuitBreakerUtils.printStatusInfo(circuitBreaker);

        // then
        Assertions.assertThat(metrics.getNumberOfSuccessfulCalls()).isEqualTo(2);
    }
}
