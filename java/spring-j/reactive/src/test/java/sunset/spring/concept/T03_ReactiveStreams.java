package sunset.spring.concept;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class T03_ReactiveStreams {

    @Test
    public void ReactiveStreams_직접구현() throws Exception {
        Iterable<Integer> dataSource = Stream.iterate(1, i -> i + 1)
            .limit(100)
            .collect(Collectors.toList());

        SimplePublisher<Integer> simplePublisher = new SimplePublisher<>(dataSource);

        SimpleSubscriber<Integer> simpleSubscriber = new SimpleSubscriber<>();
        simplePublisher.subscribe(simpleSubscriber);

        Thread.sleep(3 * 1000);
        log.info("main end...");
    }

    @Getter
    public static class SimplePublisher<T> implements Publisher<T> {

        private final Iterable<T> dataSource;
        private final ExecutorService threadPool;
        private boolean terminated;

        public SimplePublisher(Iterable<T> dataSource) {
            this.dataSource = dataSource;
            this.threadPool = Executors.newFixedThreadPool(1); // TODO: 스레드 여러개면 순서가 섞이게 됨
            this.terminated = false;
        }

        @Override
        public void subscribe(Subscriber<? super T> subscriber) {
            Iterator<T> dataIterator = dataSource.iterator();

            subscriber.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    // log.debug("request: {}", n);
                    threadPool.execute(() -> {
                        try {
                            for (int i = 0; i < n; i++) {
                                if (dataIterator.hasNext()) {
                                    subscriber.onNext(dataIterator.next());
                                } else {
                                    if (!terminated) {
                                        terminated = true;
                                        subscriber.onComplete();
                                    }
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            if (!terminated) {
                                terminated = true;
                                subscriber.onError(e);
                            }
                        }
                    });
                }

                @Override
                public void cancel() {
                    log.info("cancel");
                }
            });
        }
    }

    @Getter
    @NoArgsConstructor
    public static class SimpleSubscriber<T> implements Subscriber<T> {

        private static final int BUFFER_TOTAL_CAPACITY = 10;

        private Subscription subscription;
        private Queue<T> buffer = new ArrayDeque<>(10);
        private int unitRequest = 3;

        @Override
        public void onSubscribe(Subscription subscription) {
            log.info("onSubscribe");

            this.subscription = subscription;
            this.subscription.request(unitRequest);
        }

        @Override
        public void onNext(T item) {
            log.info("onNext: {}", item);

            if (isBufferSomeFree()) {
                buffer.add(item);
            } else {
                while (!buffer.isEmpty()) {
                    T buffered = buffer.poll();
                    // buffered data process...
                }
            }

            this.subscription.request(unitRequest);
        }

        @Override
        public void onError(Throwable throwable) {
            log.error("onError: {}", throwable.getMessage());
        }

        @Override
        public void onComplete() {
            log.info("onComplete");
            //subscription.cancel();
        }

        private boolean isBufferSomeFree() {
            return buffer.size() <= BUFFER_TOTAL_CAPACITY / 2;
        }
    }
}
