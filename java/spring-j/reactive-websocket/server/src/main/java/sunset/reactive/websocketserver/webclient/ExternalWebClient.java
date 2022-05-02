package sunset.reactive.websocketserver.webclient;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import sunset.reactive.externalrestserver.UserNicknameInfo;

@Component
public class ExternalWebClient {

    private final WebClient webClient;

    public ExternalWebClient(
        WebClient.Builder webClientBuilder
    ) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    public Mono<UserNicknameInfo> getSyncUserNickName(String userId) {
        return this.webClient
            .get()
            .uri(uriBuilder -> uriBuilder.path("/api/users/{userId}/nickname/sync")
                .pathSegment()
                .build(userId)
            )
            .retrieve()
            .bodyToMono(UserNicknameInfo.class);
    }

    public Mono<Void> getAsyncUserNickName(String userId) {
        return this.webClient
            .get()
            .uri(uriBuilder -> uriBuilder.path("/api/users/{userId}/nickname/async")
                .pathSegment()
                .build(userId)
            )
            .retrieve()
            .bodyToMono(Void.class);
    }
}
