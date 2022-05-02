package sunset.reactive.websocketserver.pubsub;

import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.FluxSink.OverflowStrategy;
import sunset.reactive.externalrestserver.UserNicknameInfo;
import sunset.reactive.websocketserver.channel.Channel;
import sunset.reactive.websocketserver.channel.ChannelListener;

@Slf4j
@RequiredArgsConstructor
@Service
public class AsyncUserNicknamePubSubService implements PubSubService<UserNicknameInfo> {

    private Channel<UserNicknameInfo> channel;
    private ConnectableFlux<UserNicknameInfo> hotSource;

    @PostConstruct
    public void init() {
        channel = Channel.connectNewChannel();

        hotSource = Flux.create((FluxSink<UserNicknameInfo> sink) -> {
                channel.setListener(
                    new ChannelListener<>() {
                        @Override
                        public void onData(UserNicknameInfo userNicknameInfo) {
                            sink.next(userNicknameInfo); // Subscriber의 요청에 상관없이 신호 발생
                        }

                        @Override
                        public void complete() {
                            log.info("complete");
                            sink.complete();
                        }
                    }
                );
            }, OverflowStrategy.IGNORE)
            .publish();
        hotSource.connect();
    }

    @Override
    public void sendMessage(UserNicknameInfo data) {
        channel.publish(data);
    }

    @Override
    public Flux<UserNicknameInfo> listen(String userId) {
        return hotSource
            .filter(userNicknameInfo -> userId.equals(userNicknameInfo.getUserId()));
    }
}
