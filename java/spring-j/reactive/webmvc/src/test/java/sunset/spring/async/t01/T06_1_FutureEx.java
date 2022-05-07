package sunset.spring.async.t01;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * 비동기 작업의 결과를 가져오는 방법 1. Future 2. Callback
 */
@Slf4j
public class T06_1_FutureEx {

    interface SuccessCallback {

        void onSuccess(String result);
    }

    interface ExceptionCallback {

        void onError(Throwable t);
    }

    public static class CallbackFutureTask extends FutureTask<String> {

        SuccessCallback sc;
        ExceptionCallback ec;

        public CallbackFutureTask(Callable<String> callable, SuccessCallback sc, ExceptionCallback ec) {
            super(callable);
            this.sc = Objects.requireNonNull(sc);
            this.ec = Objects.requireNonNull(ec);
        }

        @Override
        protected void done() {
            try {
                sc.onSuccess(get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                ec.onError(e.getCause());
            }
        }
    }

    @Test
    public void future() throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newCachedThreadPool();

        Future<String> f = es.submit(() -> {
            Thread.sleep(2000);
            log.debug("Async");
            return "Hello";
        });

        log.info("{}", f.isDone());
        Thread.sleep(2100);
        log.info("Exit");
        log.info("{}", f.isDone());

        log.info("{}", f.get()); // Thread Blocking
        es.shutdown();
    }

    @Test
    public void futureTask_callback으로_구현해사용하기() throws InterruptedException {
        ExecutorService es = Executors.newCachedThreadPool();

        CallbackFutureTask f = new CallbackFutureTask(() -> {
            Thread.sleep(2000);
            // if (1==1) throw new RuntimeException("Async Error!!!");

            log.debug("Async");
            return "Hello";
        }
            , res -> log.debug("result: {}", res)
            , e -> log.error("error: {}", e.getMessage())
        );

        es.execute(f);
        es.shutdown();

        TimeUnit.SECONDS.sleep(3);
    }
}
