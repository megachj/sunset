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

        DataPump pump = new DataPump();

        ConnectableFlux<String> hotSource = Flux.create((FluxSink<String> sink) -> {
                pump.setListener(
                    new DataListener<>() {
                        @Override
                        public void onData(List<String> chunk) {
                            chunk.forEach(s -> {
                                sink.next(s); // Subscriber의 요청에 상관없이 신호 발생
                            });
                        }

                        @Override
                        public void complete() {
                            log.info("complete");
                            sink.complete();
                        }
                    }
                );
            }, OverflowStrategy.IGNORE)
            .log("after create")
            .publish();

        hotSource.connect();

        pump.emit(List.of("이말년", "주호민"));

        // subscriber1: 침착맨, 쭈펄, 쭈거니, 병거니
        Disposable subscriber1 = hotSource
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

        pump.emit(List.of("침착맨", "쭈펄"));

        // subscriber2: 쭈거니, 병거니, 쏘영
        Disposable subscriber2 = hotSource
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

        pump.emit(List.of("쭈거니", "병거니"));

        subscriber1.dispose();

        pump.emit(List.of("쏘영"));
    }

    public static class DataPump {

        private List<DataListener<String>> listeners = new ArrayList<>();

        public void setListener(DataListener<String> listener) {
            listeners.add(listener);
        }

        public void emit(List<String> inputData) {
            listeners.forEach(l -> {
                l.onData(inputData);
            });
        }
    }

    public interface DataListener<T> {

        void onData(List<T> chunk);

        void complete();
    }
}
