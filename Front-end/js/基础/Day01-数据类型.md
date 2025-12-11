# 数据类型

数值、字符串值、数组、对象

五种可包含值的数据类型:

-   string
-   number
-   boolean
-   object
-   function

有六种对象类型:

-   Object
-   Date
-   Array
-   String
-   Number
-   Boolean

同时有两种不能包含值的数据类型:

-   null
-   undefined

## 动态类型

js 认为 变量的类型由其被赋的值决定

## 数值

JavaScript 只有一种数值类型

数值时加小数点与否等价

允许使用科学计数法

```js
console.log(1.2);
console.log(1.20);
console.log(1.2e3);
console.log(12/*L*/);
console.log(1.2/*f*/);
console.log(1e30===(1e30+1e-30));
```

JavaScript 将数字存储为 64 位浮点数

## 布尔值

有常量`true` 和`false`

## 字符串

```js
console.log("string")
console.log('string')
```

## 对象

```js
console.log({ name: 'Mike', age: 12, });
```

![image-20250807184307420](../../assetss/Day01-数据类型/image-20250807184307420.png)

最后允许有一个逗号

## 数组

```js
console.log(['Mike', 12]);
```

![image-20250807184351106](../../assetss/Day01-数据类型/image-20250807184351106.png)

最后允许有一个逗号

### null

也是一种对象类型常量

## undefined

```js
let x1 = null;
let x2 = NaN;
let x3;
console.log(typeof x1); // object
console.log(typeof x2); // number
console.log(typeof x3); // undefined
```

```js
console.log(null == undefined); // true
console.log(null === undefined); // false
console.log(null == NaN); // false
console.log(null === NaN); // false
console.log(undefined == NaN); // false
console.log(undefined === NaN); // false
```

使用`undefined`关键字**清空对象**

```js
let x = undefined;
```

## function

```js
const simpleTest = function () {
  console.log(typeof simpleTest); // function
};
```

## typeof 运算符

返回值总是字符串

```js
let value = '';
let type = typeof value;
let typeOfType = typeof type;
console.log(typeOfType === type); // t
```

而且如果是自定义的类, 似乎typeof的值都是`"object"`, String类型, 也是`"string"`而不是`"String"`

也就是说, typeof只会返回字符串: 

-   string
-   number
    -   NaN 也是 number
-   boolean
-   object
    -   null 也是 object
-   function
-   **undefined**

## constructor属性

### 判断类型

例如判断是否是数组

```js
function isArray(arr) {
  return arr !== undefined && arr != null && arr.constructor === Array;
}
```

### 实例化一个对象

实例化一个对象

```js
function instance(obj) {
  return new obj.constructor; // 等价于 new obj.constructor()
}
```

## 类型转换

-   String(Number|String|Boolean|Object)
-   Boolean(Number|String|Boolean|Object)
    -   0-> false
    -   NaN->false
    -   ""->false
    -   null->false
    -   undefined->false
-   Number(Number|String|Boolean|Object)
    -   Boolean  `flag?1:0`
-   parseFloat(String)
-   parseInt(String)
-   Object#toString

