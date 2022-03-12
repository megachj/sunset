/**
 * 변수 선언: var, let, const
 */

// ------------------------------------------------------------
// var: 재선언 가능, 변경 가능, 함수 블록 스코프
var a1 = 1;
var a1 = 2;
{
  var a2 = "name";
}
console.log("a2", a2);

// ------------------------------------------------------------
// let: 재선언 불가능, 변경 가능, 블록 스코프
let b1 = 1;
// let b1 = 2; // 재선언이 불가능함, SyntaxError: Identifier 'b1' has already been declared
b1 = 2;
{
  let b2 = "name"; // b2 는 이 블록에서만 접근 가능
}
// console.log("b2", b2); // ReferenceError: b2 is not defined

// ------------------------------------------------------------
// const: 재선언 불가능, 변경 불가능, 블록 스코프
const c1 = 1;
// c1 = 2; // 변경 불가능, TypeError: Assignment to constant variable.
console.log("c1", c1);

/**
 * 자바스크립트의 값은 원시 타입과 객체(참조형)으로 나뉜다.
 *
 * 원시 타입: 하나의 값만 가지며, 불변 데이터라서 연산을 해도 기존 값이 변경되지 않는다.
 *  - number, string, boolean
 *  - null, undefined, Symbol, BigInt(ES2020에서 추가)
 */

// ------------------------------------------------------------
// number 타입
const num = 8;
const binaryNum = 0b1111; // 2진수 리터럴(0b 로 시작)
const octNum = 0o17; // 8진수 리터럴(0o 로 시작)
console.log(num, binaryNum, octNum);

// NaN: Not A Number(숫자가 아님)을 표현하는 값이며, 읽기 전용 속성이다.
const nan1 = 3 - "a";
console.log(nan1);
console.log(nan1 == nan1, nan1 === nan1); // false, 자기 자신과도 동등하지 않음 즉 NaN 은 어떤것과도 동일하지 않음
console.log(Number.isNaN(nan1)); // true, NaN 인건 이 메서드로 판별

// ------------------------------------------------------------
// string 타입: 홑따옴표(''), 쌍따옴표(""), 백틱(``)을 이용해 표현한다.
const str1 = "hello";
const str2 = "hello";
const str3 = `hello`;

// 이스케이프: 홑따옴표, 쌍따옴표를 텍스트 데이터로 표현해야할 때는 역슬래시(\) 문자로 이스케이프 처리한다.
const message = "Don't try this at home.\nok?"; // \n 과 같은 이스케이프 표현을 사용해 나타낸 특수문자도 있다.
console.log(message);

// 템플릿 리터럴은 `${}` 로 사용한다.
const name1 = "javascript";
const sentence = `hello ${name1}, your length: ${name1.length}`;
console.log(name1, sentence);

// ------------------------------------------------------------
// null: 값이 없음을 나타냄 -> 의도적으로 값이 없음을 나타낼 때 사용
// undefined: 값이 할당되지 않는 변수, 반환 값이 없는 함수의 결괏값은 자동으로 undefined 값이 할당됨 -> 값이 할당되지 않음을 나타내고 싶을 때 사용
let v;
function undefinedF() {}
console.log("undefined 예제: ", v, undefinedF());

// ------------------------------------------------------------
// Symbol은 ES2015 에서 도입된 원시 타입으로 '데이터의 유일함'을 나타낼 때 사용한다. 원시타입이기 때문에 new 키워드를 사용해 생성할 수 없다.
const sym1 = Symbol("key");
const sym2 = Symbol("key");
console.log("심볼 예제: ", sym1 == sym2, sym1 === sym2); // 같은 값이어도 전부 같지 않게 나온다.

// 심볼의 활용: 객체나 클래스에서 유일한 프로퍼티 만들 때 사용한다.
const user = {
  name: "javascript",
};
user[Symbol("id")] = "firstId";
console.log("심볼의 활용 예제: ", user, user[Symbol("id")]); // Symbol('id') 프로퍼티에 거의 접근할 수 없다.(이터레이터, 기타 메서드로 접근할 수 있음) -> 프로퍼티 변경이 안됨.

/**
 * 자바스크립트에서 원시 타입이 아닌 모든 값은 객체이다. 객체는 '이름(키):값' 형태로 컨테이너이며, 컨테이너 내부 값은 변경 가능이다.
 * 이름(키)에는 숫자, 문자열, 심볼만 가능하고, 값은 어떤 표현식이든 올 수 있다.
 */

// ------------------------------------------------------------
// Object 로 객체 생성: 거의 사용 안함.
const obj = new Object();
// 프로퍼티 생성
obj.id = "id";
obj.name = "name";

// Object 로 랩퍼 객체 생성, 쓸 일이 거의 없음.
const obj1 = new Object("obj"); // String 랩퍼 객체 생성
const obj2 = new Object(1); // Number 랩퍼 객체 생성

// 객체 리터럴: {} 를 사용해 객체 생성, 가장 많이 사용.
const obj3 = {
  id: "id",
  name: "name",
  child: {
    name: "childName",
  },
  1: 1,
};

// 생성자 함수: 그냥 일반 함수를 선언한 후 new 키워드로 호출하면 해당 함수는 생성자 함수로 동작한다.
function Vehicle(type) {
  // 생성자 함수는 대문자로 시작하는게 일반적이다.
  this.type = type;
}
const car = new Vehicle("Car"); // { type: 'Car' }
console.log("생성자 함수 예제: ", car);

// 객체 프로퍼티 읽기
const prop = "name";
console.log(obj3.name, obj3.child.name); // 점 표기법이 편한데 전부 다 사용할 수 있는게 아니다.
console.log(obj3["id"], obj[1], obj[prop]); // [] 표기법은 전부 다 사용할 수 있다.

// 자바스크립트 객체는 동적으로 프로퍼티가 생성, 갱신된다.
const obj4 = { name: "name" };
obj4.name = "hello"; // 프로퍼티 갱신
obj4["age"] = 30; // 프로퍼티 생성

// getter 와 setter: 접근자 프로퍼티
const userInfo = {
  name: "침",
  surname: "착맨",

  get fullName() {
    return `${this.name} ${this.surname}`;
  },
  set fullName(value) {
    [this.name, this.surname] = value.split(" ");
  },
};

console.log(userInfo.fullName);
userInfo.fullName = "이 병건";
console.log(userInfo.fullName);

/**
 * 자바스크립트의 배열은 객체의 특별한 형태로, '순서(인덱스)가 있는 데이터 집합'이다.
 * 그래서 배열은 객체이지만 정수 타입인 '인덱스를 프로퍼티로 갖는' 특별한 데이터이다.
 */

// ------------------------------------------------------------
// Array() 생성자 사용
const arr = new Array(1, "2", true); // [1, '2', true]
const arr1 = new Array(3); // 인자가 정수 1개일땐 해당 길이의 빈 리스트 생성, [empty, empty, empty]

// 배열 리터럴: [] 사용
const arr2 = [1, "2", true];

// 원소 접근과 동적인 원소 생성
const arr3 = ["f", "o", "o"];
console.log(arr3[0], arr3[1], arr3[2], arr3[3]); // f o o undefined

// 희소 배열: 연속적이지 않고 중간에 빈 값이 있는 배열 -> 왠만하면 지양하자.
const arr4 = [];
arr4[0] = 0;
arr4[2] = 2;
console.log(arr4, arr4.length); // [0, empty, 2] 길이는 마지막 인덱스보다 항상 1큼

// 유사 배열 객체: length 프로퍼티로 양의 정수 값을 가진 객체여야 한다.
const arr5 = {
  0: "Hi",
  1: "My",
  2: "name is",
  3: "javascript",
  length: 4,
};

// 유사 배열 객체의 대표적인 예) arguments 객체는 함수에 전달한 인자를 유사 배열 객체로 만든 데이터이다.
function foo(a, b, c) {
  console.log(arguments[0], arguments[1], arguments[2]);
  console.log(arguments.length);

  // 배열이 아닌 유사 배열 객체이기 때문에 배열의 내장 메서드를 사용할 수 없다. 아래 코드는 TypeError가 발생한다.
  // arguments.forEach((arg) => {
  //   console.log(arg);
  // });

  // 배열의 내장 메서드를 call() 또는 apply() 함수와 결합해 사용할 수 있다.
  Array.prototype.forEach.call(arguments, (arg) => {
    console.log(arg);
  });
}
foo("a", "b", "c");

// ------------------------------------------------------------
// 랩퍼 객체는 원시 타입의 객체
// 원시타입 -박싱-> 랩퍼 객체
const str5 = "javascript";
console.log("javascript".length); // 문자열 -> 임시 객체로 변환(박싱), 프로퍼티(length) 값 읽고 임시 객체는 메모리에서 제거

const str4 = new String("javascript");
console.log(typeof str4); // object, 랩퍼 타입은 객체이다.

// 랩퍼 객체 -언박싱-> 원시타입
const wrapperNum = new Number(11);
console.log(wrapperNum.valueOf(), typeof wrapperNum.valueOf());
