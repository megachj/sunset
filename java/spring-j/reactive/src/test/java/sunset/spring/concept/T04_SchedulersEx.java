package sunset.spring.concept;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class T04_SchedulersEx {

    /**
     * https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#publishOn-reactor.core.scheduler.Scheduler-
     * - publishOn 은 onNext 부터 스케줄러에서 실행한다. 즉 onNext, onComplete, onError 을 스케줄러에서 실행하게 된다. - 일반적으로 빠른 퍼블리셔, 느린
     * 컨슈머 시나리오에서 사용된다.
     */
    @Test
    public void publishOn_테스트() {
        Flux.range(1, 3)
            .log("A")
            .publishOn(Schedulers.newSingle("pub"))
            .log("B")
            .subscribe(
                next -> {
                    log.info("onNext: {}", next);
                },
                throwable -> {
                    log.info("onError", throwable);
                },
                () -> {
                    log.info("onComplete");
                });

        log.info("Main end.");
    }

    /**
     * https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#subscribeOn-reactor.core.scheduler.Scheduler-
     * - subscribeOn 은 onSubscribe 부터 스케줄러에서 실행한다. 즉 onSubscribe, onNext, onComplete, onError 모두 스케줄러에서 실행하게 된다. -
     * 일반적으로 느린 퍼블리셔, 빠른 컨슈머 시나리오에서 사용된다. 예) blocking IO
     */
    @Test
    public void subscribeOn_테스트() {
        Flux.range(1, 3)
            .log("A")
            .subscribeOn(Schedulers.newSingle("sub"))
            .log("B")
            .subscribe(
                next -> {
                    log.info("onNext: {}", next);
                },
                throwable -> {
                    log.info("onError", throwable);
                },
                () -> {
                    log.info("onComplete");
                });

        log.info("Main end.");
    }

    @Test
    public void publishOn_subscribeOn_테스트() {
        Flux.range(1, 3)
            .log("A")
            .publishOn(Schedulers.newSingle("pub"))
            .log("B")
            .subscribeOn(Schedulers.newSingle("sub"))
            .log("C")
            .subscribe(
                next -> {
                    log.info("onNext: {}", next);
                },
                throwable -> {
                    log.info("onError", throwable);
                },
                () -> {
                    log.info("onComplete");
                });

        log.info("Main end.");
    }

    @Test
    public void subscribeOn_publishOn_테스트() {
        Flux.range(1, 3)
            .log("A")
            .subscribeOn(Schedulers.newSingle("sub"))
            .log("B")
            .publishOn(Schedulers.newSingle("pub"))
            .log("C")
            .subscribe(
                next -> {
                    log.info("onNext: {}", next);
                },
                throwable -> {
                    log.info("onError", throwable);
                },
                () -> {
                    log.info("onComplete");
                });

        log.info("Main end.");
    }

    /**
     * pub - subscribeOn - publishOn - subscriber 직접 만들어보기
     */
    public static void main(String[] args) throws Exception {
        Publisher<Integer> pub = sub -> {
            sub.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    log.info("request start");
                    sub.onNext(1);
                    sub.onNext(2);
                    sub.onNext(3);
                    sub.onComplete();
                    log.info("request end");
                }

                @Override
                public void cancel() {

                }
            });
        };

        Publisher<Integer> subscribeOnPub = sub -> {
            ExecutorService es = Executors.newSingleThreadExecutor(new CustomizableThreadFactory() {
                @Override
                public String getThreadNamePrefix() {
                    return "subOn-";
                }
            });

            es.execute(() -> pub.subscribe(new Subscriber<Integer>() {
                @Override
                public void onSubscribe(Subscription subscription) {
                    log.info("A onSubscribe");
                    sub.onSubscribe(subscription);
                }

                @Override
                public void onNext(Integer integer) {
                    log.info("A onNext: {}", integer);
                    sub.onNext(integer);
                }

                @Override
                public void onError(Throwable throwable) {
                    log.error("A onError", throwable);
                    sub.onError(throwable);
                    es.shutdown();
                }

                @Override
                public void onComplete() {
                    log.info("A onComplete");
                    sub.onComplete();
                    es.shutdown();
                }
            }));
        };

        Publisher<Integer> publishOnPub = sub -> {
            subscribeOnPub.subscribe(new Subscriber<Integer>() {
                ExecutorService es = Executors.newSingleThreadExecutor(new CustomizableThreadFactory() {
                    @Override
                    public String getThreadNamePrefix() {
                        return "pubOn-";
                    }
                });

                @Override
                public void onSubscribe(Subscription subscription) {
                    log.info("B onSubscribe");
                    sub.onSubscribe(subscription);
                }

                @Override
                public void onNext(Integer integer) {
                    es.execute(() -> {
                        log.info("B onNext: {}", integer);
                        sub.onNext(integer);
                    });
                }

                @Override
                public void onError(Throwable throwable) {
                    es.execute(() -> {
                        log.error("B onError", throwable);
                        sub.onError(throwable);
                    });
                    es.shutdown();
                }

                @Override
                public void onComplete() {
                    es.execute(() -> {
                        log.info("B onComplete");
                        sub.onComplete();
                    });
                    es.shutdown();
                }
            });
        };

        publishOnPub.subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                log.info("C onSubscribe");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                log.info("C onNext: {}", integer);
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("C onError", throwable);
            }

            @Override
            public void onComplete() {
                log.info("C onComplete");
            }
        });

        log.info("exit");
    }
}
