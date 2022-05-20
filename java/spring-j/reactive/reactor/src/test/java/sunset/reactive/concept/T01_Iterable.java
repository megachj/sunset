package sunset.reactive.concept;

import java.util.Iterator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class T01_Iterable {

    @Test
    public void Iterable_Pull방식_DataSource() {
        // Iterable 객체 = Data Source
        // iter.iterator() 를 하면 항상 새로운 객체가 리턴 => iter 를 사용하는 곳에서 항상 첫 데이터부터 받을 수 있음
        Iterable<Integer> iter = () -> new Iterator<>() {
            final static int MAX = 10;
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < MAX;
            }

            @Override
            public Integer next() {
                return ++i;
            }
        };

        // Data Source 에서 데이터를 한 개씩 Pull 해온다.
        for (Integer i : iter) { // for-each 문
            log.debug("for-each: {}", i);
        }
        for (Iterator<Integer> it = iter.iterator(); it.hasNext(); it.hasNext()) { // for 문
            log.debug("for: {}", it.next());
        }
    }
}
