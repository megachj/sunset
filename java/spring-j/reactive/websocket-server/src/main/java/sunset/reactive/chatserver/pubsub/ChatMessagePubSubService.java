package sunset.reactive.chatserver.pubsub;

import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.FluxSink.OverflowStrategy;
import sunset.reactive.chatserver.model.ChatMessage;
import sunset.reactive.common.pattern.observer.Observable;
import sunset.reactive.common.pattern.observer.SingleObservable;
import sunset.reactive.common.pubsub.PubSubService;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatMessagePubSubService implements PubSubService<ChatMessage> {

    private Observable<ChatMessage> observable;
    private ConnectableFlux<ChatMessage> hotSourcePublisher;

    @PostConstruct
    public void init() {
        observable = SingleObservable.newObservable();

        hotSourcePublisher = Flux.create(
                (FluxSink<ChatMessage> sink) -> observable.add(sink::next),
                OverflowStrategy.IGNORE
            )
            .publish();
        hotSourcePublisher.connect();
    }

    @Override
    public void publish(ChatMessage chatMessage) {
        Flux.just(chatMessage)
            // .delayElements(Duration.ofSeconds(1L))
            .doOnNext(next -> observable.notifyObservers(next))
            .subscribe();
    }

    @Override
    public Flux<ChatMessage> subscribe(String userId) {
        return hotSourcePublisher
            .filter(chatMessage -> userId.equals(chatMessage.getToUserId()));
    }
}
