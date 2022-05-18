package sunset.reactive.apiserver;

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
import sunset.reactive.apiserver.service.UserNicknameSearchService;
import sunset.reactive.common.websocketconfig.handshake.AuthUser;
import sunset.reactive.common.websocketconfig.handshake.HandshakeWebSocketMainService;

@Slf4j
@RequiredArgsConstructor
@Component
public class ApiWebSocketHandler implements WebSocketHandler {

    private final UserNicknameSearchService userNicknameSearchService;

    private final Scheduler wsConnTimer;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        AuthUser authUser = (AuthUser) session.getAttributes()
            .get(HandshakeWebSocketMainService.AUTH_USER_ATTRIBUTE_NAME);
        long connectionLiveSeconds = (Long) session.getAttributes()
            .get(HandshakeWebSocketMainService.CONNECTION_LIVE_SECONDS_ATTRIBUTE_NAME);

        // TODO
        Flux<WebSocketMessage> requestSource = session.receive()
            .take(Duration.ofSeconds(connectionLiveSeconds), wsConnTimer)
            .filter(message -> message.getType() == Type.TEXT);

        return null;
    }
}
