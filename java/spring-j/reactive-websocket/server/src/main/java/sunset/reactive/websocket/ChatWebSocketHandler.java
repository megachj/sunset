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
import sunset.reactive.websocket.model.ChatMessage;
import sunset.reactive.websocket.pubsub.PubSubService;
import sunset.reactive.websocket.repository.SessionRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    private static final Duration PING_INTERVAL = Duration.ofSeconds(1);
    private static final byte[] PING_PAYLOAD = new byte[0];

    private final SessionRepository sessionRepository;
    private final PubSubService<ChatMessage> simpleChatMessagePubSubService;
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
                ChatMessage chatMessage = ChatMessage.parsePayload(userId, webSocketMessage.getPayloadAsText());

                log.info("From[sessionId: {}]: {}", session.getId(), chatMessage);
                simpleChatMessagePubSubService.sendMessage(chatMessage);
            });

        Flux<WebSocketMessage> pubSubListenSource = simpleChatMessagePubSubService.listen(userId)
            .map(chatMessage -> String.format("from %s: %s",
                chatMessage.getFromUserId(),
                chatMessage.getContent())
            )
            .map(session::textMessage);

        Mono<Void> input = inputStream.then();
        Mono<Void> output = session.send(pubSubListenSource);

        return Mono.zip(input, output).then();
    }
}
