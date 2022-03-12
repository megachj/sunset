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

// 나머지 매개변수 ...args 는 배열이다.
function sum(type, ...args) {
  console.log("type", type);
  args.forEach((arg) => console.log("args", arg));
}
sum("money", 1, 2);

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

// arguments 객체는 사용하지 못하고, 나머지 매개변수는 쓸 수 있다.
const findFirst = (...args) => args[0];
console.log(findFirst(1, 2, 3, 4, 5));
