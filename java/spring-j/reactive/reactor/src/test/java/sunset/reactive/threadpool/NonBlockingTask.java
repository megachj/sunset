package sunset.reactive.threadpool;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class NonBlockingTask implements Runnable {

    private final int taskNumber;
    private final long nonBlockingDelayMs;

    @Override
    public void run() {
        log.info("Thread[id: {}] nonBlockingTask #{} > start", Thread.currentThread().getId(), taskNumber);

        try {
            log.info("Thread[id: {}] nonBlockingTask #{} > do nonBlocking job...", Thread.currentThread().getId(), taskNumber);

            Mono.just(taskNumber)
                .delayElement(Duration.ofMillis(nonBlockingDelayMs))
                .subscribe(
                    next -> {
                        log.info("Thread[id: {}] nonBlockingTask #{} >>> onNext {}", Thread.currentThread().getId(), taskNumber, next);
                    },
                    throwable -> {
                        log.error("Thread[id: {}] nonBlockingTask #{} >>> onError", Thread.currentThread().getId(), taskNumber, throwable);
                    },
                    () -> {
                        log.info("Thread[id: {}] nonBlockingTask #{} >>> onComplete", Thread.currentThread().getId(), taskNumber);
                    }
                );
        } catch (Exception ignored) {
        }

        log.info("Thread[id: {}] nonBlockingTask #{} > end", Thread.currentThread().getId(), taskNumber);
    }
}
