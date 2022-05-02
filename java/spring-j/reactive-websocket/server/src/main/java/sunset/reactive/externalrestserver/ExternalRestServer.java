package sunset.reactive.externalrestserver;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import sunset.reactive.websocketserver.pubsub.AsyncUserNicknamePubSubService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ExternalRestServer {

    private final AsyncUserNicknamePubSubService asyncUserNicknamePubSubService;
    private final Scheduler asyncJobScheduler;

    @GetMapping("/api/users/{userId}/nickname/sync")
    public Mono<UserNicknameInfo> getSyncUserNickName(
        @PathVariable("userId") String userId
    ) {
        log.info("External rest server: sync");
        return Mono.just(UserNicknameInfo.builder()
            .userId(userId)
            .userNickname("이말년")
            .build());
    }

    @GetMapping("/api/users/{userId}/nickname/async")
    public Mono<Void> getAsyncUserNickName(
        @PathVariable("userId") String userId
    ) {
        log.info("External rest server: async start");
        Mono
            .delay(Duration.ofSeconds(5), asyncJobScheduler)
            .doOnNext(next -> {
                asyncUserNicknamePubSubService.sendMessage(
                    UserNicknameInfo.builder()
                        .userId(userId)
                        .userNickname("침착맨")
                        .build()
                );
            })
            .subscribe();
        log.info("External rest server: async end");

        return Mono.empty();
    }
}
