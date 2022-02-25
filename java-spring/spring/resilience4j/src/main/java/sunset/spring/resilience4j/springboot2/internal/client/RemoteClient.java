package sunset.spring.resilience4j.springboot2.internal.client;

import sunset.spring.resilience4j.springboot2.internal.exception.IgnoredException;
import sunset.spring.resilience4j.springboot2.internal.exception.RecordedException;
import sunset.spring.resilience4j.springboot2.internal.library.RemoteCallLibrary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@Slf4j
@RequiredArgsConstructor
@Component
public class RemoteClient implements RemoteClientSpec{

    private final RemoteCallLibrary remoteCallLibrary;

    @Override
    public String doSuccess() {
        return remoteCallLibrary.doSuccess();
    }

    @Override
    public String doException(int code) {
        try {
            return remoteCallLibrary.doException(code);
        } catch (HttpClientErrorException ex) {
            throw new IgnoredException("클라 에러는 무시", ex);
        } catch (HttpServerErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.BAD_GATEWAY)
                throw new IllegalStateException("배드게이트웨이는 고려되지 않음.");
            else
                throw new RecordedException("서버 에러는 기록", ex);
        }
    }

    @Override
    public String doLatency() {
        return remoteCallLibrary.doLatency();
    }

    @Override
    public String twiceDoSuccess() {
        doSuccess();
        doSuccess();

        return "twiceSuccess";
    }
}
