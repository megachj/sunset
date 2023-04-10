package sunset.java.regex.templateuri;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TemplateUriRegExServiceTest {

    private static final String TEST_TEMPLATE_URI = "/aa/{aId}/bb/{bId}";

    @ParameterizedTest
    @CsvSource({
        "/aa/{aId}/bb",
        "/aa/{aId}/bb/{bId}",
        "/a_a/{aId}/b-b/{bId}",
    })
    public void isTemplateUri_파라미터_성공_테스트(String templateUri) {
        // when
        boolean actual = TemplateUriRegExService.isTemplateUri(templateUri);

        // then
        Assertions.assertTrue(actual);
    }

    @ParameterizedTest
    @CsvSource({
        "/",
        "/aa",
        "/aa/bb",
        "/aa/**/bb/**",
    })
    public void isTemplateUri_파라미터_실_테스트(String templateUri) {
        // when
        boolean actual = TemplateUriRegExService.isTemplateUri(templateUri);

        // then
        Assertions.assertFalse(actual);
    }

    @Test
    public void isTemplateUri_기타_실패_테스트() {
        // when
        boolean nullActual = TemplateUriRegExService.isTemplateUri(null);
        boolean emptyStringActual = TemplateUriRegExService.isTemplateUri("");

        // then
        Assertions.assertFalse(nullActual);
        Assertions.assertFalse(emptyStringActual);
    }

    // /aa/{aId}/bb/{bId} 에 대한 검증
    @ParameterizedTest
    @CsvSource({
        "/aa/123/bb/123",
        "/aa/aa/bb/bb",
        "/aa/hi/bb/hi",
        "/aa/hi123/bb/hi123",
        "/aa/df-/bb/df_",
        "/aa/-/bb/_",
    })
    public void convertTemplateUriToRegEx_파라미터_성공_테스트(String actualUri) {
        // given
        Pattern regexUriPattern = TemplateUriRegExService.convertTemplateUriToRegEx(TEST_TEMPLATE_URI);

        // when
        boolean actual = TemplateUriRegExService.isMatchedUri(actualUri, regexUriPattern);

        // then
        Assertions.assertTrue(actual);
    }

    // /aa/{aId}/bb/{bId} 에 대한 검증
    @ParameterizedTest
    @CsvSource({
        "/",
        "/aa/123",
        "/aa/123/bb",
        "/aa1/aa/bb/bb",
    })
    public void convertTemplateUriToRegEx_파라미터_실패_테스트(String actualUri) {
        // given
        Pattern regexUriPattern = TemplateUriRegExService.convertTemplateUriToRegEx(TEST_TEMPLATE_URI);

        // when
        boolean actual = TemplateUriRegExService.isMatchedUri(actualUri, regexUriPattern);

        // then
        Assertions.assertFalse(actual);
    }

    @Test
    public void convertTemplateUriToRegEx_기타_실패_테스트() {
        // given
        Pattern regexUriPattern = TemplateUriRegExService.convertTemplateUriToRegEx(TEST_TEMPLATE_URI);

        // when & then
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            TemplateUriRegExService.isMatchedUri(null, regexUriPattern);
        });

        // when & then
        Assertions.assertFalse(TemplateUriRegExService.isMatchedUri("", regexUriPattern));
    }
}
