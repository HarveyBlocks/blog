# symbol

symbol是一种原始**数据类型**

## 作用

每个从 `Symbol()` 返回的 symbol 值都是唯一的

一个 symbol 值能作为对象属性的标识符

## 用法

`Symbol()` 函数会返回 **symbol** 类型的值

Symbol的静态属性会暴露几个内建的成员对象

Symbol静态方法会暴露全局的 symbol 注册

```js
const symbol1 = Symbol();
const symbol2 = Symbol(42);
const symbol3 = Symbol("foo");

console.log("----------------------dynamic----------------------");
console.log(typeof symbol1); // "symbol"
console.log(symbol2 === 42); // false
console.log(symbol2.description === "42");// true
console.log(symbol3.toString());// "Symbol(foo)"
console.log(Symbol("foo") === Symbol("foo")); // false

console.log("----------------------static----------------------");
// 从全局的 symbol 注册表设置 symbol
// 全局symbol存在, 则获取, 不存在则设置
let symbolGlobal = Symbol.for("A");
// 从全局的 symbol 注册表取得 symbol
let symbolGlobal2 = Symbol.for("A");
console.log(symbolGlobal===symbolGlobal2); // true
console.log(Symbol.keyFor(symbolGlobal)); // A
console.log(Symbol.keyFor(symbol3)); // undefined
```

## Symbol.iterator

一个对象实现了Symbol.iterator属性, 则认为是可迭代的, 可以使用for-of和for-in

## Symbols 与 JSON.stringify()

当使用 JSON.stringify() 时，以 symbol 值作为键的属性会被完全忽略：

