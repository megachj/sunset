package sunset.spring.async.t03;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class NotCallableLoadTest {
    static AtomicInteger counter = new AtomicInteger(0);

    /**
     * 서버 톰캣 스레드를 20개, 200개일 때 경과 시간, 서버의 스레드 수를 비교해보자.
     *  - 20 개일 때: 총 10초 + a, 20 개당 2초씩 걸림
     *  - 200 개일 때: 총 2초, 100 개를 한번에 처리
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(100);

        RestTemplate rt = new RestTemplate();
        String url = "http://localhost:8080/notcallable";

        StopWatch main = new StopWatch();
        main.start();

        for (int i = 0; i < 100; i++) {
            es.execute(() -> {
                int idx = counter.addAndGet(1);
                log.info("Thread {}", idx);

                StopWatch sw = new StopWatch();
                sw.start();
                rt.getForObject(url, String.class);
                sw.stop();
                log.info("Elapsed {}: {}", idx, sw.getTotalTimeSeconds());
            });
        }

        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS);

        main.stop();
        log.info("Total: {}", main.getTotalTimeSeconds());
    }
}
