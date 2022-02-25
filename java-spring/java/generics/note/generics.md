## Generics 란?
자바 5에서 부터 추가된 제네릭은 타입을 파라미터로 만들어 넘어오는 파라미터에 따라 다른 타입이 되게한다.  
컴파일 시점에 타입 체크를 할 수 있어 정적인 타입 안전성을 확보할 수 있다.

## Type Parameter
Type Parameter 란, 타입이나 메소드에 붙은 `<T>` 의 T 를 말한다. 

Type Parameter 를 엄밀히 구분하면 다음과 같다.
* 형식 형인자(formal type parameter): `<T>` 의 T
* 실 형인자(actual type parameter): `<T>` 의 실제 인수 `<String>` 에서 String 과 같은 것

## Generic Type
Type Parameter 를 선언한 타입을 Generic Type 이라 한다.

Generic Type 을 실제 사용할 때는 Type Parameter 를 사용해도 되고, 안 해도 된다.
* 형인자 자료형(parameterized type): 예시 `List<String>`
* 무인자 자료형(raw type): 예시 `List`

### Type Erasure
자바 5에서 Generic Type 이 생긴 이후로는 타입을 제한하고 싶지 않더라도 Raw Type 을 사용하지 말고, `<Object>` 같은 형태로 사용하는 것을 권장한다.
단지 Raw Type 을 지원하는 이유는 제네릭이 없던시절(Java5 미만)의 코드 하위호환을 위해서다. 그리고 이러한 맥락 때문에 Type Erasure 가 사용된다.

Java5 이상의 컴파일러도 이전 코드를 문제없이 컴파일 하기위해 자바는 컴파일 시에 Type Parameter 정보들을 소거시킨다. 
제네릭정보를 소거시키기때문에 바이트코드에 제네릭에 대한 정보는 아무것도 없고 런타임시에는 제네릭이 없는 셈이다.

### Generic Type 상속 관계는 Type Parameter 와 관련이 없다.
예시) Number, Integer 는 Super - Sub 관계이다.  
List\<Number\>, List\<Integer\> 는 상속 관계가 없는 전혀 다른 타입이다. 

## Generic Method
Type Parameter 를 선언한 메소드를 Generic Method 라 한다.  
생성자에서도 Type Parameter 를 사용할 수 있다.

### 타입 추론
주로 생성자, 메소드 호출 시 컴파일러가 호출 파라미터, 받는 리턴 타입 등을 보고 타입을 추론하는 것.

자바 8 이상은 컴파일러가 대부분 추론을 잘하는데, 낮은 버전은 잘 못할 수 있다.

## Wildcard
Type Parameter 에 `<?>` 로 사용할 때의 ? 를 Wildcard 라고 하고, 범위 내 모든 타입을 받을 수 있음을 의미한다.
extends 로 upper bounded 되어 있지 않다면 Type Erasure 시에 Object 가 된다.

### List\<T\>, List\<?\>, List\<Object\> 차이
* List\<T\>: 타입이 정해지고 나면, 타입 파라미터 필드(이 경우 원소)에 대한 작업을 할 것이라는 의미이다.
* List\<?\>, List\<? extends Object\>: 타입 파라미터 필드에 관한 작업을 하지 않거나, 
타입 파라미터 필드(이 경우 원소)에 대한 작업을 Object 메소드(toString() 등)만 이용하겠다는 의미이다.
  * 원소가 필요하지 않은 List.size() 와 같은 작업만 사용
  * 원소 작업이 필요하더라도 toString() 과 같은 작업만 사용
  * List\<Object\>, List\<Integer\>, ... 와 같이 고정되어 있지 않고, 범위로 표현된다.
* List\<Object\>: List<\?\> 와 비슷한 의미인데, Generic Type 의 상속 관계 때문에 List\<Integer\> 와 같은 것을 받을 수 없다.
  * List\<Object\> 1개로 고정되어 있다.

## Bounded
* Upper bounded: `<T extends P>`
P 를 포함한 P 의 모든 자손 타입
* Lower bounded: `<T super P>`
P 를 포함한 P 의 모든 선조 타입

## Intersection Type
Intersection Type 이란 `&` 를 이용해 타입을 조합하는 것이다.  
예) (Comparable & Serializable & Cloneable)

Intersection Type 를 사용할 수 있는 곳은 두 군데이다.
* Upper bounded: 예) \<T extends Comparable\<T\> & Serializable & Cloneable\>
    * T 는 Comparable\<T\>, Serializable, Cloneable 을 모두 상속한 하위 클래스여야 한다.
* 람다식: 예) (Function<T, T> & Serializable) s -> s
    * 람다식은 함수형 인터페이스 타입이므로 동일한 제약 조건을 갖는다.
    사용한 Intersection Type 의 구현할 메소드가 반드시 1개여야 한다.
