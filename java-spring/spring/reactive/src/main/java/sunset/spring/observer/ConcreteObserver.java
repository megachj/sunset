package sunset.spring.observer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConcreteObserver implements Observer<String> {

    private final String name;

    @Override
    public void observe(String event) {
        System.out.println("Observer " + name + ": " + event);
    }
}
