package sunset.springutils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class LoadTester {

    private static final RestTemplate rt = new RestTemplate();

    private int threads;
    private AtomicInteger counter;
    private ExecutorService es;
    private String url;

    public LoadTester(int threads, String url) {
        this.threads = threads;
        this.counter = new AtomicInteger(0);
        this.es = Executors.newFixedThreadPool(threads);
        this.url = url;
    }

    public void loadForTest() throws BrokenBarrierException, InterruptedException {
        // 스레드를 동시에 실행시키기 위해서 사용
        CyclicBarrier barrier = new CyclicBarrier(threads + 1);

        for (int i = 0; i < 100; i++) {
            es.submit(() -> {
                int idx = counter.addAndGet(1);
                log.info("Thread {}", idx);

                barrier.await();

                StopWatch sw = new StopWatch();
                sw.start();

                String res = rt.getForObject(url, String.class, idx);

                sw.stop();
                log.info("Elapsed {} {}: {}", idx, sw.getTotalTimeSeconds(), res);

                return null;
            });
        }

        barrier.await();

        StopWatch main = new StopWatch();
        main.start();

        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS);

        main.stop();
        log.info("Total: {}", main.getTotalTimeSeconds());
    }
}
