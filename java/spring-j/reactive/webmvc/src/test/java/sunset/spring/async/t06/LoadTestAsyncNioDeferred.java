package sunset.spring.async.t06;

import sunset.spring.utils.LoadTester;

import java.util.concurrent.BrokenBarrierException;

public class LoadTestAsyncNioDeferred {

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        new LoadTester(100, "http://localhost:8080/rest/async/nio/deferred?idx={idx}")
            .loadForTest();
    }
}
