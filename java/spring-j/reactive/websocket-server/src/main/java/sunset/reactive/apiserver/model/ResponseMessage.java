package sunset.reactive.apiserver.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import sunset.reactive.apiserver.error.ErrorResponseCode;
import sunset.reactive.apiserver.model.exception.MessageProcessException;

@Slf4j
@Builder
@Value
public class ResponseMessage {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private String userId;

    private ResponseCommand command;
    private String bodyJsonString;

    public String serialize() {
        return String.format("%s\n\n%s", command.name(), bodyJsonString);
    }

    public static <T> ResponseMessage of(String userId, ResponseCommand responseCommand, T responseBody) {
        try {
            return ResponseMessage.builder()
                .userId(userId)
                .command(responseCommand)
                .bodyJsonString(objectMapper.writeValueAsString(responseBody))
                .build();
        } catch (JsonProcessingException e) {
            log.error(
                "ResponseMessage#of JsonProcessingException, responseCommand: {}, responseBody: {}",
                responseCommand, responseBody, e
            );
            throw new MessageProcessException(e, "응답 메시지를 변환하는 중에 서버 에러 발생",
                ErrorResponseCode.SERVER_INTERNAL_ERROR);
        }
    }

    @Getter
    public enum ResponseCommand {
        ERROR_RESPONSE,
        USER_SCORE_RESPONSE_V1,
        ;
    }
}
