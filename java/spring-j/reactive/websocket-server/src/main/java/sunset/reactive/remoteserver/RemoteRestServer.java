package sunset.reactive.remoteserver;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import sunset.reactive.apiserver.pubsub.UserNicknamePubSubService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class RemoteRestServer {

    private final UserNicknamePubSubService userNicknamePubSubService;
    private final Scheduler asyncJobScheduler;

    @GetMapping("/api/users/{userId}/nickname")
    public Mono<UserNicknameInfo> getUserNickName(
        @PathVariable("userId") String userId
    ) {
        return Mono.just(UserNicknameInfo.builder()
            .userId(userId)
            .userNickname(userId + "_sync")
            .build());
    }

    @PostMapping("/api/users/{userId}/latest-nickname/pub")
    public Mono<Void> pubLatestUserNickNameEvent(
        @PathVariable("userId") String userId
    ) {
        Mono
            .delay(Duration.ofSeconds(3), asyncJobScheduler)
            .doOnNext(next -> {
                userNicknamePubSubService.sendMessage(
                    UserNicknameInfo.builder()
                        .userId(userId)
                        .userNickname(userId + "_pubEvent")
                        .build()
                );
            })
            .subscribe();

        return Mono.empty();
    }
}
