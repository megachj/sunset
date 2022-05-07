# Reference
### 표준 문서
- [reactive-streams.org](https://www.reactive-streams.org/)
- [Java API](https://www.reactive-streams.org/reactive-streams-1.0.3-javadoc/org/reactivestreams/package-summary.html)
- [JVM Specification](https://github.com/reactive-streams/reactive-streams-jvm/blob/v1.0.3/README.md#specification)
- [TCK](https://www.reactive-streams.org/reactive-streams-tck-1.0.3-javadoc/)
- [implementation examples](https://www.reactive-streams.org/reactive-streams-examples-1.0.3-javadoc/org/reactivestreams/example/unicast/package-summary.html)

# Iterable 과 Observable 에 대해
* Iterable, Observable 은 모두 데이터 소스이다.
* Iterable <---> Observable (duality): Iterable 과 Observable 은 쌍대성을 이룬다. 즉 동일한 기능을 다르게 표현(구현)한다.
* Iterable 형태: Pull, 사용하는 쪽에서 데이터를 끌어오는 방식
* Observable 형태: Push, 사용하는 쪽에다 데이터를 밀어주는 방식

# Reactive Streams 에 대해
리액티브 스트림 스펙에 정의된 객체는 아래 4개이다.

```java
public interface Publisher<T> {
    void subscribe(Subscriber<? super T> s);
}

public interface Subscriber<T> {
    void onComplete();
    void onError(Throwable t);
    void onNext(T t);
    void onSubscribe(Subscription s);
}

public interface Subscription {
    void cancel();
    void request(long n);
}

public interface Processor<T, R> extends Subscriber<T>, Publisher<R> { }
```

Subscriber 의 메서드들이 불려지는 순서도 스펙에 정의되어 있다.
```
onSubscribe onNext* (onError | onComplete)?
```
1. onSubscribe 가 가장 먼저 호출된다.
2. onNext 는 0회 ~ 무제한 호출된다.
3. onError 또는 onComplete 중 1개만 0번 아니면 1번 호출된다. 그리고 둘 중 한개가 불리게 되면 Subscription 은 종료된다.

# Operators 에 대해
Publisher 와 Subscriber 가 바로 연결되어 있을 때는 아래와 같다.
```
// publisher 와 subscriber 가 직접 연결되어 있을 때
Publisher --[dataA]--> Subscriber

// 중간에 operator 가 연결되어 있을 때
Publisher --[dataA]--> OperatorA --[dataB]--> OperatorB --[dataC]--> Subscriber
```
