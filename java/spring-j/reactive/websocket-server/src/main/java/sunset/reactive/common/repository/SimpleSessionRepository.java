package sunset.reactive.common.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.socket.WebSocketSession;

@Slf4j
@RequiredArgsConstructor
@Repository
public class SimpleSessionRepository implements SessionRepository {

    // TODO: 스레드 안전 자료구조를 사용해야 하나?
    private final Map<String, Map<String, WebSocketSession>> sessionMap = new HashMap<>();

    @Override
    public void addSession(String userId, WebSocketSession session) {
        if (sessionMap.keySet().contains(userId)) {
            sessionMap.get(userId)
                .putIfAbsent(session.getId(), session);
        } else {
            Map<String, WebSocketSession> map = new HashMap<>(2);
            map.put(session.getId(), session);
            sessionMap.put(userId, map);
        }
    }

    @Override
    public void removeSession(String userId, WebSocketSession session) {
        if (!sessionMap.keySet().contains(userId)) {
            return;
        }

        Map<String, WebSocketSession> map = sessionMap.get(userId);
        map.remove(session.getId());
        if (map.isEmpty()) {
            sessionMap.remove(userId);
        }
    }

    @Override
    public Optional<WebSocketSession> getSession(String userId, String sessionId) {
        return Optional.ofNullable(sessionMap.get(userId))
            .map(map -> map.get(sessionId));
    }

    @Override
    public Collection<WebSocketSession> getAllSession() {
        return sessionMap.values()
            .stream()
            .flatMap(map -> map.values().stream())
            .collect(Collectors.toList());
    }
}
