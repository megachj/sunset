package sunset.reactive.websocket;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import sunset.reactive.websocket.repository.SessionRepository;
import sunset.reactive.websocket.service.PubSubService;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    private final SessionRepository sessionRepository;
    private final PubSubService pubSubService;
    private final Scheduler wsConnTimer;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String userId = (String) session.getAttributes()
            .get(ChatHandshakeWebSocketService.USER_ATTRIBUTE_NAME);
        long connectionLiveSeconds = (Long) session.getAttributes()
            .get(ChatHandshakeWebSocketService.CONNECTION_LIVE_SECONDS_ATTRIBUTE_NAME);

        Flux<WebSocketMessage> inputStream = session.receive()
            //.log("session.receive()")
            .take(Duration.ofSeconds(connectionLiveSeconds), wsConnTimer)
            .doOnSubscribe(subscription -> {
                log.info("Connection[userId: {}, sessionId: {}, connection-live-seconds: {}] is established",
                    userId, session.getId(), connectionLiveSeconds);
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
