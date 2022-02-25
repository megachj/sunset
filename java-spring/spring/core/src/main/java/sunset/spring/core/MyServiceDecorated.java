package sunset.spring.core;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
public class MyServiceDecorated implements MyServiceSpec {
    @Override
    public void hello() {
        System.out.println("Hello MyServiceDecorated!");
    }
}
