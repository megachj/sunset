package sunset.reactive.apiserver.error;

import java.util.List;
import java.util.function.Function;
import org.springframework.data.util.Pair;
import sunset.reactive.apiserver.model.ResponseMessage;
import sunset.reactive.apiserver.model.ResponseMessage.ResponseCommand;
import sunset.reactive.apiserver.model.body.ErrorResponseBody;
import sunset.reactive.apiserver.model.exception.MessageProcessException;

public class ErrorSignalHandler {

    // 처리하고 싶은 에러를 추가
    private static final List<Pair<Class<? extends Throwable>, Function<Throwable, ErrorResponseCode>>> DEFINED_EXCEPTION = List.of(
        Pair.of(MessageProcessException.class, (e -> ((MessageProcessException) e).getErrorResponseCode()))
    );

    public static ResponseMessage handle(ErrorSignal errorSignal) {
        ErrorResponseBody errorResponseBody = getErrorResponseBody(errorSignal);

        return ResponseMessage.builder()
            .command(ResponseCommand.ERROR_RESPONSE)
            .bodyJsonString(errorResponseBody.serialize())
            .build();
    }

    private static ErrorResponseBody getErrorResponseBody(ErrorSignal errorSignal) {
        ErrorResponseCode errorResponseCode = DEFINED_EXCEPTION.stream()
            .filter(defined -> defined.getFirst().isInstance(errorSignal.getThrowable()))
            .findFirst()
            .map(pair -> pair.getSecond().apply(errorSignal.getThrowable()))
            .orElse(ErrorResponseCode.SERVER_INTERNAL_ERROR);

        return ErrorResponseBody.of(errorResponseCode, errorSignal.getThrowable().getMessage(),
            errorSignal.getRequestText());
    }
}
