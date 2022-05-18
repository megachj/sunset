package sunset.reactive.websocketserver.pubsub;

import java.util.Locale;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.FluxSink.OverflowStrategy;
import sunset.reactive.websocketserver.channel.Channel;
import sunset.reactive.websocketserver.channel.ChannelListener;
import sunset.reactive.websocketserver.model.ChatMessage;

@Slf4j
@RequiredArgsConstructor
@Service
public class SimpleChatMessagePubSubService implements PubSubService<ChatMessage> {

    private Channel<ChatMessage> channel;
    private ConnectableFlux<ChatMessage> receivedMessageHotSource;

    @PostConstruct
    public void init() {
        channel = Channel.connectNewChannel();

        receivedMessageHotSource = Flux.create((FluxSink<ChatMessage> sink) -> {
                channel.setListener(
                    new ChannelListener<>() {
                        @Override
                        public void onData(ChatMessage chatMessage) {
                            sink.next(chatMessage);
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
        receivedMessageHotSource.connect();
    }

    @Override
    public void sendMessage(ChatMessage chatMessage) {
        channel.publish(chatMessage);
    }

    @Override
    public Flux<ChatMessage> listen(String userId) {
        return receivedMessageHotSource
            .filter(chatMessage ->
                "all".equals(chatMessage.getToUserId().toLowerCase(Locale.ROOT)) ||
                    userId.equals(chatMessage.getToUserId()) ||
                    userId.equals(chatMessage.getFromUserId())
            );
    }
}
