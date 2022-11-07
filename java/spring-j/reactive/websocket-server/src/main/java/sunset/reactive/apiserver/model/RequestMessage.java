package sunset.reactive.apiserver.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sunset.reactive.apiserver.error.ErrorResponseCode;
import sunset.reactive.apiserver.model.body.UserScoreRequestV1Body;
import sunset.reactive.apiserver.model.exception.MessageProcessException;

@Slf4j
@Data
@Builder
public class RequestMessage<T> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Long payAccountId;

    private RequestCommand command;
    private T bodyObject;

    public static RequestMessage<?> deserialize(Long payAccountId, String requestText) {
        try {
            String[] split = requestText.split("\n\n", 2);

            RequestCommand command = RequestCommand.valueOf(split[0]);
            Object bodyObject = objectMapper.readValue(split[1], command.getBodyClass());

            return RequestMessage.builder()
                .payAccountId(payAccountId)
                .command(command)
                .bodyObject(bodyObject)
                .build();
        } catch (Exception e) {
            log.error("RequestMessage#deserialize exception, payAccountId: {}, message: {}", payAccountId,
                requestText.replaceAll("\n", " "), e);
            throw new MessageProcessException(e, "잘못된 요청 메시지입니다.", ErrorResponseCode.CLIENT_BAD_REQUEST);
        }
    }

    @RequiredArgsConstructor
    @Getter
    public enum RequestCommand {
        USER_SCORE_REQUEST_V1(UserScoreRequestV1Body.class),
        ;

        private final Class<?> bodyClass;
    }
}
