package sunset.reactive.websocket;

import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerAdapter;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

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
    public HandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter(chatHandshakeWebSocketService());
    }

    @Bean
    public WebSocketService chatHandshakeWebSocketService() {
        return new ChatHandshakeWebSocketService(new ReactorNettyRequestUpgradeStrategy());
    }

    @Bean
    public Scheduler wsConnTimer() {
        // TODO: 스케줄러 최적화
        return Schedulers.newParallel("wsConnTimer");
    }
}
