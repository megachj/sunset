package sunset.reactive.apiserver.pubsub;

import java.time.Duration;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.FluxSink.OverflowStrategy;
import sunset.reactive.common.pattern.observer.Observable;
import sunset.reactive.common.pattern.observer.SingleObservable;
import sunset.reactive.common.pubsub.PubSubService;
import sunset.reactive.remoteserver.UserScoreInfo;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserScorePubSubService implements PubSubService<UserScoreInfo> {

    private Observable<UserScoreInfo> observable;
    private ConnectableFlux<UserScoreInfo> hotSourcePublisher;

    @PostConstruct
    public void init() {
        observable = SingleObservable.newObservable();

        hotSourcePublisher = Flux.create(
                (FluxSink<UserScoreInfo> sink) -> observable.add(sink::next),
                OverflowStrategy.IGNORE
            )
            .publish();
        hotSourcePublisher.connect();
    }

    @Override
    public void publish(UserScoreInfo data) {
        Flux.just(data)
            .delayElements(Duration.ofSeconds(1L))
            .doOnNext(next -> observable.notifyObservers(next))
            .subscribe();
    }

    @Override
    public Flux<UserScoreInfo> subscribe(String userId) {
        return hotSourcePublisher
            .filter(userNicknameInfo -> userId.equals(userNicknameInfo.getUserId()));
    }
}
