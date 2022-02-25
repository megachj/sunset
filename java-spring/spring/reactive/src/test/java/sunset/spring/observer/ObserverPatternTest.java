package sunset.spring.observer;

import static org.mockito.Mockito.times;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ObserverPatternTest {

    @Test
    public void 옵저버패턴_테스트() {
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
}
