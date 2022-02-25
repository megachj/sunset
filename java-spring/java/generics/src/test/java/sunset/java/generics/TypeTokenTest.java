package sunset.java.generics;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class TypeTokenTest {

    @Test
    public void superTypeToken() {
        Sub sub = new Sub();

        System.out.println(((ParameterizedType) sub.getClass().getGenericSuperclass())
            .getActualTypeArguments()[0]
            .getTypeName());

        try {
            TypeReference intType = new TypeReference<Integer>();

            System.out.println(((ParameterizedType) intType.getClass().getGenericSuperclass())
                .getActualTypeArguments()[0]
                .getTypeName());
        } catch (ClassCastException e) {
            System.out.println(e.getMessage());
        }

        AbstractTypeReference intType = new AbstractTypeReference<Integer>() {
        };
        System.out.println(((ParameterizedType) intType.getClass().getGenericSuperclass())
            .getActualTypeArguments()[0]
            .getTypeName());

        AbstractTypeReference listType = new AbstractTypeReference<List<Integer>>() {
        };
        AbstractTypeReference setType = new AbstractTypeReference<Set<Map<String, List<Integer>>>>() {
        };

        System.out.println(((ParameterizedType) listType.getClass().getGenericSuperclass())
            .getActualTypeArguments()[0]
            .getTypeName());

        System.out.println(((ParameterizedType) setType.getClass().getGenericSuperclass())
            .getActualTypeArguments()[0]
            .getTypeName());
    }

    class TypeReference<T> {

    }

    class Sub extends TypeReference<String> {

    }

    abstract class AbstractTypeReference<T> {

    }
}
