package sunset.reactive.apiserver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import sunset.reactive.remoteserver.UserNicknameInfo;

@Slf4j
@Service
public class UserNicknameSearchService {

    private final WebClient webClient;

    public UserNicknameSearchService(
        WebClient.Builder webClientBuilder
    ) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    public Mono<UserNicknameInfo> getUserNickname(String userId) {
        return this.webClient
            .get()
            .uri(uriBuilder -> uriBuilder.path("/api/users/{userId}/nickname")
                .pathSegment()
                .build(userId)
            )
            .retrieve()
            .bodyToMono(UserNicknameInfo.class);
    }

    public Mono<Void> pubLatestUserNickname(String userId) {
        return this.webClient
            .post()
            .uri(uriBuilder -> uriBuilder.path("/api/users/{userId}/latest-nickname/pub")
                .pathSegment()
                .build(userId)
            )
            .retrieve()
            .bodyToMono(Void.class);
    }
}
