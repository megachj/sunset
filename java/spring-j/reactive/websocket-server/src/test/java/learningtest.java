import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class learningtest {

    @Test
    public void test() {
        String message1 = "COMMAND\nrequest1\nbody1";
        String message2 = "COMMAND\n\nbody2";

        String[] split1 = message1.split("\n");
        String[] split2 = message2.split("\n");

        for (int i = 0; i < split1.length; ++i) {
            log.info("split1[{}]: [{}]", i, split1[i]);
        }
        for (int i = 0; i < split2.length; ++i) {
            log.info("split2[{}]: [{}]", i, split2[i]);
        }
    }
}
