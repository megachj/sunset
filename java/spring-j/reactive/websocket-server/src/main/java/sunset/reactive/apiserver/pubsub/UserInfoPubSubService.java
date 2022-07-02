package sunset.reactive.apiserver.pubsub;

import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.FluxSink.OverflowStrategy;
import sunset.reactive.common.pubsub.PubSubService;
import sunset.reactive.remoteserver.UserInfo;
import sunset.reactive.common.channel.Channel;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserInfoPubSubService implements PubSubService<UserInfo> {

    private Channel<UserInfo> channel;
    private ConnectableFlux<UserInfo> hotSourcePublisher;

    @PostConstruct
    public void init() {
        channel = Channel.connectNewChannel();

        hotSourcePublisher = Flux.create((FluxSink<UserInfo> sink) ->
                channel.setListener(sink::next), OverflowStrategy.IGNORE)
            .publish();
        hotSourcePublisher.connect();
    }

    @Override
    public void sendMessage(UserInfo data) {
        channel.publish(data);
    }

    @Override
    public Flux<UserInfo> listen(String userId) {
        return hotSourcePublisher
            .filter(userNicknameInfo -> userId.equals(userNicknameInfo.getUserId()));
    }
}
