package sunset.reactive.websocket.pubsub;

import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.FluxSink.OverflowStrategy;
import sunset.reactive.websocket.channel.Channel;
import sunset.reactive.websocket.channel.ChannelListener;
import sunset.reactive.websocket.model.ChatMessage;

@Slf4j
@RequiredArgsConstructor
@Service
public class SimpleChatMessagePubSubService implements PubSubService<ChatMessage> {

    private Channel<ChatMessage> channel;
    private ConnectableFlux<ChatMessage> hotSource;

    @PostConstruct
    public void init() {
        channel = Channel.connectNewChannel();

        hotSource = Flux.create((FluxSink<ChatMessage> sink) -> {
                channel.setListener(
                    new ChannelListener<>() {
                        @Override
                        public void onData(ChatMessage chatMessage) {
                            sink.next(chatMessage); // Subscriber의 요청에 상관없이 신호 발생
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
    public void sendMessage(ChatMessage chatMessage) {
        channel.publish(chatMessage);
    }

    @Override
    public Flux<ChatMessage> listen(String userId) {
        return hotSource
            .filter(chatMessage ->
                userId.equals(chatMessage.getToUserId()) || userId.equals(chatMessage.getFromUserId())
            );
    }
}
