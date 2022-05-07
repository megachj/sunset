package sunset.reactive.websocketserver.pubsub;

import reactor.core.publisher.Flux;

public interface PubSubService<T> {
    void sendMessage(T t);
    Flux<T> listen(String userId);
}
