package sunset.reactive.concept;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@SuppressWarnings("deprecation")
@Slf4j
public class T02_1_Observable {

    @Test
    public void Observable_Push방식_DataSource() {
        // Observer: Data 를 전달받는 객체
        Observer ob = (observable, data) -> {
            log.debug("observable[{}] push data[{}] to observer", observable, data);
        };

        // Observable: Data Source
        IntObservable io = new IntObservable();
        io.addObserver(ob);

        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(io); // data push 시작

        log.debug("Exit..");
        es.shutdown();
    }

    static class IntObservable extends Observable implements Runnable {

        @Override
        public void run() {
            for (int i = 1; i <= 10; i++) {
                setChanged();
                notifyObservers(i);
            }
        }
    }
}
