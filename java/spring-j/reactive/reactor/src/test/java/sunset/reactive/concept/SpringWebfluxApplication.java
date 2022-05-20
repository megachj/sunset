package sunset.reactive.concept;

import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class SpringWebfluxApplication {

    @RestController
    public static class Controller {

        // Q: 구독을 하지 않고 Publisher 만 리턴하는데 어떻게 http response body 에 데이터가 입력되어 클라이언트에 응답되는가?
        // A: Publisher 만 리턴하면 스프링5 웹 엔진에서 내부적으로 구독처리 과정을 하고 값을 리턴하게 된다.
        // WebMvc 즉 서블릿(톰캣)이어도 리턴이 정상 동작된다.
        @RequestMapping("/hello")
        public Publisher<String> hello(String name) {

            return new Publisher<String>() {
                @Override
                public void subscribe(Subscriber<? super String> subscriber) {
                    subscriber.onSubscribe(new Subscription() {
                        @Override
                        public void request(long n) {
                            subscriber.onNext("hello " + name);
                            subscriber.onComplete();
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                }
            };
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringWebfluxApplication.class, args);
    }
}
