package sunset.reactive.apiserver.model.exception;

import lombok.Getter;
import sunset.reactive.apiserver.error.ErrorResponseCode;

@Getter
public class MessageProcessException extends RuntimeException {

    private ErrorResponseCode errorResponseCode;

    public MessageProcessException(Throwable cause, String message, ErrorResponseCode errorResponseCode) {
        super(message, cause);
        this.errorResponseCode = errorResponseCode;
    }

    public MessageProcessException(String message, ErrorResponseCode errorResponseCode) {
        super(message);
        this.errorResponseCode = errorResponseCode;
    }
}
