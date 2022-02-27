package sunset.spring.resilience4j.springboot2.internal.client;

public interface RemoteClientSpec {

    String doSuccess();

    String doException(int code);

    String doLatency();

    String twiceDoSuccess();
}
