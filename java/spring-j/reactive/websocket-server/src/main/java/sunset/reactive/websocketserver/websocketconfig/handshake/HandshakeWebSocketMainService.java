package sunset.reactive.websocketserver.websocketconfig.handshake;

import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class HandshakeWebSocketMainService extends HandshakeWebSocketService {

    public static final String AUTH_USER_ATTRIBUTE_NAME = "auth-user";

    public static final String CONNECTION_LIVE_SECONDS_ATTRIBUTE_NAME = "connection-live-seconds";
    private static final String CONNECTION_LIVE_SECONDS_HEADER_NAME = "connection-live-seconds";
    private static final long DEFAULT_CONNECTION_LIVE_SECONDS = 600;

    private static final List<String> RESPONSE_CONTENT_TYPE = List.of(
        "application/json;charset=UTF-8"
    );

    private final SessionAuthService sessionAuthService;

    public HandshakeWebSocketMainService(RequestUpgradeStrategy requestUpgradeStrategy,
        SessionAuthService sessionAuthService
    ) {
        super(requestUpgradeStrategy);
        this.sessionAuthService = sessionAuthService;
    }

    @PostConstruct
    public void postConstruct() {
        log.info("HandshakeWebSocketMainService upgradeStrategy, {}", this.getUpgradeStrategy());
    }

    @Override
    public Mono<Void> handleRequest(ServerWebExchange exchange, WebSocketHandler handler) {
        HttpHeaders httpHeaders = exchange.getRequest().getHeaders();

        AuthUser authUser;
        try {
            authUser = sessionAuthService.authenticate(exchange.getRequest());
        } catch (Exception e) {
            log.warn("[Handshake Error] authentication failed", e);
            setAuthenticateFailedInfoToResponse(e, exchange.getResponse());
            return Mono.empty();
        }

        long connectionLiveSeconds = getConnectionLiveSeconds(httpHeaders);

        WebSocketHandler webSocketHandlerDecorator = session -> {
            session.getAttributes().put(AUTH_USER_ATTRIBUTE_NAME, authUser);
            session.getAttributes().put(CONNECTION_LIVE_SECONDS_ATTRIBUTE_NAME, connectionLiveSeconds);
            return handler.handle(session);
        };

        return super.handleRequest(exchange, webSocketHandlerDecorator);
    }

    private void setAuthenticateFailedInfoToResponse(Exception e, ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().put("Content-Type", RESPONSE_CONTENT_TYPE);

        Mono<DataBuffer> responseBody = Mono.just(
                "{\"error_code\": \"UNAUTHORIZED\", \"error_message\": \"사용자 인증에 실패했습니다.\"}")
            .map(content -> response.bufferFactory().wrap(content.getBytes(StandardCharsets.UTF_8)));

        response.writeWith(responseBody).subscribe();
    }

    public long getConnectionLiveSeconds(HttpHeaders httpHeaders) {
        List<String> values = httpHeaders.get(CONNECTION_LIVE_SECONDS_HEADER_NAME);
        if (values == null || values.isEmpty()) {
            return DEFAULT_CONNECTION_LIVE_SECONDS;
        }

        return Long.parseLong(values.get(0));
    }
}
