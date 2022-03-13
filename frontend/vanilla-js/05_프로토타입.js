/**
 * 프로토타입: 자바스크립트에서는 프로토타입을 기반으로 객체 지향의 '상속' 개념을 구현한다.
 *  - 모든 객체는 자신의 부모 역할을 하는 프로토타입 객체의 참조 링크를 가지고 있으며,
 *    이 링크를 통해 프로토타입으로부터 프로퍼티나 메서드를 상속받을 수 있다.
 *  - 객체의 프로토 타입은 참조 링크 형태로 '[[Prototype]]' 내부 프로퍼티에 저장된다.
 *  - [[Prototype]] 은 자바스크립트 엔진 내부에서만 사용가능하지만 크롬, 파이어폭스에선 __proto__ 로 접근할 수 있다.
 *    하지만 이러한 접근은 표준이 아니므로 지양해야하고, 만약 접근하고 싶다면 'Object.getPrototypeOf()' 를 사용하자.
 *
 * 프로토타입 체인: 프로토타입 체인은 상위 프로토타입과 연쇄적으로 연결된 구조를 말한다.
 *  프로퍼티, 메서드를 못찾으면 연쇄적으로 상위로 올라가면서 찾는 구조이다.
 *
 * 최상위 프로토타입: Object.prototype 은 프로토타입 체인의 최상위에 있는 프로토타입이다.
 *
 * 프로토타입의 생성: 객체가 생성되는 시점에 상위 프로토타입이 설정된다.
 */
const arr = []; // 배열객체 -> Array.prototype -> Object.prototype
console.log("배열", arr);
console.log("--------------------------------------------------\n");

/**
 * 프로토타입과 생성자 함수
 *  - 모든 함수에는 prototyp 이라는 특별한 프로퍼티가 존재한다.
 *  - prototype 이 일반 함수에서는 사용할 일이 없고, 생성자 함수에서는 특별한 역할을 한다.
 *  - [[Prototype]] 과 함수의 prototype 프로퍼티는 다르다.
 *    prototype 프로퍼티는 일반적인 객체의 프로퍼티이며, 프로토타입을 가리키는 참조 링크가 아니다.
 *
 * 생성자 함수의 prototype 프로퍼티
 *  - 생성자 함수로 생성된 객체는 '생성자 함수의 prototype 프로퍼티'가 프로토타입([[Prototype]]) 으로 설정된다.
 */
function Vehicle(type) {
  this.type = type;
}
const vehicle = new Vehicle("car");
console.log("Vehicle.prototype", Vehicle.prototype); // {}
console.log(Vehicle.prototype === Object.getPrototypeOf(vehicle)); // 생성자 함수의 prototype 이 생성된 객체의 프로토타입 링크가 된다.

// Vehicle.prototype 에 stop 메서드를 추가함.
Vehicle.prototype.stop = function () {
  console.log("stop!");
};
vehicle.stop();
console.log("--------------------------------------------------\n");

/**
 * 프로토타입을 사용한 상속 구현
 */
function Vehicle1() {
  console.log("initialize Vehicle1");
}
Vehicle1.prototype.run = function () {
  console.log("run!");
};
Vehicle1.prototype.stop = function () {
  console.log("stop!");
};

function Car1(type) {
  Vehicle1.apply(this, arguments);
  this.type = type;
}

function inherit(parent, child) {
  function F() {}
  F.prototype = parent.prototype;
  child.prototype = new F();
  child.prototype.constructor = child;

  child.prototype = parent.prototype;
}

inherit(Vehicle1, Car1);
console.log(new Car1("SUV"));
console.log("--------------------------------------------------\n");

/**
 * class: 자바스크립트의 클래스와 상속은 생성자 함수와 프로토타입을 사용해 구현할 수 있지만, 직관적이지 않고 번거롭다.
 *  그래서 ES2015 부터 class 키워드를 이용한 새로운 문법이 등장했다.
 */
class Vehicle2 {
  constructor() {
    console.log("initialize Vehicle2");
  }

  run() {
    console.log("run!");
  }

  stop() {
    console.log("stop!");
  }
}
console.log(new Vehicle2());

class Car2 extends Vehicle2 {
  #name; // private 접근 제한자 #

  constructor(type) {
    // this 를 사용하기 전에 super() 를 호출해야한다.
    // 왜냐하면 부모 클래스 constructor() 메서드에서 반환한 객체를 자식 클래스에서 사용하기 때문이다.
    super();
    this.type = type;
    this.#name = "myCar";
  }

  static CreateSUV() {
    return new Car2("SUV");
  }
}

const suv = Car2.CreateSUV();
console.log(suv);
// console.log(suv.#name); // private 접근자는 외부에서 접근할 수 없다.
console.log("--------------------------------------------------\n");
