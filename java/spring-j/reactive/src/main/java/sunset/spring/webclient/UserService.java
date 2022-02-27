package sunset.spring.webclient;

import sunset.spring.webclient.external.ExternalUserApiController;
import sunset.spring.webclient.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UserService {
    private final WebClient webClient;

    public UserService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    public Mono<String> getUserName(int userId) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance().path(ExternalUserApiController.GET_USER)
            .buildAndExpand(userId);
        return webClient
            .get()
            .uri(uriComponents.toUri())
            .retrieve()
            .bodyToMono(User.class)
            .flatMap(user -> {
                log.info("user: {}", user);
                return Mono.justOrEmpty(user.getName());
            });
    }

    public Mono<Boolean> existsUser(String name) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance().path(ExternalUserApiController.GET_FIRST_SAME_NAME_USER)
            .queryParam("name", name)
            .buildAndExpand();
        return webClient
            .get()
            .uri(uriComponents.toUri())
            .retrieve()
            .bodyToMono(User.class)
            .flatMap(user -> {
                log.info("user: {}", user);
                return Mono.just(true);
            });
    }
    
    public Mono<User> addUser(User user) {
        return webClient
            .post()
            .uri(ExternalUserApiController.POST_ADD_USER)
            .body(Mono.just(user), User.class) // BodyInserters.fromValue(user) 이렇게도 쓸 수 있음.
            .retrieve()
            .bodyToMono(User.class);
    }
}
