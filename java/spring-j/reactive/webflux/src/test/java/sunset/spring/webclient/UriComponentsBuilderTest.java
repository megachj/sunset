package sunset.spring.webclient;

import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class UriComponentsBuilderTest {

    @Test
    public void UriComponentsBuilder_테스트() {
        // case1) path, query parameter, path parameter
        UriComponents uriComponents = UriComponentsBuilder.newInstance().path("/users/{user_id}")
            .queryParam("name", "아무개") // query parameter
            .buildAndExpand(Map.of("user_id", "1")); // path parameter

        Assertions.assertThat(uriComponents.toUriString())
            .isEqualTo("/users/1?name=아무개");

        // case2) full
        UriComponents fullUriComponents = UriComponentsBuilder.newInstance().scheme("https").host("localhost")
            .port("8080").path("/users/{user_id}/name/{name}")
            .queryParam("age", "30")
            .queryParam("gender", "male")
            .buildAndExpand("1", "아무개");

        Assertions.assertThat(fullUriComponents.toUriString())
            .isEqualTo("https://localhost:8080/users/1/name/아무개?age=30&gender=male");
    }
}
