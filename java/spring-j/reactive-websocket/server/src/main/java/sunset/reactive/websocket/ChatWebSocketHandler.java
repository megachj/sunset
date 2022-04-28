package sunset.reactive.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sunset.reactive.websocket.repository.SessionRepository;
import sunset.reactive.websocket.service.PubSubService;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    private final SessionRepository sessionRepository;
    private final PubSubService pubSubService;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<WebSocketMessage> inputStream = session.receive()
            .log("session.receive()")
            .doOnSubscribe(subscription -> {
                log.info("Connection[{}] is established", session.getId(), subscription);
                sessionRepository.addSession(session.getId(), session); // FIXME: userId
            })
            .doOnComplete(() -> {
                log.info("Connection[{}] is closed on complete", session.getId());
                sessionRepository.removeSession(session.getId(), session); // FIXME: userId
            })
            .doOnError(error -> {
                log.error("Connection[{}] is closed on error", session.getId(), error);
                sessionRepository.removeSession(session.getId(), session); // FIXME: userId
            })
            .doOnCancel(() -> {
                log.info("Connection[{}] is closed on cancel", session.getId());
                sessionRepository.removeSession(session.getId(), session); // FIXME: userId
            })
            .doOnNext(webSocketMessage -> {
                String message = webSocketMessage.getPayloadAsText();
                log.info("Received inbound message from [{}]: {}", session.getId(), message);
                pubSubService.sendMessage(String.format("From [%s]: %s", session.getId(), message));
            });

        Flux<WebSocketMessage> pubSubListenSource = pubSubService.listen()
            .map(session::textMessage);

        Mono<Void> input = inputStream.then();
        Mono<Void> output = session.send(pubSubListenSource);

        return Mono.zip(input, output).then();
    }
}
