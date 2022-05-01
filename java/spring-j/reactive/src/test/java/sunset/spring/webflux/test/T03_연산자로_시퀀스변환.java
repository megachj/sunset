package sunset.spring.webflux.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Instant;

@Slf4j
public class T03_연산자로_시퀀스변환 {
    // 리액티브 스트림 연산자 공식 문서: http://projectreactor.io/docs/core/release/reference/#which-operator

    @Test
    public void 매핑() {
        Flux.just("A", "B", "C")
            .map(e -> e + " hello")
            .subscribe(e -> log.info("{}", e));

        Flux.range(2018, 5)
            .timestamp() // tuple 로 변환하고 T1에 현재 타임 스탬프 추가
            .index() // tuple 로 변환하고 T1에 인덱스 추가
            .subscribe(
                e -> log.info("index: {}, ts: {}, value: {}",
                    e.getT1(), Instant.ofEpochMilli(e.getT2().getT1()), e.getT2().getT2())
            );
    }

    @Test
    public void 필터링() {
        Flux.range(1, 100)
            .filter(e -> e % 2 == 1)
            .take(10)
            .skipUntil(e -> e >= 10)
            .elementAt(2)
            .single()
            .subscribe(
                e -> log.info("onNext: {}", e),
                e -> log.error("onError: ", e),
                () -> log.info("onComplete")
            );
    }

    public void 수집() {
        
    }

    public void 줄이기() {

    }

    public void 조합() {

    }

    public void 일괄처리() {

    }

    public void flatMap_concatMap_flatMapSequential() {

    }

    public void 샘플링() {

    }

    public void 블로킹구조_전환() {

    }

    public void 파이프라인중간_처리() {

    }

    public void 데이터와_신호_변환() {

    }
}
