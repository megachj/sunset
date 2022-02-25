package sunset.spring.resilience4j.springboot2.internal.circuitbreaker;

import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.micrometer.tagged.TaggedBulkheadMetrics;
import io.github.resilience4j.micrometer.tagged.TaggedCircuitBreakerMetrics;
import io.github.resilience4j.micrometer.tagged.TaggedRateLimiterMetrics;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
public class CircuitBreakerRegistryConfig {

    /*
    CircuitBreakerRegistry 타입은 스프링 빈이 오직 1개여야 한다. 그렇지 않으면 애플리케이션 기동 시에 아래 에러가 발생한다.
      - Parameter 0 of method circuitBreakerAspect in io.github.resilience4j.circuitbreaker.autoconfigure.AbstractCircuitBreakerConfigurationOnMissingBean required a single bean, but n were found:
     */
    @Bean("circuitBreakerRegistry")
    public CircuitBreakerRegistry registry(MeterRegistry meterRegistry) {
        CircuitBreakerConfig defaultCircuitBreakerConfig = CircuitBreakerConfig.custom()
                .slidingWindow(100, 20, CircuitBreakerConfig.SlidingWindowType.COUNT_BASED) // 추천 값: 사이즈 100, COUNT_BASED
                .failureRateThreshold(50) // 실패율 threshold, 추천 값: 50 이하
                .slowCallRateThreshold(50) // 지연율 threshold
                .slowCallDurationThreshold(Duration.ofSeconds(1)) // 지연시간 기준
                .permittedNumberOfCallsInHalfOpenState(10) // HALF_OPEN 일 때 허용 콜 수
                .waitDurationInOpenState(Duration.ofSeconds(30)) // OPEN 에서 HALF_OPEN 으로 바뀌는 대기 시간, 추천 값: 수초 이내
                // waitDurationInOpenState 기간 이후에 ScheduledExecutorSerivce를 이용해 half-open으로 자동으로 전환해줄지 결정하는 값.
                // 내부적으로 CircuitBreakerStateMachine 의 scheduledExecutorService 에 스케줄링할 스레드를 하나 등록하게 된다.
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .build();

        CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.of(defaultCircuitBreakerConfig);
        TaggedCircuitBreakerMetrics.ofCircuitBreakerRegistry(circuitBreakerRegistry) // metrics
                .bindTo(meterRegistry);

        return circuitBreakerRegistry;
    }

    /*
    선택 사항
     */
    @Bean
    public RateLimiterRegistry rateLimiterRegistry(MeterRegistry meterRegistry) {
        RateLimiterRegistry rateLimiterRegistry = RateLimiterRegistry.ofDefaults();
        TaggedRateLimiterMetrics.ofRateLimiterRegistry(rateLimiterRegistry)
                .bindTo(meterRegistry);

        return rateLimiterRegistry;
    }

    /*
    선택 사항
     */
    @Bean
    public BulkheadRegistry bulkheadRegistry(MeterRegistry meterRegistry) {
        BulkheadRegistry bulkheadRegistry = BulkheadRegistry.ofDefaults();
        TaggedBulkheadMetrics.ofBulkheadRegistry(bulkheadRegistry)
                .bindTo(meterRegistry);

        return bulkheadRegistry;
    }
}
