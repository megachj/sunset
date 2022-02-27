package sunset.java.basic;

import lombok.Builder;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

public class TypeTest {

    @Test
    public void 기본형참조타입_테스트() {
        // 기본형의 참조타입은 null 이 가능하다.
        Boolean isValid = null;
        Assertions.assertNull(isValid);
    }

    @Test
    public void Optional_테스트() {
        User user1 = User.builder()
                .name("유저1")
                .build();

        Assertions.assertTrue(user1.getHasJoined().isEmpty());
        Assertions.assertThrows(NoSuchElementException.class, () -> user1.getHasJoined().get());

        user1.setHasJoined(Optional.of(Boolean.FALSE));
        Assertions.assertTrue(user1.getHasJoined().isPresent());
        Assertions.assertEquals(false, user1.getHasJoined().get());

        user1.setHasJoined(Optional.of(Boolean.TRUE));
        Assertions.assertTrue(user1.getHasJoined().isPresent());
        Assertions.assertEquals(true, user1.getHasJoined().get());

        user1.setHasJoined(Optional.ofNullable(null));
        Assertions.assertTrue(user1.getHasJoined().isEmpty());

        user1.setHasJoined(null);
        Assertions.assertNull(user1.getHasJoined()); // Optional 객체도 null 이 가능
        Assertions.assertThrows(NullPointerException.class, () -> user1.getHasJoined().get());
    }

    @Data
    @Builder
    static class User {
        private String name;
        @Builder.Default // 빌더 기본값
        private Optional<Boolean> hasJoined = Optional.empty();
    }
}
