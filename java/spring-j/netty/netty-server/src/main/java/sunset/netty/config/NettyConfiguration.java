package sunset.netty.config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sunset.netty.tcpserver.SimpleChatChannelInitializer;

import java.net.InetSocketAddress;

@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties(NettyProperties.class)
public class NettyConfiguration {

    private final NettyProperties nettyProperties;

    @Bean
    public ServerBootstrap tcpServerBootstrap(SimpleChatChannelInitializer simpleChatChannelInitializer) {
        ServerBootstrap b = new ServerBootstrap();
        b.group(tcpBossGroup(), tcpWorkerGroup())
            .channel(NioServerSocketChannel.class)
            .handler(new LoggingHandler(LogLevel.DEBUG))
            .childHandler(simpleChatChannelInitializer);
        b.option(ChannelOption.SO_BACKLOG, nettyProperties.getTcpServer().getBacklog());
        return b;
    }

    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup tcpBossGroup() {
        return new NioEventLoopGroup(nettyProperties.getTcpServer().getBossCount());
    }

    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup tcpWorkerGroup() {
        return new NioEventLoopGroup(nettyProperties.getTcpServer().getWorkerCount());
    }

    @Bean
    public InetSocketAddress tcpSocketAddress() {
        return new InetSocketAddress(nettyProperties.getTcpServer().getPort());
    }
}
