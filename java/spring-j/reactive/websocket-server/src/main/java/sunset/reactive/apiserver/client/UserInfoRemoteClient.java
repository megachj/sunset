package sunset.reactive.apiserver.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import sunset.reactive.remoteserver.UserInfo;

@Slf4j
@Service
public class UserInfoRemoteClient {

    private final WebClient webClient;

    public UserInfoRemoteClient(
        WebClient.Builder webClientBuilder
    ) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    public Mono<UserInfo> getUserInfo(String userId) {
        return this.webClient
            .get()
            .uri(uriBuilder -> uriBuilder.path("/api/users/{userId}")
                .pathSegment()
                .build(userId)
            )
            .retrieve()
            .bodyToMono(UserInfo.class);
    }

    public Mono<Void> collectLatestUserInfo(String userId) {
        return this.webClient
            .post()
            .uri(uriBuilder -> uriBuilder.path("/api/users/{userId}/collect-latest")
                .pathSegment()
                .build(userId)
            )
            .retrieve()
            .bodyToMono(Void.class);
    }
}
