package sunset.reactive.apiserver.client;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import sunset.reactive.remoteserver.UserScoreInfo;

@Slf4j
@Service
public class UserScoreRemoteClient {

    private final WebClient webClient;

    public UserScoreRemoteClient(
        WebClient.Builder webClientBuilder
    ) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    public Mono<UserScoreInfo> searchUserScoreInfo(UserScoreRequest request) {
        return this.webClient
            .get()
            .uri(uriBuilder -> uriBuilder.path("/api/users/{userId}/score")
                .queryParam("category", request.category)
                .pathSegment()
                .build(request.userId)
            )
            .retrieve()
            .bodyToMono(UserScoreInfo.class);
    }

    public Mono<Void> collectLatestUserScoreInfo(UserScoreRequest request) {
        return this.webClient
            .post()
            .uri(uriBuilder -> uriBuilder.path("/api/users/{userId}/score/collect-latest")
                .queryParam("category", request.category)
                .pathSegment()
                .build(request.userId)
            )
            .retrieve()
            .bodyToMono(Void.class);
    }

    @Value(staticConstructor = "of")
    public static class UserScoreRequest {

        String userId;
        String category;
    }
}
