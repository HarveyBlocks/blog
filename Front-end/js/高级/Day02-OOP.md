# OOP

>   面向对象
>
>   ES6 的 `class` 语法反而只是这个原生原型机制的 “语法糖”—— 它让写法更接近传统 OOP，但底层运行逻辑依然是原型链的查找和继承

```js
class Obj {
  constructor(a, b) {
    this.f1 = a;
    this.f2 = b;
  }

  method() {}

  static staticMethod(x) {
    return new Obj(x, x);
  }

  /*getter*/
  get sum() {
    return this.f1 + this.f2;
  }

  /*setter*/
  set valueA(a) {
    this.f1 = a;
  }
}

function f2() {
  let data = Obj.staticMethod(3);
  console.log(data);
  console.log(data.sum); // 6
  console.log(data.valueA); // undefined
  data.valueA = 2;
  console.log(data.sum); // 5
}

const simpleTest = f2;
```

## 基础

```js
let data = Obj.staticMethod(3);
data.AAA = 2;
console.log(data.AAA);
```

### 构造器

在Javascript中, 对于一个类型, 直接传入, 哪就是这个类型的构造器

```js
class A{}

console.log(A); // [class A]
console.log(typeof A); // function
```



## 访问控制

-   private
-   public
-   protected





## 继承

```js
class Sun extends Obj {
  constructor(a, b, c) {
    super(a, b);
    this.c = c;
  }

  get sum() {
    return super.sum + this.c;
  }
  
}
```

## 抽象类

有抽象类和抽象方法



## Object 类方法



|函数签名|描述|
|:---------|:---------|
|Object.defineProperty(object, property, descriptor) | 添加或更改对象属性|
|Object.defineProperties(object, descriptors) | 添加或更改许多对象属性 |
|Object.getOwnPropertyDescriptor(object, property) |   访问属性 |
|Object.getOwnPropertyNames(object) |  以数组的形式返回所有属性 |
|Object.keys(object) |  以数组的形式返回可枚举属性 |
|Object.getPrototypeOf(object) | 访问原型 |
|Object.preventExtensions(object) | 防止向对象添加属性 |
|Object.isExtensible(object) | 如果属性可以添加到对象，则返回 true |
|Object.seal(object) | 防止更改对象属性（不是值） |
|Object.isSealed(object) |  如果对象是密封的，则返回 true |
|Object.freeze(object) | 防止对对象进行任何更改 |
|Object.isFrozen(object)  |  如果对象被冻结，则返回 true |

