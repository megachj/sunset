package sunset.spring.resilience4j.springboot2.internal.client;

import sunset.spring.resilience4j.springboot2.internal.circuitbreaker.MyCircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.decorators.Decorators;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Primary @Component
public class RemoteClientBreakable implements RemoteClientSpec {

    private final RemoteClient remoteClient;
    private final io.github.resilience4j.circuitbreaker.CircuitBreaker circuitBreaker;

    @CircuitBreaker(name = MyCircuitBreakerConfig.CIRCUIT_BREAKER_NAME, fallbackMethod = "doSuccess")
    @Override
    public String doSuccess() {
        return remoteClient.doSuccess();
    }
    private String doSuccess(Throwable ex) {
        String eMessage = "It's doSuccess fallback method.";
        log.warn(eMessage);
        return eMessage;
    }

    @CircuitBreaker(name = MyCircuitBreakerConfig.CIRCUIT_BREAKER_NAME, fallbackMethod = "doException")
    @Override
    public String doException(int code) {
        return remoteClient.doException(code);
    }
    private String doException(int code, CallNotPermittedException ex) {
        String eMessage = "It's doException fallback method.";
        log.warn(eMessage);
        return eMessage;
    }

    @CircuitBreaker(name = MyCircuitBreakerConfig.CIRCUIT_BREAKER_NAME, fallbackMethod = "doLatency")
    @Override
    public String doLatency() {
        return remoteClient.doLatency();
    }
    private String doLatency(CallNotPermittedException ex) {
        String eMessage = "It's doLatency fallback method.";
        log.warn(eMessage);
        return eMessage;
    }

    @Override
    public String twiceDoSuccess() {
        Decorators.ofSupplier(this::doSuccess)
                .withCircuitBreaker(circuitBreaker)
                .withFallback(this::doSuccess)
                .get();
        Decorators.ofSupplier(this::doSuccess)
                .withCircuitBreaker(circuitBreaker)
                .withFallback(this::doSuccess)
                .get();

        return "twiceSuccess";
    }
}
