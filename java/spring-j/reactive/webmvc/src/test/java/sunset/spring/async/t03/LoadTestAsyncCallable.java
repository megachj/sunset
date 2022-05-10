package sunset.spring.async.t03;

import sunset.spring.utils.LoadTester;

import java.util.concurrent.BrokenBarrierException;

public class LoadTestAsyncCallable {

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        new LoadTester(100, "http://localhost:8080/async/callable")
            .loadForTest();
    }
}
