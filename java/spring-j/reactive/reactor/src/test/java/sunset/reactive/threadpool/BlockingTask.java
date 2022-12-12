package sunset.reactive.threadpool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class BlockingTask implements Runnable {

    private final int taskNumber;
    private final long blockingMs;

    @Override
    public void run() {
        log.info("Thread[id: {}] blockingTask #{} > start", Thread.currentThread().getId(), taskNumber);

        try {
            log.info("Thread[id: {}] blockingTask #{} > blocking {}ms...", Thread.currentThread().getId(),
                taskNumber, blockingMs);
            Thread.sleep(blockingMs);
        } catch (Exception ignored) {
        }

        log.info("Thread[id: {}] blockingTask #{} > end", Thread.currentThread().getId(), taskNumber);
    }
}
