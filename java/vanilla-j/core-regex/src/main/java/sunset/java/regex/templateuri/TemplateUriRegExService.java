package sunset.java.regex.templateuri;

/**
 * templateUri 란? pathVariable 이 '{xxx...}' 형태로 들어간 URI 를 지칭한다.<br>
 * 그리고 템플릿 부분에 들어오는 실제값은 영문자, 숫자, -, _ 로 한정한다.<br>
 * ex) /users/{userId}/bank-accounts/{bankAccountId}
 */
public class TemplateUriRegExService {

    /**
     * 템플릿이 포함된 URI 인지를 확인한다.
     *
     * @param templateUri
     * @return
     */
    public static boolean isTemplateUri(String templateUri) {

    }

    /**
     * templateUri 를 regEx 로 변환한다.
     *
     * @param templateUri
     * @return
     */
    public static String convertTemplateUriToRegEx(String templateUri) {

    }
}
