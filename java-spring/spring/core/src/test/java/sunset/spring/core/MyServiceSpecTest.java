package sunset.spring.core;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
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
