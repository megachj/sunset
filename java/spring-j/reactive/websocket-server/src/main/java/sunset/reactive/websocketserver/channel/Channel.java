package sunset.reactive.websocketserver.channel;

import java.util.ArrayList;
import java.util.List;

public class Channel<T> {

    private List<ChannelListener<T>> listeners = new ArrayList<>();

    public void setListener(ChannelListener<T> listener) {
        listeners.add(listener);
    }

    public void publish(T data) {
        listeners.forEach(l -> {
            l.onData(data);
        });
    }

    public static <T> Channel<T> connectNewChannel() {
        return new Channel<>();
    }
}
