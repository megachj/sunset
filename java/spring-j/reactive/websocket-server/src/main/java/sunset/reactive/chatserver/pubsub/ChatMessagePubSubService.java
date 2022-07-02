package sunset.reactive.chatserver.pubsub;

import java.util.Locale;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.FluxSink.OverflowStrategy;
import sunset.reactive.common.channel.Channel;
import sunset.reactive.chatserver.model.ChatMessage;
import sunset.reactive.common.pubsub.PubSubService;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatMessagePubSubService implements PubSubService<ChatMessage> {

    private Channel<ChatMessage> channel;
    private ConnectableFlux<ChatMessage> hotSourcePublisher;

    @PostConstruct
    public void init() {
        channel = Channel.connectNewChannel();

        hotSourcePublisher = Flux.create((FluxSink<ChatMessage> sink) ->
                channel.setListener(chatMessage -> sink.next(chatMessage)),
                OverflowStrategy.IGNORE)
            .publish();
        hotSourcePublisher.connect();
    }

    @Override
    public void sendMessage(ChatMessage chatMessage) {
        channel.publish(chatMessage);
    }

    @Override
    public Flux<ChatMessage> listen(String userId) {
        return hotSourcePublisher
            .filter(chatMessage -> userId.equals(chatMessage.getToUserId()));
    }
}
