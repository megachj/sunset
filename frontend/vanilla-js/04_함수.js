/**
 * 함수: 자바스크립트 함수는 일급 함수이다.(변수에 함수 할당, 함수를 인자로 전달, 함수를 반환 값으로 사용)
 *
 * 함수를 생성하는 방법은 세 가지가 있다.
 *  - 함수 선언문
 *  - 함수 표현식
 *  - Function 생성자 함수
 */

// 1) 함수 선언문: 호이스팅됨(코드의 최상단으로 끌어올려지는 효과, 따라서 선언하기 전에서도 함수를 호출할 수 있음)
function multiply(a, b) {
  return a * b;
}
console.log(multiply(1, 2));

// 2) 함수 표현식
// 함수 이름이 없는 익명 함수 표현식
const multiply2 = function (a, b) {
  return a * b;
};
console.log(multiply2(1, 2));

// 함수 이름이 있는 기명 함수 표현식: 함수 이름을 함수 내에서만 사용할 수 있어서 재귀 함수시에 사용한다.
const factorial = function doJob(n) {
  return n <= 1 ? 1 : n * factorial(n - 1);
};
console.log(factorial(5));
console.log("--------------------------------------------------\n");

// 나머지 매개변수 ...args 는 배열이다.
function sum(type, ...args) {
  console.log("type", type);
  args.forEach((arg) => console.log("args", arg));
}
sum("money", 1, 2);
console.log("--------------------------------------------------\n");

/**
 * 화살표 함수: () => {}
 *  - function 키워드 생략
 *  - 매개변수가 하나인 경우 괄호 생략 가능
 *  - 함수 몸체에서 문이 하나인 경우 중괄호, return 생략 가능
 *  - arguments 객체와 this 를 바인딩하지 않음(나머지 매개변수는 쓸 수 있음)
 */
const arr = [1, 2, 3, 4, 5];

// 일반함수를 map() 의 인자로 전달
const map1 = arr.map(function (element, index) {
  return `${index}: ${element}`;
});

// 화살표함수를 map() 의 인자로 전달하면 더 간결하다.
const map2 = arr.map((element, index) => `${index}: ${element}`);
console.log(map1, map2);
console.log("--------------------------------------------------\n");

// arguments 객체는 사용하지 못하고, 나머지 매개변수는 쓸 수 있다.
const findFirst = (...args) => args[0];
console.log(findFirst(1, 2, 3, 4, 5));
console.log("--------------------------------------------------\n");

/**
 * this: this 는 읽기 전용 값으로 함수를 호출한 방법에 의해 값이 달라진다.
 */
console.log("### this ###");
try {
  console.log(this === window, this === undefined); // true, false
} catch (err) {
  console.log("브라우저에서 실행해주세요.");
}
console.log("--------------------------------------------------\n");

/**
 * 일반 함수의 this 바인딩
 *  - 전역 실행 컨텍스트에서의 this 는 항상 전역 객체를 참조한다.
 *  - 전역 실행 컨텍스트는 자바스크립트 엔진이 코드를 실행할 때 처음으로 생성되는 컨텍스트로,
 *    자바스크립트 코드가 실행되는 최상위 환경이라고 할 수 있다.
 *    - 브라우저 환경: window 객체가 전역 객체
 *    - Node.js 환경: global 객체가 전역 객체
 */
console.log("### 일반함수 this 바인딩 ###");

function func() {
  try {
    console.log(this === window); // true
  } catch (err) {
    console.log("브라우저에서 실행해주세요.");
  }
}
func();

function strictFunc() {
  "use strict";
  try {
    console.log(this === window, this === undefined); // false, true
  } catch (err) {
    console.log("브라우저에서 실행해주세요.");
  }
}
strictFunc();
console.log("--------------------------------------------------\n");

/**
 * 생성자 함수의 this 바인딩
 *  - 생성자 함수 내 코드를 실행하기 전에 객체를 만들어 this에 바인딩한다.
 *  - 생성된 객체는 생성자 함수의 prototype 프로퍼티에 해당하는 객체를 프로토타입으로 설정한다.
 */
console.log("### 생성자 함수 this 바인딩 ###");
function Vehicle(type) {
  // 아래 코드가 실행하기 전에 객체를 만들어 this 에 바인딩한다.
  this.type = type; // 프로퍼티 생성
  // retur this; // 이 부분을 생략해도 this 에 바인딩한 객체가 반환된다.
}
const car = new Vehicle("Car"); // 생성자 함수 호출 시 new 키워드를 사용해야 위 내용이 적용된다.
const bicycle = Vehicle("Bicycle"); // new 를 안쓰면 객체가 생성되지 않는다.
console.log(car, bicycle);
console.log("--------------------------------------------------\n");

/**
 * 메서드의 this 바인딩
 *  - 객체의 프로퍼티인 함수를 메서드라고 한다.
 *  - 메서드를 호출하면 this 는 해당 메서드를 소유하는 객체로 바인딩된다.
 */
console.log("### 메서드 this 바인딩 ###");
const obj = {
  lang: "javascript",
  greeting() {
    // console.log(this);
    return `hello ${this.lang}`;
  },
};
console.log("객체를 통해 메서드를 호출: ", obj.greeting()); // 메서드 형태로 호출해서 this 가 할당됨

const greeting = obj.greeting;
console.log("메서드를 변수에 할당하고 변수로 호출: ", greeting()); // 일반 함수 형태로 호출해서 this 가 undefined
console.log("--------------------------------------------------\n");

/**
 * call(), apply() 와 bind()
 *  - 함수의 호출 방법에 상관없이 this 를 특정한 객체로 바인딩 하는 방법
 *  - 이를 명시적 방인딩이라고 한다.
 *
 * call():
 *  첫 번째 인자로 this 로 바인딩할 객체를 지정한다.
 *  그 이후 인자들은 모두 호출하는 함수로 전달된다.
 *
 * apply():
 *  call() 메서드와 동일하지만 호출하는 함수에 전달할 인자들을 배열 형태로 전달해야 한다.
 *
 * bind():
 *  - 함수의 this 바인딩을 영구적으로 변경한다(생성자 함수로 사용되는 경우는 예외이다).
 *    bind() 메서드로 this 가 변경된 함수는 call(), apply() 또는 다른 bind() 를 사용해도 this 바인딩을 변경할 수 없다.
 *  - this 를 바인딩하여 함수를 호출하는 것이 아니라 '새로운 함수를 반환'한다.
 */
console.log("### call(), apply() ###");

const obj1 = { name: "javascript" };
function greeting1() {
  return `Hello ${this.name}`;
}
console.log(greeting1.call(obj1));

function getUserInfo(age, country) {
  return `name: ${this.name}, age: ${age}, country: ${country}`;
}
console.log(getUserInfo.call(obj1, 20, "Korea"));
console.log(getUserInfo.apply(obj1, [20, "Korea"]));

console.log("\n### bind() ###");
const obj2 = { name: "Lee" };
const obj3 = { name: "Han" };

function getUserInfo(age, country) {
  return `name: ${this.name}, age: ${age}, country: ${country}`;
}

const bound = getUserInfo.bind(obj2, 20); // this 를 obj2 로 바인딩, 또 함수의 age 인자 값을 20으로 고정시킬 수 있음

console.log(bound("Korea"));
console.log(bound(30, "Korea"));
console.log(bound.apply(obj3, [30, "Korea"])); // 이미 바인딩이 되어서 obj2 로 고정되어 있음
console.log("--------------------------------------------------\n");

/**
 * TODO: 잘 이해가 되지 않음
 * 화살표 함수와 렉시컬 this
 *
 * 렉시컬 this:
 *  함수를 어디에 선언하는지에 따라 this 에 값이 결정된다.
 *  화살표 함수의 this 는 화살표 함수를 둘러싸고 있는 렉시컬 스코프에서 this 의 값을 받아 사용한다.
 *  이러한 this 를 렉시컬 this 라고 하며 이 값은 변경되지 않는다.
 */
const obj4 = {
  lang: "javascript",
  greeting: () => {
    return `hello ${this.lang}`;
  },
};
console.log(obj4.greeting()); // 왜 undefined 일까?

const obj5 = { lang: "javascript" };
const greeting2 = () => {
  return `hello ${this.lang}`;
};
console.log(greeting2.call(obj5)); // 화살표 함수는 call() 를 사용해도 this 바인딩이 바뀌지 않는다.

const obj6 = {
  name: "js",
  greeting() {
    setTimeout(
      function timer() {
        console.log(this.name);
      }.bind(this),
      1000
    );
  },
};
obj6.greeting();

const obj7 = {
  name: "js",
  greeting() {
    setTimeout(() => {
      console.log(this.name);
    }, 1000);
  },
};
obj7.greeting();
