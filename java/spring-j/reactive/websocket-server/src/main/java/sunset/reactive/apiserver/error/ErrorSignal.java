package sunset.reactive.apiserver.error;

import lombok.Value;

@Value(staticConstructor = "of")
public class ErrorSignal {

    Throwable throwable;
    String requestText;
}
