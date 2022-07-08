package sunset.reactive.common.pattern.observer;

public class SingleObservable<T> implements Observable<T> {

    private Observer<T> observer;

    @Override
    public void add(Observer<T> observer) {
        if (this.observer != null) {
            throw new IllegalStateException("SingleObservable 은 Observer 를 1개만 등록할 수 있습니다.");
        }
        this.observer = observer;
    }

    @Override
    public void remove(Observer<T> observer) {
        this.observer = null;
    }

    @Override
    public void notifyObservers(T event) {
        if (observer == null) {
            throw new IllegalStateException("등록된 Observer 가 없습니다.");
        }
        observer.observe(event);
    }

    public static <T> SingleObservable<T> newObservable() {
        return new SingleObservable<>();
    }
}
