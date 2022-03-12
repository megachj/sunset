/**
 * 기본 문법(배열)
 */
[n1, n2, ...rest_n] = [1, 2, 3, 4, 5, 6, 7, 8, 9];
console.log(n1, n2, rest_n);
console.log("--------------------------------------------------\n");

/**
 * 기본 문법(객체)
 */
var { a1, a2, ...rest_a } = { a1: 10, a2: 20, a3: 30, a4: 40 };
console.log(a1, a2, rest_a);

// 원래의 key 값과 다른 이름으로 변수 사용하기
var {
  b1: user,
  b2: address,
  ...rest_b
} = { b1: { name: "침착맨", age: 40 }, b2: "강동구", b3: 30, b4: 40 };
console.log(user, address, rest_b);

// 객체의 키가 변수명으로 사용 불가능한 문자열인 경우
var key = "it is key";
var { "an-apple": an_apple, [key]: it_is_key } = {
  "an-apple": 10,
  "it is key": 20,
};
console.log(an_apple, it_is_key);

// 변수 선언 명시(var, let, const) 가 없을 경우 괄호를 사용해 묶어야 한다.
({ c, d } = { c: 30, d: 40 });
console.log(c, d);
console.log("--------------------------------------------------\n");

/**
 * 기본값 할당
 */
[e1, e2] = [10];
console.log(e1, e2); // 10, undefined

var { f1, f2 } = { f1: 20 };
console.log(f1, f2); // 20, undefined

[e3 = 10, e4 = 20] = [30];
console.log(e3, e4); // 30, 20

var { f3 = 30, f4 = (new_f4 = 40) } = { f3: 10 };
console.log(f3, f4, new_f4); // 10, 40, 40
console.log("--------------------------------------------------\n");

/**
 * 복사(copy): 전개 연산자를 사용해 배열, 객체의 깊은 복사를 할 수 있다.
 */
var arr = [1, 2, 3];
var copy1 = arr; // 얕은 복사
var [...copy2] = arr; // 깊은 복사
var copy3 = [...arr]; // 깊은 복사

arr[0] = "String";
console.log(arr, copy1);
console.log(copy2, copy3);

var prevState = {
  name: "이말년",
  birth: "1983-12-05",
  age: 30,
};

var state = {
  ...prevState,
  name: "침착맨",
  age: 40,
  hobby: "인터넷방송",
};
console.log(prevState);
console.log(state);
console.log("--------------------------------------------------\n");

/**
 * 함수에서의 사용: 함수의 파라미터 부분에서 비구조화 할당을 사용할 수 있다.
 */
function renderUser({ name, age, addr }) {
  console.log(name, age, addr);
}

const users = [
  { name: "kim", age: 10, addr: "kor" },
  { name: "joe", age: 20, addr: "usa" },
  { name: "miko", age: 30, addr: "jp" },
];

users.map((user) => {
  renderUser(user);
});

// map 함수의 파라미터에도 바로 사용할 수 있다.
users.map(({ name, age, addr }) => {
  console.log(name, age, addr);
});
console.log("--------------------------------------------------\n");

/**
 * for of 문: 배열내 원소인 객체들은 for of 문을 사용해 비구조화 할 수 있따.
 */
for (var { name: n, age: a } of users) {
  console.log(n, a);
}
console.log("--------------------------------------------------\n");

/**
 * 중첩된 객체 및 배열의 비구조화
 */
const kim = {
  name: "kim",
  age: 10,
  addr: "kor",
  friends: [
    { name: "joe", age: 20, addr: "usa" },
    { name: "miko", age: 30, addr: "jp" },
    { name: "침착맨", age: 40, addr: "강동구" },
    { name: "주펄", age: 42, addr: "고기동" },
  ],
};

var {
  name: userName,
  friends: [, , { name: chimName }, { name: pearlName }],
} = kim;
console.log(userName, chimName, pearlName);
console.log("--------------------------------------------------\n");
