package sunset.reactive.apiserver.service;

import static sunset.reactive.common.utils.ReactorLoggingUtils.PREFIX;
import static sunset.reactive.common.utils.ReactorLoggingUtils.SIGNALS;
import java.util.logging.Level;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import sunset.reactive.apiserver.client.UserScoreRemoteClient;
import sunset.reactive.apiserver.model.body.UserScoreRequestV1Body;
import sunset.reactive.apiserver.model.body.UserScoreResponseV1Body;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserScoreService {

    private final UserScoreRemoteClient userScoreRemoteClient;

    public Flux<UserScoreResponseV1Body> process(String userId, UserScoreRequestV1Body requestBody) {
        collectLatestUserScoreInfo(userId, requestBody.getCategories());
        return searchUserScoreInfo(userId, requestBody.getCategories());
    }

    private Flux

    private Flux<UserScoreResponseV1Body> searchUserScoreInfo(String userId, String category) {

    }

    private void collectLatestUserScoreInfo(String userId, String category) {
        flux.defer(() -> {
                return Flux.just(userScoreRemoteClient.collectLatestUserScoreInfo(userId, category));
            })
            .log(PREFIX + "service.userscore.CollectLatestUserInfo", Level.FINE, SIGNALS)
            .subscribe();
    }
}
