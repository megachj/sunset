package sunset.reactive.common.websocketconfig.handshake;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SessionAuthService {

    private static final String AUTH_HEADER_NAME = "auth";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public AuthUser authenticate(ServerHttpRequest request) {
        Optional<String> authHeader = getAuthFromHeader(request);
        if (authHeader.isEmpty()) {
            throw new IllegalArgumentException("인증 헤더가 존재하지 않습니다.");
        }

        try {
            return objectMapper.readValue(authHeader.get(), AuthUser.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<String> getAuthFromHeader(ServerHttpRequest request) {
        if (!request.getHeaders().containsKey(AUTH_HEADER_NAME)) {
            return Optional.empty();
        }

        return Optional.ofNullable(request.getHeaders().get(AUTH_HEADER_NAME).get(0));
    }
}
