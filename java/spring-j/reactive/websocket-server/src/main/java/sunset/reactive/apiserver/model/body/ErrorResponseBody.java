package sunset.reactive.apiserver.model.body;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import lombok.Builder;
import lombok.Data;
import sunset.reactive.apiserver.error.ErrorResponseCode;

@Builder
@Data
public class ErrorResponseBody {

    private ErrorResponseCode errorCode;
    private String errorMessage;
    private String encodedRequest;

    public String serialize() {
        // objectMapper 를 사용하면, Object -> jsonString 할 때 또 JsonProcessingException 을 처리해야함.
        return String.format(
            "{\"error_code\": \"%s\", "
                + "\"error_message\": \"%s\", "
                + "\"encoded_request\": \"%s\"}",
            errorCode, errorMessage, encodedRequest);
    }

    public static ErrorResponseBody of(ErrorResponseCode errorCode, String errorMessage, String requestText) {
        String encodedRequest;
        try {
            encodedRequest = URLEncoder.encode(requestText, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            encodedRequest = "";
        }

        return ErrorResponseBody.builder()
            .errorCode(errorCode)
            .errorMessage(errorMessage)
            .encodedRequest(encodedRequest)
            .build();
    }
}
