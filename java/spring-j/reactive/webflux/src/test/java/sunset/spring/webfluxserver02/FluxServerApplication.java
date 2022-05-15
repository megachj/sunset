package sunset.spring.webfluxserver02;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootApplication
@RestController
public class FluxServerApplication {

    @GetMapping("/event/{id}")
    Mono<Event> event(@PathVariable long id) {
        return Mono.just(new Event(id, "event" + id));
    }

    @GetMapping("/events-mono")
    Mono<List<Event>> eventsMono() {
        List<Event> list = Arrays.asList(new Event(1L, "event1"), new Event(2L, "event2"));
        return Mono.just(list);
    }

    @GetMapping(value = "/events-flux")
    Flux<Event> eventsFlux() {
        List<Event> list = Arrays.asList(new Event(1L, "event1"), new Event(2L, "event2"));
        return Flux.fromIterable(list);
    }

    // sse(server-sent-event)
    @GetMapping(value = "/events-flux-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Event> eventsFluxStream() {
        Flux<Event> es = Flux
            .<Event, Long>generate(() -> 1L, (id, sink) -> {
                sink.next(new Event(id, "value" + id));
                return id + 1;
            });

        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));

        return Flux.zip(es, interval).map(tu -> tu.getT1());
    }

    public static void main(String[] args) {
        SpringApplication.run(FluxServerApplication.class, args);
    }

    @Data
    @AllArgsConstructor
    public static class Event {
        long id;
        String value;
    }
}
