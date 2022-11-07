package sunset.reactive.remoteserver;

import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import sunset.reactive.apiserver.pubsub.UserScorePubSubService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserScoreRemoteRestServer {

    private final UserScorePubSubService userScorePubSubService;
    private final Scheduler asyncJobScheduler;

    @GetMapping("/api/users/{userId}/score")
    public Mono<UserScoreInfo> searchUserScoreInfo(
        @PathVariable("userId") String userId,
        @RequestParam String category
    ) {
        return Mono.just(UserScoreInfo.builder()
            .userId(userId)
            .category(category)
            .score(50L)
            .scoreUpdatedAt(LocalDateTime.now().minusMinutes(30))
            .build());
    }

    @PostMapping("/api/users/{userId}/score/collect-latest")
    public Mono<Void> collectLatestUserScoreInfo(
        @PathVariable("userId") String userId,
        @RequestParam String category
    ) {
        Mono
            .delay(Duration.ofSeconds(3), asyncJobScheduler)
            .doOnNext(next -> {
                userScorePubSubService.publish(
                    UserScoreInfo.builder()
                        .userId(userId)
                        .category(category)
                        .score(100L)
                        .scoreUpdatedAt(LocalDateTime.now())
                        .build()
                );
            })
            .subscribe();

        return Mono.empty();
    }
}
