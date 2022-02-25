package sunset.spring.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MyController {

    private final MyService myService;

    /**
     * curl 명령어: curl -X GET 'http://localhost:8080/run/{type}'
     *
     * @param type
     * @return
     */
    @GetMapping("/run/{type}")
    public String run(@PathVariable AsyncService.Type type) {
        myService.runTask(type);
        return "OK";
    }
}
