package sunset.java.regex.templateuri;

import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * templateUri 란? pathVariable 이 '{xxx...}' 형태로 들어간 URI 를 지칭한다.<br>
 * 그리고 템플릿 부분에 들어오는 실제값은 영문자, 숫자, -, _ 로 한정한다.<br>
 * ex) /users/{userId}/bank-accounts/{bankAccountId}
 */
public class TemplateUriRegExService {

    private static final String TEMPLATE_PART = "\\{(\\w|-|_)+\\}";
    private static final Pattern TEMPLATE_CONTAINS_PATTERN = Pattern.compile("(.*)" + TEMPLATE_PART + "(.*)");
    private static final Pattern TEMPLATE_EQUALS_PATTERN = Pattern.compile("^" + TEMPLATE_PART + "$");

    private static final String REGEX_PART = "(\\w|-|_)+";

    /**
     * 템플릿이 포함된 URI 인지를 확인한다.
     *
     * @param templateUri
     * @return
     */
    public static boolean isTemplateUri(String templateUri) {
        if (templateUri == null) {
            return false;
        }

        return TEMPLATE_CONTAINS_PATTERN.matcher(templateUri).matches();
    }

    /**
     * templateUri 를 regEx 로 변환한다.
     *
     * @param templateUri
     * @return
     */
    public static Pattern convertTemplateUriToRegEx(String templateUri) {
        if (templateUri == null) {
            throw new IllegalArgumentException();
        }

        String regEx = Stream.of(templateUri.strip().split("/"))
            .map(part -> {
                if (TEMPLATE_EQUALS_PATTERN.matcher(part).matches()) {
                    return REGEX_PART;
                }
                return part;
            })
            .collect(Collectors.joining("/", "^", "$"));

        return Pattern.compile(regEx);
    }

    public static boolean isMatchedUri(String actualUri, Pattern pattern) {
        if (actualUri == null || pattern == null) {
            throw new IllegalArgumentException();
        }

        return pattern.matcher(actualUri).matches();
    }
}
