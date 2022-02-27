package sunset.spring.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@RequiredArgsConstructor
@Service
public class AsyncService {

    private final Executor asyncTaskExecutor;

    @Async
    public void taskSpringAsync1() {
        log.info("taskSpringAsync1, start.");
        try { Thread.sleep(3000); } catch (Exception ignored) {}
        log.info("taskSpringAsync1, end.");
    }

    /**
     * org.springframework.beans.factory.NoSuchBeanDefinitionException 발생. 'invalid' 이름의 executor 빈을 찾을 수 없음.
     */
    @Async("invalid")
    public void taskSpringAsync2() {
        log.info("taskSpringAsync2, start.");
        try { Thread.sleep(3000); } catch (Exception ignored) {}
        log.info("taskSpringAsync2, end.");
    }

    @Async("asyncTaskExecutor")
    public void taskSpringAsync3() {
        log.info("taskSpringAsync3, start.");
        try { Thread.sleep(3000); } catch (Exception ignored) {}
        log.info("taskSpringAsync3, end.");
    }

    public void taskCompletableFuture1() {
        CompletableFuture.runAsync(() -> {
            log.info("taskCompletableFuture1, start.");
            try { Thread.sleep(3000); } catch (Exception ignored) {}
            log.info("taskCompletableFuture1, end.");
        });
    }

    public void taskCompletableFuture2() {
        CompletableFuture.runAsync(() -> {
            log.info("taskCompletableFuture2, start.");
            try { Thread.sleep(3000); } catch (Exception ignored) {}
            log.info("taskCompletableFuture2, end.");
        }, asyncTaskExecutor);
    }

    enum Type {
        SPRING_ASYNC1 {
            @Override
            void runTask(AsyncService asyncService) {
                asyncService.taskSpringAsync1();
            }
        },
        SPRING_ASYNC2 {
            @Override
            void runTask(AsyncService asyncService) {
                asyncService.taskSpringAsync2();
            }
        },
        SPRING_ASYNC3 {
            @Override
            void runTask(AsyncService asyncService) {
                asyncService.taskSpringAsync3();
            }
        },
        COMPLETABLE_FUTURE1 {
            @Override
            void runTask(AsyncService asyncService) {
                asyncService.taskCompletableFuture1();
            }
        },
        COMPLETABLE_FUTURE2 {
            @Override
            void runTask(AsyncService asyncService) {
                asyncService.taskCompletableFuture2();
            }
        };

        abstract void runTask(AsyncService asyncService);
    }
}
