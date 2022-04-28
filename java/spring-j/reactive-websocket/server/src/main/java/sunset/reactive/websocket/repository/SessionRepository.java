package sunset.reactive.websocket.repository;

import java.util.Collection;
import java.util.Optional;
import org.springframework.web.reactive.socket.WebSocketSession;

public interface SessionRepository {

    void addSession(String userId, WebSocketSession session);
    void removeSession(String userId, WebSocketSession session);
    Optional<WebSocketSession> getSession(String userId, String sessionId);
    Collection<WebSocketSession> getAllSession();
}
