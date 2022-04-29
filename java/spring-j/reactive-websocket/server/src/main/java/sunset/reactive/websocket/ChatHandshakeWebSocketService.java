package sunset.reactive.websocket;

import java.util.List;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
public class ChatHandshakeWebSocketService extends HandshakeWebSocketService {

    public static final String USER_HEADER_NAME = "authenticated-user";

    private static final String AUTH_TOKEN_HEADER_NAME = "auth";

    public ChatHandshakeWebSocketService(RequestUpgradeStrategy requestUpgradeStrategy) {
        super(requestUpgradeStrategy);
    }

    @PostConstruct
    public void postConstruct() {
        log.info("ChatHandshakeWebSocketService upgradeStrategy, {}", this.getUpgradeStrategy());
    }

    @Override
    public Mono<Void> handleRequest(ServerWebExchange exchange, WebSocketHandler handler) {
        try {
            String userId = authenticateUser(exchange);

            return super.handleRequest(
                exchange,
                session -> {
                    session.getAttributes().put(USER_HEADER_NAME, userId);
                    return handler.handle(session);
                });
        } catch (Exception e) {
            log.warn("[handshake error] 인증되지 않은 사용자 접근으로 401 상태 리턴. {}", e.getMessage());

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return Mono.empty();
        }
    }

    public String authenticateUser(ServerWebExchange exchange) {
        HttpHeaders httpHeaders = exchange.getRequest().getHeaders();

        List<String> authTokens = httpHeaders.get(AUTH_TOKEN_HEADER_NAME);
        if (authTokens == null || authTokens.isEmpty()) {
            throw new IllegalArgumentException("인증 토큰이 없습니다.");
        }

        String authToken = authTokens.get(0);
        if (authToken.substring(0, 1).equals("0")) {
            throw new IllegalArgumentException("인증 토큰이 잘못됐습니다.");
        }

        String userId = authToken.substring(1);

        return userId;
    }
}
