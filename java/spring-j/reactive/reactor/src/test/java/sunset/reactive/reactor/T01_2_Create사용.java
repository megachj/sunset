package sunset.reactive.reactor;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.Disposable;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.FluxSink.OverflowStrategy;

@Slf4j
public class T01_2_Create사용 {

    @Test
    public void create사용_및_hot스트림사용_해서_PubSub구현() {

        Channel<String> channel = new Channel<>();
        ConnectableFlux<String> dataPublisher = Flux.create((FluxSink<String> sink) ->
                channel.setListener(sink::next), OverflowStrategy.IGNORE
            )
            .log("after create")
            .publish();

        dataPublisher.connect(); // cold source -> hot source

        channel.publish("이말년");
        channel.publish("주호민");

        // subscriber1: 침착맨, 쭈펄, 쭈거니, 병거니
        Disposable subscriber1 = dataPublisher
            .doOnSubscribe(subscription -> {
                log.info("subscriber1: onSubscribe");
            })
            .doOnComplete(() -> {
                log.info("subscriber1: onComplete");
            })
            .doOnNext(next -> {
                log.info("subscriber1: onNext[{}]", next);
            })
            .subscribe();

        channel.publish("침착맨");
        channel.publish("쭈펄");

        // subscriber2: 쭈거니, 병거니, 쏘영
        Disposable subscriber2 = dataPublisher
            .doOnSubscribe(subscription -> {
                log.info("subscriber2: onSubscribe");
            })
            .doOnComplete(() -> {
                log.info("subscriber2: onComplete");
            })
            .doOnNext(next -> {
                log.info("subscriber2: onNext[{}]", next);
            })
            .subscribe();

        channel.publish("쭈거니");
        channel.publish("병거니");

        subscriber1.dispose();

        channel.publish("쏘영");
    }

    public static class Channel<T> {

        private List<ChannelListener<T>> listeners = new ArrayList<>();

        public void setListener(ChannelListener<T> listener) {
            listeners.add(listener);
        }

        public void publish(T data) {
            listeners.forEach(l -> {
                l.onData(data);
            });
        }
    }

    public interface ChannelListener<T> {

        void onData(T chunk);

        // void complete();
    }
}
