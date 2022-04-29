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
        String userId = (String) session.getAttributes()
            .get(ChatHandshakeWebSocketService.USER_HEADER_NAME);

        Flux<WebSocketMessage> inputStream = session.receive()
            .log("session.receive()")
            .doOnSubscribe(subscription -> {
                log.info("Connection[userId: {}, sessionId: {}] is established", userId, session.getId());
                sessionRepository.addSession(userId, session);
            })
            .doOnComplete(() -> {
                log.info("Connection[userId: {}, sessionId: {}] is closed on complete", userId, session.getId());
                sessionRepository.removeSession(userId, session);
            })
            .doOnError(error -> {
                log.error("Connection[userId: {}, sessionId: {}] is closed on error", userId, session.getId(),
                    error);
                sessionRepository.removeSession(userId, session);
            })
            .doOnCancel(() -> {
                log.info("Connection[userId: {}, sessionId: {}] is closed on cancel", userId, session.getId());
                sessionRepository.removeSession(userId, session);
            })
            .doOnNext(webSocketMessage -> {
                String message = webSocketMessage.getPayloadAsText();
                log.info("Received inbound message from [userId: {}, sessionId: {}]: {}", userId, session.getId(),
                    message);
                pubSubService.sendMessage(String.format("From [%s]: %s", userId, message));
            });

        Flux<WebSocketMessage> pubSubListenSource = pubSubService.listen()
            .map(session::textMessage);

        Mono<Void> input = inputStream.then();
        Mono<Void> output = session.send(pubSubListenSource);

        return Mono.zip(input, output).then();
    }
}
