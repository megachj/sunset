/**
 * 스코프: 변수나 매개변수에 접근할 수 있는 범위를 결정한다.
 *  - 함수 스코프: 선언된 함수 단위로 생성되는 스코프이다. 함수 안에 선언된 변수, 함수들이 포함된다.
 *  - 블록 스코프: 블록({}) 단위로 생성되는 스코프이다.
 *
 * var 은 함수 스코프이고, let과 const 는 블록 스코프이다.
 *
 * 렉시컬 스코프란? 변수나 함수를 어디에 작성하였는지에 기초해 스코프가 결정되는 것을 말한다.
 * 대부분 현대 프로그래밍 언어들은 렉시컬 스코프를 따르고 자바스크립트도 마찬가지이다.(반대는 동적 스코프)
 *
 * 스코프 체이닝: 스코프 체인을 따라 검색하는 과정(안쪽 -> 바깥쪽 순서)
 */
function foo() {
  var a = 1; // 함수 스코프
  function bar(b) {
    console.log(a, b); // 1, 2
  }
  bar(2);
}
foo();

function foo1() {
  if (true) {
    var a = 1; // 함수 스코프
  }
  console.log(a); // 1
}
foo1();

function foo2() {
  if (true) {
    const a = 1; // 블록 스코프
  }
  // console.log(a); // 에러
}
foo2();
console.log("--------------------------------------------------\n");

/**
 * 호이스팅(Hoisting): 선언문이 스코프 내의 가장 최상단으로 끌어올려지는 것을 말한다.
 *
 * 자바스크립트의 변수는 세 가지 단계로 나누어 생성된다.
 *  1. 선언: 스코프에 변수를 선언한다.
 *  2. 초기화: 변수의 값을 undefined 로 초기화하며, 실제로 변수에 접근 가능한 단계이다.
 *  3. 할당: 할당문을 만나면 변수에 실제 값을 할당한다.
 *
 * 호이스팅은 스코프별로 동작한다.
 *
 * 변수의 호이스팅:
 *  - var: 스코프 최상단에서 선언과 초기화가 한 번에 실행된다.
 *  - let, const: 스코프 최상단에서 선언되고, 초기화는 실제 선언문을 만나면 실행된다. 초기화 전에 변수에 접근시 ReferenceError 가 발생한다.
 *    선언과 초기화 사이 구간을 Temporal Dead Zone(TDZ) 라고도 부른다.
 *
 * 함수 선언문(function)의 호이스팅:
 *  함수 선언, 초기화, 할당 세 가지 단계가 스코프 최상단에서 실행된다.
 *  함수 표현식(var, let, const) 는 변수의 호이스팅 규칙에 따라 동작한다.
 */
function foo4() {
  console.log(a); // 왜 ReferenceError 가 나지 않고, undefined 로 보이는걸까?
  var a = 1;
}
foo4();

// 위 코드 foo4 는 실제로 이런 순서로 실행된다고 할 수 있다.
function foo5() {
  var a; // 선언 및 초기화(undefined)
  console.log(a);
  a = 1; // 할당
}
foo5();

function foo6() {
  // 최상단에서는 선언이 됨.
  console.log(a); // TDZ 구간
  let a; // 이 단계에서 초기화 및 할당이 됨.
}
// foo6();

console.log("--------------------------------------------------\n");

/**
 * 클로저(closure)
 */
