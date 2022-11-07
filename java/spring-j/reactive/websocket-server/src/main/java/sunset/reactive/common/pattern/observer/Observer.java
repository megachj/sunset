package sunset.reactive.common.pattern.observer;

public interface Observer<T> {

    void observe(T event);
}
