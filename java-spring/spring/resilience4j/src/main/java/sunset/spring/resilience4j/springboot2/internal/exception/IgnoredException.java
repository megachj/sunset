package sunset.spring.resilience4j.springboot2.internal.exception;

public class IgnoredException extends RuntimeException {
    public IgnoredException() {
        super();
    }
    public IgnoredException(String message, Throwable cause) {
        super(message, cause);
    }
}
