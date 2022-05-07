package sunset.spring.concept;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

@Slf4j
public class T05_intervalEx {

    @Test
    public void test() throws Exception {
        // 내부적으로 사용되는 스레드들이 모두 데몬 스레드이다.
        Flux.interval(Duration.ofMillis(100))
            .take(10)
            .subscribe(s -> log.debug("onNext: {}", s));

        log.info("exit");

        // main 메소드로 작성해도 데몬스레드여서 슬립이 필요하다.
        // test 메소드는 유저 스레드가 남아있어도 종료되어버린다.
        TimeUnit.SECONDS.sleep(3);
    }

    public static void main(String[] args) {
        Publisher<Integer> pub = sub -> {
            sub.onSubscribe(new Subscription() {
                int no = 0;
                boolean cancelled = false;

                @Override
                public void request(long l) {
                    ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
                    exec.scheduleAtFixedRate(() -> {
                        if (cancelled) {
                            exec.shutdown();
                            return;
                        }
                        sub.onNext(no++);
                    }, 0, 10, TimeUnit.MILLISECONDS);
                }

                @Override
                public void cancel() {
                    log.debug("cancel");
                    cancelled = true;
                }
            });
        };

        Publisher<Integer> takePub = sub -> {
            pub.subscribe(new Subscriber<Integer>() {
                int count = 0;
                final int capacity = 5;
                Subscription subscription;

                @Override
                public void onSubscribe(Subscription subscription) {
                    this.subscription = subscription;
                    sub.onSubscribe(subscription);
                }

                @Override
                public void onNext(Integer integer) {
                    if (count++ > capacity - 1) {
                        subscription.cancel();
                        return;
                    }
                    sub.onNext(integer);
                }

                @Override
                public void onError(Throwable throwable) {
                    sub.onError(throwable);
                }

                @Override
                public void onComplete() {
                    sub.onComplete();
                }
            });
        };

        takePub.subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                log.debug("onSubscribe");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                log.debug("onNext: {}", integer);
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("onError", throwable);
            }

            @Override
            public void onComplete() {
                log.debug("onComplete");
            }
        });
    }
}
