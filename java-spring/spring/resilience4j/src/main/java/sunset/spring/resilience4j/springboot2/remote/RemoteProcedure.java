package sunset.spring.resilience4j.springboot2.remote;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@RestController
@RequestMapping("/remote")
public class RemoteProcedure {

    @GetMapping("/success")
    public String getSuccess() {
        return "SUCCESS";
    }

    @GetMapping("/exception/{code}")
    public void getException(@PathVariable("code") int code) {
        HttpStatus httpStatus = HttpStatus.resolve(code);
        if (httpStatus == null)
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);

        if (httpStatus.is4xxClientError()) {
            throw new HttpClientErrorException(httpStatus);
        } else if (httpStatus.is5xxServerError()) {
            throw new HttpServerErrorException(httpStatus);
        }
    }

    @GetMapping("/latency")
    public String getLatencySuccess() {
        try {
            Thread.sleep(3000);
        } catch (Exception ignored) {}

        return "LATENCY OK";
    }

    @GetMapping("/chaos-monkey")
    public String getChaosMonkey() {
        return "OK";
    }
}
