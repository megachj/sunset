package sunset.reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;

@SpringBootApplication
public class WebSocketApplication {

    public static void main(String[] args) {
        BlockHound.builder().install();
        SpringApplication.run(WebSocketApplication.class, args);
    }
}
