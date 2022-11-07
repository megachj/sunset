package sunset.reactive.apiserver;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink.OverflowStrategy;
import reactor.core.publisher.Mono;
import sunset.reactive.apiserver.dispatcher.RequestMessageDispatcher;
import sunset.reactive.apiserver.error.ErrorSignal;
import sunset.reactive.apiserver.error.ErrorSignalHandler;
import sunset.reactive.apiserver.model.ResponseMessage;
import sunset.reactive.common.pattern.observer.Observable;
import sunset.reactive.common.pattern.observer.SingleObservable;

@Slf4j
@RequiredArgsConstructor
@Component
public class RequestProcessor {

    private final RequestMessageDispatcher requestMessageDispatcher;

    public ProcessedResponse process(Long payAccountId, Flux<String> requestTextSource) {
        Observable<ErrorSignal> errorSignalObservable = SingleObservable.newObservable();
        Flux<ResponseMessage> errorResponseSource = Flux.<ErrorSignal>create(
                emitter -> errorSignalObservable.add(emitter::next),
                OverflowStrategy.DROP
            )
            .map(ErrorSignalHandler::handle);

        Flux<ResponseMessage> successResponseSource = requestTextSource
            .flatMap(
                requestText -> requestMessageDispatcher.doDispatch(payAccountId, requestText)
                    .doOnError(e -> errorSignalObservable.notifyObservers(ErrorSignal.of(e, requestText)))
                    .onErrorResume(e -> Mono.empty())
            );

        return ProcessedResponse.of(successResponseSource, errorResponseSource);
    }

    @Value(staticConstructor = "of")
    public static class ProcessedResponse {

        Flux<ResponseMessage> successResponseSource;
        Flux<ResponseMessage> errorResponseSource;
    }
}
