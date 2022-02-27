package sunset.spring.resilence4j.springboot2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {"sunset.spring.*"})
public class Resilience4jTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(Resilience4jTestApplication.class, args);
    }
}
