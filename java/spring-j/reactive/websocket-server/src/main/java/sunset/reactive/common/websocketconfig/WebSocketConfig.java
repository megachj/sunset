package sunset.reactive.common.websocketconfig;

import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerAdapter;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import sunset.reactive.common.websocketconfig.handshake.HandshakeWebSocketMainService;
import sunset.reactive.apiserver.ApiWebSocketHandler;
import sunset.reactive.chatserver.ChatWebSocketHandler;

@Configuration
@EnableWebFlux
public class WebSocketConfig {

    @Bean
    public HandlerMapping handlerMapping(ChatWebSocketHandler chatWebSocketHandler,
        ApiWebSocketHandler apiWebSocketHandler
    ) {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(
            Map.of(
                "/chat/open", chatWebSocketHandler,
                "/api/open", apiWebSocketHandler
            )
        );
        mapping.setOrder(0);
        return mapping;
    }

    @Bean
    public HandlerAdapter handlerAdapter(HandshakeWebSocketMainService handshakeWebSocketMainService) {
        return new WebSocketHandlerAdapter(handshakeWebSocketMainService);
    }

    @Bean
    public RequestUpgradeStrategy requestUpgradeStrategy() {
        return new ReactorNettyRequestUpgradeStrategy();
    }
}
