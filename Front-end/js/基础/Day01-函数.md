# 函数

## 声明

```js
function func(a, b) {
  return a * b;
}
```

作用域是同级所有

### 函数表达式

```js
[var|let|const identifier = ] function [identifier]([param1, parame2, ...]) {
  // function body
  [return [sth];]
}
```

作用域就是变量identifier的作用域

关于上述的两个identifier, 都是函数名

```js
let f1 = function () {console.log("1")};
let f2 = function f2() {console.log("2")};
let f3 = function f4() {console.log("3")};
function f5() {console.log("4")};
f1(); // 1
f2(); // 2
f3(); // 3
// f4(); /*未解析的函数或方法 f4() */
f5(); // 4
```

对于f4()的情况, 在源码上警告, 运行时发生异常:

![image-20250807204314981](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/js/基础/Day01-函数/image-20250807204314981.png)

函数的声明存在作用域

```js
const simpleTest = function () {
  function f() {
    console.log('f');
  }
};
f(); // 未解析的函数或方法 f() 
```

### 箭头函数

ES6 中引入

0.   传统函数声明

     ```js
     hello = function(msg) {
       return "Hello World!" + msg;
     }
     ```

1.   省略`function`关键字, 用运算符代替

     ```JS
     hello = (msg) => {
         return "Hello World: " + msg;
     }
     ```

2.   如果函数体只有一行, 则省略`{}`, 这一行是`return`语句的, 省略`return`

     ```js
     var simpleTest1 = (msg) => console.log(msg); // void
     var simpleTest2 = () => 'Hello world' + msg; // string
     ```

## 调用

```js
identifier([arg1,arg2,...]);
```

```js
function f(p1, p2, p3) {
  console.log(p1);
  console.log(p2);
  console.log(p3);
}

f(1, 'x');
// 1 x undefined
```

### 自调用

```js
(function () {
  var x = "Hello!!";  // 我会调用自己
})();
```

## 参数

-   形参时没有指定数据类型
-   形参不进行类型检测
-   形参的个数不进行检测,  默认都是`undefined`

### arguments对象

包含了函数调用的参数数组

```js
function findMax() {
  var i;
  var max = -Infinity;
  for (i = 0; i < arguments.length; i++) {
    if (arguments[i] > max) {
      max = arguments[i];
    }
  }
  return max;
}
```

## call()

可以将函数中的`this`的指向的对象转换

```js
var person = {
  fullName: function(city, country) {
    return this.firstName + " " + this.lastName + "," + city + "," + country;
  }
}
var person1 = {
  firstName:"John",
  lastName: "Doe"
}
person.fullName.call(person1, "Oslo", "Norway"); // person对象中的this转而指向了person1
```

应该类似于`invoke`

## apply()

-   call() 方法分别接受参数。
-   apply() 方法接受**数组形式的参数**

如果 apply() 方法的第一个参数不是对象

-   严格模式下，第一个参数被转化为对象
-   "非严格"模式下，改变this, 将this指向全局对象

## 函数闭包

在函数里定义函数

```js
function add() {
  var counter = 0;
  function plus() {counter += 1;}
  plus();   
  return counter;
}
```

### 示例

需要一个单例的counter(number), 只有一个increment方法, 给这个counter自增

解决方法

-   在一个匿名函数里实例化一次counter, 这个counter是局部变量, 无法被外界访问
-   匿名函数返回increment函数, 这个increment函数被作为对象返回的时候, 就可以被外界访问这个函数
-   将返回值(function)函数赋值给一个变量

```js
const increment = (function () {
  let counter = 0;
  let func = function () {
    return counter++;
  };
  return func;
})(); /*匿名函数被执行一次*/

function f2() {
  console.log(increment()); // 0
  console.log(increment()); // 1
  console.log(increment()); // 2
  console.log(increment()); // 3
}

```

略微化简

```js
const increment = (() => {
  let counter = 0;
  return () => counter++;
})(); 
```

