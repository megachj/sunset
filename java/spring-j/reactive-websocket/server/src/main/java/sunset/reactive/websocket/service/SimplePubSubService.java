package sunset.reactive.websocket.service;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.FluxSink.OverflowStrategy;

@Slf4j
@RequiredArgsConstructor
@Service
public class SimplePubSubService implements PubSubService {

    private Channel channel;
    private ConnectableFlux<String> hotSource;

    @PostConstruct
    public void init() {
        channel = Channel.connectNewChannel();

        hotSource = Flux.create((FluxSink<String> sink) -> {
                channel.setListener(
                    new ChannelListener<>() {
                        @Override
                        public void onMessage(String message) {
                            sink.next(message); // Subscriber의 요청에 상관없이 신호 발생
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
    public void sendMessage(String message) {
        channel.publish(message);
    }

    @Override
    public Flux<String> listen() {
        return hotSource;
    }

    public static class Channel {

        private List<ChannelListener<String>> listeners = new ArrayList<>();

        public void setListener(ChannelListener<String> listener) {
            listeners.add(listener);
        }

        public void publish(String message) {
            listeners.forEach(l -> {
                l.onMessage(message);
            });
        }

        public static Channel connectNewChannel() {
            return new Channel();
        }
    }

    public interface ChannelListener<T> {

        void onMessage(T message);

        void complete();
    }
}
