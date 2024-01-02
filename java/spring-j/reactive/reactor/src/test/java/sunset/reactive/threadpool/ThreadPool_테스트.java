package sunset.reactive.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
public class ThreadPool_테스트 {

    @DisplayName("FixedThreadPool + submit + BlockingTask 테스트: 작업이 완료되어야 다음 작업을 진행한다.")
    @Test
    public void test1() throws Exception {
        log.info("main: start");
        ExecutorService fixedExecutorService = Executors.newFixedThreadPool(2);

        for (int i = 0; i < 5; ++i) {
            BlockingTask iTask = new BlockingTask(i, 1_000L);
            fixedExecutorService.submit(iTask);
        }

        Thread.sleep(3_000L);
        log.info("main: end");
    }

    @DisplayName("FixedThreadPool + submit + NonBlockingTask 테스트: 논블로킹 작업 제외하고 완료되어야 다음 작업을 진행한다.")
    @Test
    public void test2() throws Exception {
        log.info("main: start");
        ExecutorService fixedExecutorService = Executors.newFixedThreadPool(2);

        for (int i = 0; i < 5; ++i) {
            NonBlockingTask iTask = new NonBlockingTask(i, 1_000L);
            fixedExecutorService.submit(iTask);
        }

        Thread.sleep(3_000L);
        log.info("main: end");
    }

    @DisplayName("ScheduledThreadPool + schedule + BlockingTask 테스트")
    @Test
    public void test3() throws Exception {
        log.info("main: start");
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);

        for (int i = 0; i < 5; ++i) {
            BlockingTask iTask = new BlockingTask(i, 1_000L);
            long delayMs = i % 2 == 0 ? 0L : 1_000L;
            log.info("main >>> schedule after {}ms...", delayMs);
            scheduledExecutorService.schedule(iTask, delayMs, TimeUnit.MILLISECONDS);
        }

        Thread.sleep(10_000L);
        log.info("main: end");
    }

    @DisplayName("ScheduledThreadPool + schedule + NonBlockingTask 테스트")
    @Test
    public void test4() throws Exception {
        log.info("main: start");
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);

        for (int i = 0; i < 5; ++i) {
            NonBlockingTask iTask = new NonBlockingTask(i, 1_000L);
            long delayMs = i % 2 == 0 ? 0L : 1_000L;
            log.info("main >>> schedule after {}ms...", delayMs);
            scheduledExecutorService.schedule(iTask, delayMs, TimeUnit.MILLISECONDS);
        }

        Thread.sleep(10_000L);
        log.info("main: end");
    }

    @DisplayName("ScheduledThreadPool + schedule + NonBlockingTask 테스트")
    @Test
    public void test5() throws Exception {
        log.info("main: start");
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        NonBlockingTask task1 = new NonBlockingTask(1, 1_000L);
        scheduledExecutorService.schedule(task1, 2_000L, TimeUnit.MILLISECONDS);

        NonBlockingTask task2 = new NonBlockingTask(2, 1_000L);
        scheduledExecutorService.schedule(task2, 0L, TimeUnit.MILLISECONDS);

        Thread.sleep(10_000L);
        log.info("main: end");
    }
}
