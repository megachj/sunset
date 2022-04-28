package sunset.reactive.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class ChatHandshakeWebSocketService extends HandshakeWebSocketService {

    // TODO: http header 에서 토큰으로 사용자 인증 작업
    @Override
    public Mono<Void> handleRequest(ServerWebExchange exchange, WebSocketHandler handler) {
        return super.handleRequest(exchange, handler);
    }
}
