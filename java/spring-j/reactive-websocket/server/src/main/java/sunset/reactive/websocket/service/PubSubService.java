package sunset.reactive.websocket.service;

import reactor.core.publisher.Flux;

public interface PubSubService {

    void sendMessage(String message);
    Flux<String> listen();
}
