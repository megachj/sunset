package sunset.reactive.apiserver;

import static sunset.reactive.common.utils.ReactorLoggingUtils.PREFIX;
import static sunset.reactive.common.utils.ReactorLoggingUtils.SIGNALS;
import java.time.Duration;
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
import sunset.reactive.apiserver.client.UserInfoRemoteClient;
import sunset.reactive.common.websocketconfig.handshake.AuthUser;
import sunset.reactive.common.websocketconfig.handshake.HandshakeWebSocketMainService;

@Slf4j
@RequiredArgsConstructor
@Component
public class ApiWebSocketHandler implements WebSocketHandler {

    private final UserInfoRemoteClient userInfoRemoteClient;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        AuthUser authUser = (AuthUser) session.getAttributes()
            .get(HandshakeWebSocketMainService.AUTH_USER_ATTRIBUTE_NAME);
        long connectionLiveSeconds = (Long) session.getAttributes()
            .get(HandshakeWebSocketMainService.CONNECTION_LIVE_SECONDS_ATTRIBUTE_NAME);

        Flux<String> requestTextSource = session.receive()
            .take(Duration.ofSeconds(connectionLiveSeconds))
            .filter(message -> message.getType() == Type.TEXT)
            .map(WebSocketMessage::getPayloadAsText)
            .log(PREFIX + "api.Receive", Level.FINE, SIGNALS);

        return null;
    }
}
