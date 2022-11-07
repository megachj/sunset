package sunset.reactive.common.utils;

import reactor.core.publisher.SignalType;

public class ReactorLoggingUtils {

    public static final String PREFIX = "sunset.react.";

    public static final SignalType[] SIGNALS = new SignalType[]{
        SignalType.SUBSCRIBE, SignalType.REQUEST, SignalType.CANCEL,
        SignalType.ON_SUBSCRIBE, SignalType.ON_NEXT, SignalType.ON_COMPLETE, SignalType.ON_ERROR
    };
}
