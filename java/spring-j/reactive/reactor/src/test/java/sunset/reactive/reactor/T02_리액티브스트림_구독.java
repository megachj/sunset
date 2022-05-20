package sunset.reactive.reactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
public class T02_리액티브스트림_구독 {

    @Test
    public void 구독하기_데이터무제한() {
        Flux.just("A", "B", "C")
            .subscribe(
                data -> log.info("onNext: {}", data),
                err -> {
                },
                () -> log.info("onComplete")
            );
    }

    @Test
    public void 구독하기_데이터제한() {
        // 리액티브 스트림은 프로듀서가(onError 또는 onComplete 신호를 사용해) 종료하거나 구독자가 Subscription 인스턴스를 통해 취소할 수 있다.
        Flux.range(1, 100)
            .subscribe(
                data -> log.info("onNext: {}", data),
                err -> {
                },
                () -> log.info("onComplete"),
                subscription -> {
                    subscription.request(4); // 데이터 4개 요청
                    subscription.cancel(); // 구독자가 구독 취소
                }
            );
    }

    @Test
    public void Disposable() throws Exception {
        Disposable disposable = Flux.interval(Duration.ofMillis(50))
            .subscribe(data -> log.info("onNext: {}", data));
        Thread.sleep(200);
        disposable.dispose();
    }

    @Test
    public void 사용자정의구독자_구현() {
        // 실제로 아래처럼 사용하는 것은 좋지않다. 왜냐하면 스스로 배압을 관리하고 구독자에 대한 모든 TCK 요구사항을 올바르게 구현하기란 어렵기 때문이다.
        Subscriber<String> subscriber = new Subscriber<>() {
            volatile Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                subscription = s;
                log.info("initial request for 1 element");
                subscription.request(1);
            }

            @Override
            public void onNext(String s) {
                log.info("onNext: {}", s);
                log.info("requesting 1 more element");

                subscription.request(1);
            }

            @Override
            public void onError(Throwable t) {
                log.warn("onError: {}", t.getMessage());
            }

            @Override
            public void onComplete() {
                log.info("onComplete");
            }
        };

        Flux<String> stream = Flux.just("Hello", "world", "!");
        stream.subscribe(subscriber);
    }

    @Test
    public void BaseSubscriber_상속해서_구현() {
        BaseSubscriber<String> subscriber = new BaseSubscriber<String>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                log.info("initial request for 1 element");
                request(1);
            }

            @Override
            protected void hookOnNext(String value) {
                log.info("onNext: {}", value);
                log.info("requesting 1 more element");

                request(1);
            }
        };

        Flux<String> stream = Flux.just("Hello", "world", "!");
        stream.subscribe(subscriber);
    }
}
