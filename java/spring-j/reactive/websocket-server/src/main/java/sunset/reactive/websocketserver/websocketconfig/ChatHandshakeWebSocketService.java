package sunset.reactive.websocketserver.websocketconfig;

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

    public static final String USER_ATTRIBUTE_NAME = "authenticated-user";
    public static final String CONNECTION_LIVE_SECONDS_ATTRIBUTE_NAME = "connection-live-seconds";

    private static final String AUTH_TOKEN_HEADER_NAME = "auth";
    private static final String CONNECTION_LIVE_SECONDS_HEADER_NAME = "connection-live-seconds";

    private static final long DEFAULT_CONNECTION_LIVE_SECONDS = 10;

    public ChatHandshakeWebSocketService(RequestUpgradeStrategy requestUpgradeStrategy) {
        super(requestUpgradeStrategy);
    }

    @PostConstruct
    public void postConstruct() {
        log.info("ChatHandshakeWebSocketService upgradeStrategy, {}", this.getUpgradeStrategy());
    }

    @Override
    public Mono<Void> handleRequest(ServerWebExchange exchange, WebSocketHandler handler) {
        HttpHeaders httpHeaders = exchange.getRequest().getHeaders();

        String userId;
        try {
            userId = authenticateAndGetUserId(httpHeaders);
        } catch (Exception e) {
            log.warn("[handshake error] 인증되지 않은 사용자 접근으로 401 상태 리턴. {}", e.getMessage());

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return Mono.empty();
        }

        long connectionLiveSeconds = getConnectionLiveSeconds(httpHeaders);

        WebSocketHandler webSocketHandlerDecorator = session -> {
            session.getAttributes().put(USER_ATTRIBUTE_NAME, userId);
            session.getAttributes().put(CONNECTION_LIVE_SECONDS_ATTRIBUTE_NAME, connectionLiveSeconds);
            return handler.handle(session);
        };

        return super.handleRequest(exchange, webSocketHandlerDecorator);
    }

    public String authenticateAndGetUserId(HttpHeaders httpHeaders) {
        List<String> values = httpHeaders.get(AUTH_TOKEN_HEADER_NAME);
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("인증 토큰이 없습니다.");
        }

        String authToken = values.get(0);
        if (authToken.substring(0, 1).equals("0")) {
            throw new IllegalArgumentException("인증 토큰이 잘못됐습니다.");
        }

        String userId = authToken.substring(1);

        return userId;
    }

    public long getConnectionLiveSeconds(HttpHeaders httpHeaders) {
        List<String> values = httpHeaders.get(CONNECTION_LIVE_SECONDS_HEADER_NAME);
        if (values == null || values.isEmpty()) {
            return DEFAULT_CONNECTION_LIVE_SECONDS;
        }

        return Long.parseLong(values.get(0));
    }
}
