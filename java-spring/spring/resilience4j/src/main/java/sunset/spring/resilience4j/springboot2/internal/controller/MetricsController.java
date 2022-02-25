package sunset.spring.resilience4j.springboot2.internal.controller;

import sunset.spring.resilience4j.springboot2.internal.circuitbreaker.MyCircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/metrics")
@RequiredArgsConstructor
public class MetricsController {

    @Qualifier(MyCircuitBreakerConfig.CIRCUIT_BREAKER_NAME)
    private final CircuitBreaker circuitBreaker;

    @GetMapping("/circuitbreaker")
    public ResponseEntity<CircuitBreaker.Metrics> getCircuitBreakerMetrics() {
        return new ResponseEntity<>(circuitBreaker.getMetrics(), HttpStatus.OK);
    }
}
