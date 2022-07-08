package sunset.reactive.common.pubsub;

import reactor.core.publisher.Flux;

public interface PubSubService<T> {
    void publish(T t);
    Flux<T> subscribe(String userId);
}
