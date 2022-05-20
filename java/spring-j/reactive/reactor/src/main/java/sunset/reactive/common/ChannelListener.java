package sunset.reactive.common;

public interface ChannelListener<T> {

    void onData(T chunk);

    // void complete();
}
