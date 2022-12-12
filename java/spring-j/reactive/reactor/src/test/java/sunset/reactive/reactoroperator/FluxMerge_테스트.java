package sunset.reactive.reactoroperator;

import java.util.logging.Level;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import sunset.reactive.common.Channel;

@Slf4j
public class FluxMerge_테스트 {

    @DisplayName("async_infinite Flux 2개를 merge")
    @Test
    public void asyncInfiniteFlux_merge() throws InterruptedException {
        log.info("main: start");
        Flux.merge(
                asyncInfinitePublisher("ENG", 1L, new Channel<>(), "A", "B", "C")
                    .log("ENG pub", Level.FINE), // onSubscribe, request: [Test worker], onNext: [ENG]
                asyncInfinitePublisher("KOR", 1L, new Channel<>(), "가", "나", "다")
                    .log("KOR pub", Level.FINE) // onSubscribe, request: [Test worker], onNext: [KOR]
            )
            .log("main sub") // onSubscribe, request: [Test worker], onNext: [ENG, KOR]
            .subscribe();

        Thread.sleep(2_000);
        log.info("main: end");
    }

    @DisplayName("async_infinite Flux 2개를 merge 후 publishOn")
    @Test
    public void asyncInfiniteFlux_mergeAndPublishOn() throws InterruptedException {
        log.info("main: start");
        Flux.merge(
                asyncInfinitePublisher("ENG", 1L, new Channel<>(), "A", "B", "C")
                    .log("ENG pub", Level.FINE), // onSubscribe, request: [Test worker], onNext: [ENG]
                asyncInfinitePublisher("KOR", 1L, new Channel<>(), "가", "나", "다")
                    .log("KOR pub", Level.FINE) // onSubscribe, request: [Test worker], onNext: [KOR]
            )
            .log("before publishOn", Level.FINE) // onSubscribe, request: [Test worker], onNext: [ENG, KOR]
            .publishOn(Schedulers.newSingle("SUB"))
            .log("main sub") // onSubscribe, request: [Test worker], onNext: [SUB]
            .subscribe();

        Thread.sleep(2_000);
        log.info("main: end");
    }

    @DisplayName("async_infinite Flux 2개를 merge 후 subscribeOn")
    @Test
    public void asyncInfiniteFlux_mergeAndSubscribeOn1() throws InterruptedException {
        log.info("main: start");
        Flux.merge(
                asyncInfinitePublisher("ENG", 1L, new Channel<>(), "A", "B", "C")
                    .log("ENG pub", Level.FINE), // onSubscribe, request: [SUB], onNext: [ENG]
                asyncInfinitePublisher("KOR", 1L, new Channel<>(), "가", "나", "다")
                    .log("KOR pub", Level.FINE) // onSubscribe, request: [SUB], onNext: [KOR]
            )
            .log("before subscribeOn", Level.FINE) // onSubscribe, request: [SUB], onNext: [ENG, KOR]
            .subscribeOn(Schedulers.newSingle("SUB"))
            .log("main sub") // onSubscribe, request: [Test worker], onNext: [ENG, KOR]
            .subscribe();

        Thread.sleep(2_000);
        log.info("main: end");
    }

    @DisplayName("async_infinite Flux 2개를 merge 후 subscribeOn requestOnSeparateThread false")
    @Test
    public void asyncInfiniteFlux_mergeAndSubscribeOn2() throws InterruptedException {
        log.info("main: start");
        Flux.merge(
                asyncInfinitePublisher("ENG", 1L, new Channel<>(), "A", "B", "C")
                    .log("ENG pub", Level.FINE), // onSubscribe, request: [SUB], onNext: [ENG]
                asyncInfinitePublisher("KOR", 1L, new Channel<>(), "가", "나", "다")
                    .log("KOR pub", Level.FINE) // onSubscribe, request: [SUB], onNext: [KOR]
            )
            .log("before subscribeOn", Level.FINE) // onSubscribe, request: [SUB], onNext: [ENG, KOR]
            .subscribeOn(Schedulers.newSingle("SUB"), false)
            .log("main sub") // onSubscribe, request: [Test worker], onNext: [ENG, KOR]
            .subscribe();

        Thread.sleep(2_000);
        log.info("main: end");
    }

    private static <T> Flux<T> asyncInfinitePublisher(String threadName, long sleepMillis, Channel<T> channel,
        T... dataArgs
    ) {
        new Thread(
            null,
            () -> {
                for (int i = 0; true; i = (i + 1) % dataArgs.length) {
                    try {
                        Thread.sleep(sleepMillis);
                        channel.publish(dataArgs[i]);
                    } catch (Exception ignored) {
                    }
                }
            },
            threadName
        ).start();

        return Flux.create(emitter -> {
            channel.setListener(emitter::next);
        });
    }
}
