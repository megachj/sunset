/**
 * 타입 변환: 명시적 강제 변환, 암시적 강제 변환
 *  - 명시적 강제 변환: 타입 변환 코드를 적어서 의도적으로 변환
 *  - 암시적 강제 변환: 표현식의 평가 중 타입이 변환
 */
console.log("### 문자열로 명시적 변환: String() ###");
console.log(
  String(3),
  String(3n),
  String(false),
  String(null),
  String(undefined),
  String({})
);
// console.log(String(Symbol())); // Symbol 은 변환시 TypeError 발생
console.log("--------------------------------------------------\n");

console.log("### 숫자로 명시적 변환: Number() ###");
console.log(
  Number(undefined),
  Number(null),
  Number("a"),
  Number("1"),
  Number({}),
  Number(true)
);
// console.log(Number(Symbol()), Number(3n)); // Symbol, BigInt 는 변환시 TypeError 발생
console.log("--------------------------------------------------\n");

console.log("### 불리언으로 명시적 변환: Boolean() ###");
console.log(
  Boolean(undefined),
  Boolean(null),
  Boolean(""),
  Boolean(0),
  Boolean(Symbol()),
  Boolean(1n),
  Boolean({})
);
console.log("--------------------------------------------------\n");
