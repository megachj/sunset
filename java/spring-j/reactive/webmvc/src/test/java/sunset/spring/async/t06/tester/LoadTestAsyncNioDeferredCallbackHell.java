package sunset.spring.async.t06.tester;

import sunset.spring.utils.LoadTester;

import java.util.concurrent.BrokenBarrierException;

public class LoadTestAsyncNioDeferredCallbackHell {

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        new LoadTester(100, "http://localhost:8080/rest/async/nio/deferred/callback-hell?idx={idx}")
            .loadForTest();
    }
}
