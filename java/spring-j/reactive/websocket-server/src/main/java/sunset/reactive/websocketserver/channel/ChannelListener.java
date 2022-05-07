package sunset.reactive.websocketserver.channel;

public interface ChannelListener<T> {

    void onData(T t);

    void complete();
}
