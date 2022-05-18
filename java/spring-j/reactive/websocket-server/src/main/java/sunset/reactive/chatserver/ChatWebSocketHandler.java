package sunset.reactive.chatserver;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketMessage.Type;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import sunset.reactive.chatserver.model.ChatMessage;
import sunset.reactive.common.pubsub.PubSubService;
import sunset.reactive.common.websocketconfig.handshake.AuthUser;
import sunset.reactive.common.websocketconfig.handshake.HandshakeWebSocketMainService;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    private final PubSubService<ChatMessage> simpleChatMessagePubSubService;

    private final Scheduler wsConnTimer;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        AuthUser authUser = (AuthUser) session.getAttributes()
            .get(HandshakeWebSocketMainService.AUTH_USER_ATTRIBUTE_NAME);
        long connectionLiveSeconds = (Long) session.getAttributes()
            .get(HandshakeWebSocketMainService.CONNECTION_LIVE_SECONDS_ATTRIBUTE_NAME);

        Mono<Void> chatMsgInput = session.receive()
            .take(Duration.ofSeconds(connectionLiveSeconds), wsConnTimer)
            .filter(webSocketMessage -> webSocketMessage.getType() == Type.TEXT)
            .map(webSocketMessage -> ChatMessage.parsePayload(authUser.getId(),
                webSocketMessage.getPayloadAsText()))
            .doOnNext(chatMessage -> {
                simpleChatMessagePubSubService.sendMessage(chatMessage);
            })
            .then();

        Flux<WebSocketMessage> chatMsgOutputSource = simpleChatMessagePubSubService.listen(authUser.getId())
            .map(chatMessage -> String.format("from %s: %s",
                chatMessage.getFromUserId(),
                chatMessage.getContent())
            )
            .map(session::textMessage);

        Mono<Void> output = session.send(
            Flux.merge(chatMsgOutputSource)
        );

        return Mono.zip(chatMsgInput, output).then();
    }
}
