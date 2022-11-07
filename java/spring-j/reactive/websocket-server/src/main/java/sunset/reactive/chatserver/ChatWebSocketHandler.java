package sunset.reactive.chatserver;

import static sunset.reactive.common.utils.ReactorLoggingUtils.PREFIX;
import static sunset.reactive.common.utils.ReactorLoggingUtils.SIGNALS;
import java.util.logging.Level;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketMessage.Type;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sunset.reactive.chatserver.model.ChatMessage;
import sunset.reactive.common.pubsub.PubSubService;
import sunset.reactive.common.websocketconfig.handshake.AuthUser;
import sunset.reactive.common.websocketconfig.handshake.HandshakeWebSocketMainService;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    private final PubSubService<ChatMessage> chatMessagePubSubService;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        AuthUser authUser = (AuthUser) session.getAttributes()
            .get(HandshakeWebSocketMainService.AUTH_USER_ATTRIBUTE_NAME);

        Mono<Void> chatMsgInput = session.receive()
            .filter(wsMessage -> wsMessage.getType() == Type.TEXT)
            .map(WebSocketMessage::getPayloadAsText)
            .log(PREFIX + "chat.Receive", Level.FINE, SIGNALS)
            .map(wsMessage -> ChatMessage.serializeFromReceivedMessage(authUser.getId(), wsMessage))
            .doOnNext(chatMessagePubSubService::publish)
            .then();

        Flux<WebSocketMessage> chatMsgOutputSource = chatMessagePubSubService.subscribe(authUser.getId())
            .log(PREFIX + "chat.Send", Level.FINE, SIGNALS)
            .map(ChatMessage::deserializeToSentMessage)
            .map(session::textMessage);

        Mono<Void> outputSources = session.send(
            Flux.merge(chatMsgOutputSource)
        );

        /*
        return 한 스트림만 구독된다.
         - chatMsgInput 을 리턴하지 않으면 구독되지 않는다. 즉 웹소켓 인풋 메시지가 들어오지 않는다.
         - outputSources 을 리턴하지 않으면 구독되지 않는다. 즉 웹소켓을 통해 메시지가 나가지 않는다.
         */
        return Mono.zip(chatMsgInput, outputSources).then();
    }
}
