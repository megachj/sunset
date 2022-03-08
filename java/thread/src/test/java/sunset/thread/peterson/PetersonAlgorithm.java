package sunset.thread.peterson;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 피터슨 알고리즘: 상호배제(Mutex)의 대표적인 알고리즘
 * - n 개 스레드도 Mutex 만족시킬 수 있다. (i 스레드가 i+1 스레드에게 턴을 넘기는 식으로 구현)
 * - 상호배제를 하도록하는 flag, turn 이 메모리 가시성(volatile)을 얻을 수만 있으면 Mutex 를 보장한다.
 */
public class PetersonAlgorithm {

    static int count = 0; // 공공재 갯수

    // NOTE: flag, turn 은 메모리 가시성을 얻기 위해서 volatile 이 필수이다.(synchronized, Atomic~ 변수를 사용하지 않으므로)
    // 신호, 공유자원을 사용하고 싶다라고 표현하기 위한 변수, 임계구역에 들어갈 때는 true, 나올 때는 false 로 설정
    volatile static boolean[] flag = new boolean[2]; // flag[0]: producer, flag[1]: consumer
    // 차례, 누구 차례인지를 명시하는 변수, 0이면 0번째 스레드(producer)가 임계 구역에 들어간다.
    volatile static int turn = 0; // 0: producer, 1: consumer

    @RepeatedTest(10000)
    public void 피터슨알고리즘() {
        Thread producer = new Thread(new Producer());
        Thread consumer = new Thread(new Consumer());

        producer.start();
        consumer.start();

        try {
            producer.join(); // producer 가 종료할 때까지 대기
            consumer.join(); // consumer 가 종료할 때까지 대기
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        assertEquals(0, count);
    }

    static class Producer implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 10_000; ++i) {
                flag[0] = true; // producer 가 공유자원을 사용하고 싶다는 신호전달
                turn = 1; // 먼저 consumer 에게 턴을 양보한다

                // busy waits 상태(소비자 신호가 false 가 되거나 내턴이 될때까지 기다림)
                while (flag[1] && turn == 1) ;

                // ### critical section: start ###
                count++;

                flag[0] = false; // 나올땐 공유자원을 안쓴다는 신호를 전달
                // ### critical section: end ###
            }
        }
    }

    static class Consumer implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 10_000; ++i) {
                flag[1] = true;
                turn = 0;

                while (flag[0] && turn == 0) ;

                // ### critical section: start ###
                count--;

                flag[1] = false;
                // ### critical section: end ###
            }
        }
    }
}
