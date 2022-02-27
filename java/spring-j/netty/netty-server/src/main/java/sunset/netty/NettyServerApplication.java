package sunset.netty;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import sunset.netty.tcpserver.TCPServer;

@RequiredArgsConstructor
@SpringBootApplication
public class NettyServerApplication {

    private final TCPServer tcpServer;

    public static void main(String[] args) {
        SpringApplication.run(NettyServerApplication.class, args);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> readyEventApplicationListener() {
        return event -> {
            tcpServer.start();
            // TODO: websocketServer, httpServer 추가
        };
    }
}
