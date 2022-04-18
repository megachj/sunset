package sunset.reactive.websocket;

import java.net.URI;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

public class WebSocketClientApplication {

    public static void main(String[] args) throws InterruptedException {
        WebSocketClient client = new ReactorNettyWebSocketClient();
        client.execute(
                URI.create("ws://localhost:8080/ws/echo"),
                session -> session.send(
                        Mono.just(session.textMessage("event-spring-reactive-client-websocket")))
                    .thenMany(session.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .log())
                    .then())
            .block();
    }
}
