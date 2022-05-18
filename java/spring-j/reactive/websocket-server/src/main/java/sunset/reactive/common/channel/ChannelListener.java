package sunset.reactive.common.channel;

public interface ChannelListener<T> {

    void onData(T t);

    void complete();
}
