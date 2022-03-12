/**
 * 표현식(Expression): 값으로 평가되는 구문. 예) 임의의 숫자, 문자열 모두 expression 이다.
 *  - 값을 반환한다는 표현을 js 에서는 보통 '값을 평가한다'라고 표현한다.
 *  - 변수에 값을 할당하는 것도 표현식이다.
 * 
 * 문(Statement): 지시를 내리는 문장.
 *  - 표현문: 함수 호출
 *  - 선언문: 변수 또는 함수 정의
 *  - 조건문: if / else if / else 문, switch 문
 *  - 반복문: while 문, do while 문, for 문, for in 문, for of 문
 *  - 점프문: break 문, continue 문, return 문
 */


'javascript'; // 이 문자열은 결괏값으로 'javascript'를 반환하는 표현식이다.

var a;
a = 1; // 값을 할당하는 것이므로 표현식이다.

var x = 1;
var y = 2;
x + y; // 여러 표현식을 결합하는 것을 복합 표현식이라고 한다.
console.log("--------------------------------------------------\n");

/**
 * truthy 값, falsy 값
 * 
 * falsy 값: false, null, undefined, NaN, ''(빈문자열), 0, 0n
 * truthy 값: falsy 값 이외의 모든 값
 *  - truthy 인데 falsy 로 헷갈리는 값: 빈 배열, 빈 객체, 공백 또는 줄 바꿈 문자열, 문자열 'false'
 */ 

/**
 * 동등 연산자(==): 객체 참조 값이 동일하거나, 피연산자들의 타입이 달라도 같은 값을 반환할 수 있다면 동등하다고 판단
 * 엄격한 동등 연산자(===): 암시적 강제 변환을 허용하지 않는다. 
 * 
 * 엄격한 동등 연산자가 true 면 동등 연산자도 당연히 true 이다.
 */
console.log("### 동등 연산자 예제 ###");
console.log(1 == true); // true, 타입이 다르므로 1 을 암시적 형 변환 truthy 로 판단
console.log("1" == 1); // true
console.log("a" == 1); // false
console.log("true" == true); // false
console.log([] == {}); // false
console.log("--------------------------------------------------\n");

/**
 * in 연산자: 객체에 특정 프로퍼티가 있는지 확인
 * instanceof 연산자: 좌측 피연산자가 우측 피연산자의 인스턴스인지 확인
 */
console.log("### in, instanceof 예제 ###");
const obj = { a: 1, b: 2 };
console.log('a' in obj); // true
console.log('c' in obj); // false

const arr = [1, 2];
console.log(arr instanceof Array); // true
console.log(arr instanceof Date); // false
console.log("--------------------------------------------------\n");

/**
 * typeof 연산자: 피연산자의 타입을 string 형으로 반환
 */
console.log("### in, instanceof 예제 ###");
console.log(typeof 'js');
console.log(typeof 1);
console.log(typeof true);
console.log(typeof undefined);
console.log(typeof null); // object, 자바스크립트 설계 오류로 인해 null 이 아닌 object 임
console.log(typeof function(){});
console.log(typeof {});
console.log(typeof Symbol());
console.log(typeof 1n);
console.log("--------------------------------------------------\n");
