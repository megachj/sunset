package sunset.spring.core;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class MyServiceSpecTest {

    @Autowired
    private MyServiceSpec myService;
    @Autowired
    private MyServiceDecorated myServiceDecorated;

    @Autowired
    @Qualifier("myService")
    private MyServiceSpec myService2;
    @Autowired
    private MyService myServiceImpl;

    @Test
    public void primary우선순위_를갖는가() {
        Assertions.assertThat(myService).isEqualTo(myServiceDecorated);
        Assertions.assertThat(myService2).isEqualTo(myServiceImpl);
    }
}
