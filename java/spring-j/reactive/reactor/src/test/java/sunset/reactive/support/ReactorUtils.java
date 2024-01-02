package sunset.reactive.support;

import reactor.core.publisher.SignalType;

public class ReactorUtils {

    public static final SignalType[] ALL_SIGNAL_TYPES = {
        SignalType.SUBSCRIBE, SignalType.ON_SUBSCRIBE,
        SignalType.REQUEST, SignalType.CANCEL,
        SignalType.ON_NEXT,
        SignalType.ON_COMPLETE, SignalType.ON_ERROR
    };
}
