package sunset.spring.resilence4j.springboot2.client;

import sunset.spring.resilence4j.springboot2.Resilience4jTestApplication;
import sunset.spring.resilience4j.springboot2.internal.circuitbreaker.CircuitBreakerUtils;
import sunset.spring.resilience4j.springboot2.internal.circuitbreaker.MyCircuitBreakerConfig;
import sunset.spring.resilience4j.springboot2.internal.client.RemoteClient;
import sunset.spring.resilience4j.springboot2.internal.client.RemoteClientBreakable;
import sunset.spring.resilience4j.springboot2.internal.exception.IgnoredException;
import sunset.spring.resilience4j.springboot2.internal.exception.RecordedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpServerErrorException;

import java.time.Duration;

import static org.mockito.Mockito.when;

@Slf4j
@RunWith(SpringRunner.class)
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
                            .slowCallDurationThreshold(Duration.ofMillis(50)) // 지연시간 기준
                            .waitDurationInOpenState(Duration.ofSeconds(60)) // OPEN 에서 HALF_OPEN 으로 바뀌는 대기 시간
                            .ignoreExceptions(IgnoredException.class) // ignoreExceptions 가 가장 우선순위가 높다.
                            .recordException(ex -> false) // 이 옵션이 있어야 recordExceptions 만 record(fail) 처리된다. 나머지는 모두 success 처리.
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

    @Before
    public void initStubbing() {
        circuitBreaker = circuitBreakerRegistry.circuitBreaker(MyCircuitBreakerConfig.CIRCUIT_BREAKER_NAME);
        circuitBreaker.reset(); // 여러 테스트 시에 서킷 브레이커 상태, 데이터 초기화
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
        // when: 실패율로 상태 변화
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
        Assertions.assertThat(metrics.getNumberOfNotPermittedCalls()).isEqualTo(1); // 요청이 허용되지 않음.
    }

    @Test
    public void openStateFallback_latency_test() {
        // when: 지연율로 상태 변화
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
