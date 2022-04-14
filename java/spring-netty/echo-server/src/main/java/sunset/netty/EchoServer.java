package sunset.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.net.InetSocketAddress;

public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: " + EchoServer.class.getSimpleName() + " <port>");
        }
        int port = Integer.parseInt(args[0]);
        new EchoServer(port).start();
    }

    public void start() throws  Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup(); // 1. EventLoopGroup 생성
        try {
            ServerBootstrap b = new ServerBootstrap(); // 2. ServerBootstrap 생성
            b.group(group)
                .channel(NioServerSocketChannel.class) // 3. NIO 전송 채널 지정
                .localAddress(new InetSocketAddress(port)) // 4. 지정된 포트로 소켓 주소 설정
                .childHandler(new ChannelInitializer<SocketChannel>() { // 5. EchoServerHandler 를 채널 파이프라인으로 추가
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(serverHandler);
                    }
                });
            ChannelFuture f = b.bind().sync(); // 6. 서버를 비동기식으로 바인딩, sync() 는 바인딩 될때까지 대기
            f.channel().closeFuture().sync(); // 7. 채널의 CloseFuture 를 얻고 완료될 때까지 현재 스레드 블로킹
        } finally {
            group.shutdownGracefully().sync(); // 8. EventLoopGroup 종료하고 모든 리소스 해제
        }
    }
}
