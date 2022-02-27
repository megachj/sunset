package sunset.netty.config;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Value
@Setter
@ConfigurationProperties(prefix = "sunset.netty")
public class NettyProperties {

    private Properties tcpServer;
    private Properties websocketServer;
    private Properties httpServer;

    @Getter
    @Setter
    public static class Properties {
        private int port;
        private int bossCount;
        private int workerCount;
        private boolean keepAlive;
        private int backlog;
    }
}
