package sunset.spring.concept;

import static org.mockito.Mockito.times;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class T02_2_Observer패턴 {

    @Test
    public void 옵저버패턴_직접구현해서_사용해보기() {
        // given
        Subject<String> subject = new ConcreteSubject();
        Observer<String> observerA = Mockito.spy(new ConcreteObserver("A"));
        Observer<String> observerB = Mockito.spy(new ConcreteObserver("B"));

        // when
        subject.notifyObservers("No listeners");

        subject.registerObserver(observerA);
        subject.notifyObservers("Message for A");

        subject.registerObserver(observerB);
        subject.notifyObservers("Message for A & B");

        subject.unregisterObserver(observerA);
        subject.notifyObservers("Message for B");

        subject.unregisterObserver(observerB);
        subject.notifyObservers("No listeners");

        // then
        Mockito.verify(observerA, times(1)).observe("Message for A");
        Mockito.verify(observerA, times(1)).observe("Message for A & B");
        Mockito.verifyNoMoreInteractions(observerA);

        Mockito.verify(observerB, times(1)).observe("Message for A & B");
        Mockito.verify(observerB, times(1)).observe("Message for B");
        Mockito.verifyNoMoreInteractions(observerB);
    }

    public interface Subject<T> {

        void registerObserver(Observer<T> observer);

        void unregisterObserver(Observer<T> observer);

        void notifyObservers(T event);
    }

    public interface Observer<T> {

        void observe(T event);
    }

    public static class ConcreteSubject implements Subject<String> {

        private final Set<Observer<String>> observers = new CopyOnWriteArraySet<>();

        @Override
        public void registerObserver(Observer<String> observer) {
            observers.add(observer);
        }

        @Override
        public void unregisterObserver(Observer<String> observer) {
            observers.remove(observer);
        }

        @Override
        public void notifyObservers(String event) {
            observers.forEach(observer -> observer.observe(event));
        }
    }

    @RequiredArgsConstructor
    public static class ConcreteObserver implements Observer<String> {

        private final String name;

        @Override
        public void observe(String event) {
            System.out.println("Observer " + name + ": " + event);
        }
    }
}
