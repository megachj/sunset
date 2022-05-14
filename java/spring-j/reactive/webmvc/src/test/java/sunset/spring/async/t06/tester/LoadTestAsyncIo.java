package sunset.spring.async.t06.tester;

import sunset.spring.utils.LoadTester;

import java.util.concurrent.BrokenBarrierException;

public class LoadTestAsyncIo {

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        new LoadTester(100, "http://localhost:8080/rest/async/io?idx={idx}")
            .loadForTest();
    }
}
