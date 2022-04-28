package sunset.spring.reactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
public class T01_1_시퀀스생성 {

    @Test
    public void 시퀀스만들기_기본() {
        // element 로 만들기
        Mono<String> mono1 = Mono.just("One");
        Mono<String> mono2 = Mono.justOrEmpty(null);
        Mono<String> mono3 = Mono.justOrEmpty(Optional.empty());

        Flux<String> flux1 = Flux.just("Hello", "world");
        Flux<Integer> flux2 = Flux.fromArray(new Integer[]{1, 2, 3});
        Flux<Integer> flux3 = Flux.fromIterable(Arrays.asList(9, 8, 7));

        // range 로 만들기: 2010, 2010+1, ..., 2010+8
        Flux<Integer> flux4 = Flux.range(2010, 9);

        // 비동기 래핑: HTTP 요청, DB 쿼리 같은 작업시에 사용
        Mono<String> mono4 = Mono.fromCallable(() -> "http request.. response!");
        Mono<String> mono5 = Mono.fromSupplier(() -> "http request.. response!");
        Mono<String> mono6 = Mono.fromRunnable(() -> {
            log.info("Hello world");
        });

        // 빈 스트림, 오류만 포함하는 스트림
        Mono<String> emptyMono = Mono.empty(); // 데이터 없이 완료 신호
        Mono<String> neverMono = Mono.never(); // 데이터, 완료, 에러 모든 신호가 없음
        Mono<String> errorMono = Mono.error(new RuntimeException("Unknown id")); // 에러 신호

        Flux<String> emptyFlux = Flux.empty();
        Flux<String> neverFlux = Flux.never();
        Flux<String> errorFlux = Flux.error(new RuntimeException("Unknown id"));
    }

    @Test
    public void defer_시퀀스만들기() {
        // defer 는 Mono<String> 을 구독하는 시점에 세션 검사
        String sessionId = "KR-2231";
        Mono<String> userInfo = Mono.defer(() -> isValidSession(sessionId) 
                ? Mono.just("유저정보") : Mono.error(new RuntimeException("Invalid user session")));
        
        // 이 라인이 실행될때 세션 검사
        Mono<String> userInfo1 = isValidSession(sessionId)
                ? Mono.just("유저정보") : Mono.error(new RuntimeException("Invalid user session"));
    }

    private boolean isValidSession(String sessionId) {
        if (!StringUtils.hasText(sessionId)) {
            return false;
        }
        return sessionId.contains("KR");
    }
}
