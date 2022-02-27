package sunset.spring;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Hidden
public class HealthCheckController {
    @GetMapping("/api/ping")
    public String ping() { return "pong"; }
}
