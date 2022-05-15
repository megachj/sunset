package sunset.spring.async.webflux;

import sunset.springutils.LoadTester;

import java.util.concurrent.BrokenBarrierException;

public class LoadTest {

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        new LoadTester(100, "http://localhost:8080/rest?idx={idx}")
            .loadForTest();
    }
}
