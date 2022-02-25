package sunset.spring.resilience4j.springboot2.internal.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CircuitBreakerUtils {

    public static void printStatusInfo(CircuitBreaker circuitBreaker) {
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        log.info("circuitbreaker state {}\n\t{ bufferedCalls: {}, successfulCalls: {}, failedCalls: {}, notPermittedCalls: {}, failureRate: {}, \n\t  slowCalls: {}, slowSuccessfulCalss: {}, slowFailedCalls: {}, slowRate: {} }",
                circuitBreaker.getState(),
                metrics.getNumberOfBufferedCalls(),
                metrics.getNumberOfSuccessfulCalls(),
                metrics.getNumberOfFailedCalls(),
                metrics.getNumberOfNotPermittedCalls(),
                metrics.getFailureRate(),
                metrics.getNumberOfSlowCalls(),
                metrics.getNumberOfSlowSuccessfulCalls(),
                metrics.getNumberOfFailedCalls(),
                metrics.getSlowCallRate()
        );
    }
}
