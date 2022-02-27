package sunset.spring.resilience4j.springboot2.internal.circuitbreaker;

import sunset.spring.resilience4j.springboot2.internal.exception.IgnoredException;
import sunset.spring.resilience4j.springboot2.internal.exception.RecordedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MyCircuitBreakerConfig {

    public static final String CIRCUIT_BREAKER_NAME = "cb_remoteClient";
    public static final String CONFIG_BEAN_NAME = "remoteClientCircuitBreaker";

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    // 클라이언트 환경에 맞게 설정 변경
    @ConditionalOnMissingBean(name = CONFIG_BEAN_NAME)
    @Bean(name = CONFIG_BEAN_NAME)
    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.from(circuitBreakerRegistry.getDefaultConfig())
                .ignoreExceptions(IgnoredException.class) // 무시 예외가 더 우선순위가 높음.
                .recordException(ex -> false) // 모든 예외가 성공 처리
                .recordExceptions(RecordedException.class) // 이 예외는 실패 처리
                .build();
    }

    /*
    서킷브레이커를 스프링 빈으로 등록하는 게 좋은 것 같다.
    다른 컴포넌트에서 이 서킷브레이커를 가져다쓸 때 Registry 로 한다면 해당 컴포넌트에서 서킷을 가져다쓰는게 아니라 만들어버리기 때문에 오류가 날 가능성이 많다.
    물론 그것을 피하기 위해 Breakable 계층을 의존 객체를 넣으면 되지만, 코드가 지저분해진다.
     */
    @Bean(name = CIRCUIT_BREAKER_NAME)
    public CircuitBreaker circuitBreaker(@Qualifier(CONFIG_BEAN_NAME) CircuitBreakerConfig circuitBreakerConfig) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME, circuitBreakerConfig);
        circuitBreaker.getEventPublisher()
                .onSuccess(event -> {
                    String message = String.format("%s 성공 기록. %s", circuitBreaker.getState(), event);
                    if (circuitBreaker.getState() == io.github.resilience4j.circuitbreaker.CircuitBreaker.State.OPEN) {
                        // 닫혀 있을때 요청하고, 열려 있을때 성공 응답을 받게 되는 경우
                        log.warn("{}", message);
                    } else if (event.getElapsedDuration().compareTo(circuitBreaker.getCircuitBreakerConfig().getSlowCallDurationThreshold()) >= 0) {
                        // 지연 성공
                        log.info("{}", message);
                    }
                })
                .onError(event -> {
                    log.info("circuitbreaker error: {}", event);
                })
                .onIgnoredError(event -> {
                    log.info("circuitbreaker ignoredError: {}", event);
                })
                .onReset(event -> {
                    log.info("circuitbreaker reset: {}", event);
                })
                .onStateTransition(event -> {
                    log.info("circuitbreaker stateTransition: {}", event);
                });
        log.info("register circuitBreaker '{}'. config: {}", circuitBreaker.getName(), circuitBreaker.getCircuitBreakerConfig());

        return circuitBreaker;
    }
}
