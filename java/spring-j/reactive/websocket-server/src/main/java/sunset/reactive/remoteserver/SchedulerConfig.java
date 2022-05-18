package sunset.reactive.remoteserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
public class SchedulerConfig {

    @Bean
    public Scheduler asyncJobScheduler() {
        return Schedulers.newParallel("asyncJob");
    }
}
