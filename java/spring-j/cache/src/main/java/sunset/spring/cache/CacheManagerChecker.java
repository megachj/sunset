package sunset.spring.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor
@Component
public class CacheManagerChecker implements CommandLineRunner {

    private final CacheManager cacheManager;

    @Override
    public void run(String... args) throws Exception {
        String cacheClassName = String.format("Using cache manager: [%s]", this.cacheManager.getClass().getName());
        String cacheNames = String.format("cache names: %s", new ArrayList<>(cacheManager.getCacheNames()));
        log.info("\n\n===================================================\n" +
                cacheClassName + "\n" +
                cacheNames + "\n" +
                "===================================================\n\n");
    }
}
