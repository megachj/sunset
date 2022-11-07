package sunset.reactive.apiserver.error;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ErrorResponseCode {
    CLIENT_BAD_REQUEST("클라이언트 요청 오류"),
    SERVER_INTERNAL_ERROR("서버 내부 에러 발생"),
    ;

    private final String description;
}
