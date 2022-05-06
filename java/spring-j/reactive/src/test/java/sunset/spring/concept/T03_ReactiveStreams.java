package sunset.spring.concept;

import java.util.Iterator;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

@Slf4j
public class T03_ReactiveStreams {

    @Test
    public void ReactiveStreams_직접구현() throws Exception {
        Iterable<Integer> dataSource = Stream.iterate(1, i -> i + 1)
            .limit(10)
            .collect(Collectors.toList());

        SimplePublisher<Integer> simplePublisher = new SimplePublisher<>(dataSource);

        SimpleSubscriber<Integer> simpleSubscriber = new SimpleSubscriber<>();
        simplePublisher.subscribe(simpleSubscriber);
    }

    @Test
    public void Operator_직접구현() throws Exception {
        Iterable<Integer> dataSource = Stream.iterate(1, i -> i + 1)
            .limit(10)
            .collect(Collectors.toList());

        Publisher<Integer> pub = SimplePublisher.<Integer>from(dataSource);
        Publisher<String> mappingPub = SimplePublisher.map(pub, s -> "[" + s + "]");
        Publisher<String> reducingPub = SimplePublisher.reduce(mappingPub, "", (a, b) -> a + b);
        reducingPub.subscribe(new SimpleSubscriber<>());
    }

    @Test
    public void 리액터_프로젝트로_Operator_실행() {
        Flux.<Integer>create(e -> {
                e.next(1);
                e.next(2);
                e.next(3);
                e.complete();
            })
            .log("Step1")
            .map(s -> s*10)
            .reduce(0, (a, b) -> a + b)
            .log("Step2")
            .subscribe(s -> log.info("{}", s));
    }
}

@Slf4j
@Getter
class SimplePublisher<T> implements Publisher<T> {

    private final Iterable<T> dataSource;

    public SimplePublisher(Iterable<T> dataSource) {
        this.dataSource = dataSource;
    }

    public static <T> SimplePublisher<T> from(Iterable<T> dataSource) {
        return new SimplePublisher<>(dataSource);
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        Iterator<T> dataIterator = dataSource.iterator();

        subscriber.onSubscribe(new Subscription() {
            @Override
            public void request(long n) {
                try {
                    dataSource.forEach(data -> subscriber.onNext(data));
                    subscriber.onComplete();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }

            @Override
            public void cancel() {

            }
        });
    }

    // operator 메소드: map
    public static <T, R> Publisher<R> map(Publisher<T> upstreamPublisher, Function<T, R> f) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> downstreamSubscriber) {
                upstreamPublisher.subscribe(new DelegateSubscriber<T, R>(downstreamSubscriber) {
                    @Override
                    public void onNext(T item) {
                        downstreamSubscriber.onNext(f.apply(item));
                    }
                });
            }
        };
    }

    // operator 메소드: reduce
    public static <T, R> Publisher<R> reduce(Publisher<T> upstreamPublisher, R init, BiFunction<R, T, R> bf) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> downstreamSubscriber) {
                upstreamPublisher.subscribe(new DelegateSubscriber<T, R>(downstreamSubscriber) {
                    R result = init;

                    @Override
                    public void onNext(T item) {
                        result = bf.apply(result, item);
                    }

                    @Override
                    public void onComplete() {
                        downstreamSubscriber.onNext(result);
                        downstreamSubscriber.onComplete();
                    }
                });
            }
        };
    }
}

@Slf4j
@Getter
@NoArgsConstructor
class SimpleSubscriber<T> implements Subscriber<T> {

    private Subscription subscription;

    @Override
    public void onSubscribe(Subscription subscription) {
        log.info("onSubscribe");

        this.subscription = subscription;
        this.subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(T item) {
        log.info("onNext: {}", item);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("onError: {}", throwable.getMessage());
    }

    @Override
    public void onComplete() {
        log.info("onComplete");
    }
}

abstract class DelegateSubscriber<T, R> implements Subscriber<T> {

    Subscriber<? super R> downStreamSubscriber;

    public DelegateSubscriber(Subscriber<? super R> downStreamSubscriber) {
        this.downStreamSubscriber = downStreamSubscriber;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        downStreamSubscriber.onSubscribe(subscription);
    }

    @Override
    public void onError(Throwable throwable) {
        downStreamSubscriber.onError(throwable);
    }

    @Override
    public void onComplete() {
        downStreamSubscriber.onComplete();
    }
}
