package sunset.spring.aop_reflection;

import sunset.spring.aop_reflection.service.TargetService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@RequiredArgsConstructor
@Component
public class MyCommandLineRunner implements CommandLineRunner {

    private final TargetService targetService;

    @Override
    public void run(String... args) throws Exception {
        targetService.hello("Hello!", Arrays.asList("faker", "smeb"));
    }
}
