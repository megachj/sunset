package sunset.reactive.remoteserver;

import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import sunset.reactive.apiserver.pubsub.UserInfoPubSubService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class RemoteRestServer {

    private final UserInfoPubSubService userInfoPubSubService;
    private final Scheduler asyncJobScheduler;

    @GetMapping("/api/users/{userId}")
    public Mono<UserInfo> getUserInfo(
        @PathVariable("userId") String userId
    ) {
        return Mono.just(UserInfo.builder()
            .userId(userId)
            .score(50L)
            .scoreUpdatedAt(LocalDateTime.now().minusMinutes(30))
            .build());
    }

    @PostMapping("/api/users/{userId}/collect-latest")
    public Mono<Void> collectUserLatestInfo(
        @PathVariable("userId") String userId
    ) {
        Mono
            .delay(Duration.ofSeconds(3), asyncJobScheduler)
            .doOnNext(next -> {
                userInfoPubSubService.sendMessage(
                    UserInfo.builder()
                        .userId(userId)
                        .score(100L)
                        .scoreUpdatedAt(LocalDateTime.now())
                        .build()
                );
            })
            .subscribe();

        return Mono.empty();
    }
}
