package sunset.spring.observer;

public interface Observer<T> {
    void observe(T event);
}
