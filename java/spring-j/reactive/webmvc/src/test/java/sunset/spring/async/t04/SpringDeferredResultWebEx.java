package sunset.spring.async.t04;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * profiles: tomcat-thread-1
 */
@Slf4j
@SpringBootApplication
public class SpringDeferredResultWebEx {

    @RestController
    public static class MyController {
        Queue<DeferredResult<String>> results = new ConcurrentLinkedQueue<>();

        /**
         * tomcat-thread-1: /dr/event 가 실행되는 즉시 100개의 클라이언트에게 바로 응답, http-nio-8080-exec 1개, 다른 워커 스레드 0개
         *
         * @return
         */
        @GetMapping("/dr")
        public DeferredResult<String> dr() {
            log.info("/dr");
            DeferredResult<String> deferredResult = new DeferredResult<>(600000L);
            results.add(deferredResult);
            return deferredResult;
        }

        /**
         * @return: 현재 응답 지연중인 클라이언트 수 리턴
         */
        @GetMapping("/dr/count")
        public String drCount() {
            return String.valueOf(results.size());
        }

        /**
         * 현재 응답 지연중인 클라이언트에게 응답하도록 이벤트 발생
         *
         * @param msg
         * @return
         */
        @GetMapping("/dr/event")
        public String drEvent(String msg) {
            for (DeferredResult<String> dr: results) {
                dr.setResult("Hello " + msg);
                results.remove(dr);
            }
            return "OK";
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringDeferredResultWebEx.class, args);
    }
}
