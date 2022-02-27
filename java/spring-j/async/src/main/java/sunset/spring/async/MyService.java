package sunset.spring.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MyService {

    private final AsyncService asyncService;

    public void runTask(AsyncService.Type type) {
        log.info("myService runTask start.");
        type.runTask(asyncService);
        log.info("myService runTask end.");
    }
}
