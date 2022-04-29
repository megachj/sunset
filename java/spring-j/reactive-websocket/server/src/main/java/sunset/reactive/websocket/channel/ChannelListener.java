package sunset.reactive.websocket.channel;

public interface ChannelListener<T> {

    void onData(T t);

    void complete();
}
