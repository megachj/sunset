package sunset.reactive.websocketserver.websocketconfig;

import java.util.Collections;
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
import sunset.reactive.websocketserver.websocketconfig.handshake.HandshakeWebSocketMainService;

@Configuration
@EnableWebFlux
public class WebSocketConfiguration {

    @Bean
    public HandlerMapping handlerMapping(ChatWebSocketHandler chatWebSocketHandler) {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(Collections.singletonMap("/chat/open", chatWebSocketHandler));
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

    @Bean
    public Scheduler wsConnTimer() {
        // TODO: 스케줄러 최적화
        return Schedulers.newParallel("wsConnTimer");
    }
}
