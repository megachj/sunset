package sunset.reactive.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
public class SchedulerConfig {

    @Bean
    public Scheduler wsConnectionTimer() {
        return Schedulers.newParallel("wsConnTimer", 3);
    }
}
