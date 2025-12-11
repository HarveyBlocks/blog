# 高级类型

## Intersection Types

-   交叉类型, 将两个类型叠加到一种类型, 可以理解成多继承
-   `type & type`
-   交叉类型的对象是assignable to 其中一个类型的对象的
-   一个具有交叉类型的对象, 拥有这两个类型的成员的并集

一个用交叉类型实现的多继承的例子, 但是由于不允许用对象作为键, 因此会有报错(但是可以运行)

```ts
function set<U, T extends U>(lower: T, upper: U, id: Extract<keyof any, string>): void {
    let upperInLower: any = (<any>lower)[upper.constructor];
    if (!upperInLower) {
        (<any>lower)[upper.constructor] = upperInLower = {};
    }
    upperInLower[id] = (<any>upper)[id];
}

function extend<T, U>(first: T, second: U): T & U {
    let result = <T & U>{};
    for (let id in first) {
        (<any>result)[id] = (<any>first)[id];
    }
    for (let id in second) {
        if (result.hasOwnProperty(id)) {
            delete (<any>result)[id];
            set(result, first, id);
            set(result, second, id);
            console.warn("same member of " + id);
        } else {
            (<any>result)[id] = (<any>second)[id];
        }
    }
    return result;
}


class Student {
    constructor(public name: string, public grade: number) {
    }
}


class Teacher {
    constructor(public name: string, public salary: number) {
    }
}


let person = extend(new Student("A", 99), new Teacher("B", 10000));
console.log(person.grade) // 99
console.log(person.salary) // 10000
console.log(person.name) // undefined
console.log(person[Teacher].name) // B
console.log(person[Student].name) // A
```

## Union Types

-   联合类型
-   `type|type`
-   type中的任意一个assignable to 这个联合类型
-   一个具有联合类型的对象, 拥有这两个类型的成员的交集

```tsx
interface Bird {
    fly();
    layEggs();
}

interface Fish {
    swim();
    layEggs();
}

function getSmallPet(): Fish | Bird {
    // ...
}

let pet = getSmallPet();
pet.layEggs(); // okay
pet.swim();    // errors
```

## 类型保护

判断某一个对象是某一类型之后, 还要断言一下, 优点麻烦

类型保护就是在某些确定类型的情形下, 省却断言的步骤

### 类型谓词

```ts
interface AnyConstructor<T> {
    new(...args: any[]): T;
}

function isType<T>(obj: T, type: AnyConstructor<T>): obj is T {
    return obj&&obj.constructor === type;
}

console.log(isType(new Fish(), Fish));
```

其中的`obj is T`即类型为此

`obj` 需要来自参数(this也行, 可以认为this也在参数中)

`T` 一定要assignable to obj 的类型

在此判断之后使用此对象, 不需要断言也能检查出类型

```ts
interface AnyConstructor<T> {
    new(...args: any[]): T;
}

function isType<T>(obj: T, type: AnyConstructor<T>): obj is T {
    return obj.constructor === type;
}

class Fish {
    swim() {
        console.log("swim");
    }
}

let fish: any = new Fish();
if (isType(fish, Fish)) {
    fish.swim(); // OK
} else {
    // fish.swim(); ERROR
}
```

在联合类型下, 甚至能自动判断else分支下的情况

```ts
let pet: Fish | Bird = <Fish | Bird>(new Fish()); // 断言一下, 否则new Fish()太简单测试不出来

function isFish(pet: Fish | Bird): pet is Fish {
    return pet.constructor === Fish;
}

if (isFish(pet)) {
    pet.swim();
} else {
    pet.fly();
}
```

### typeof

传统的typeof只能返回字符串, typescript增强了typeof, 能够直接让分支里的代码直接识别**基本变量**的类型

```ts
function padLeft(value: string, padding: string | number) {
    if (typeof padding === "number") {
        return Array(padding + 1).join(" ") + value;
    } else if (typeof padding === "string") {
        return padding + value;
    }
    throw new Error(`Expected string or number, got '${padding}'.`);
}
```

支持typename

-   `"number"`

-   `"string"`

-   `"boolean"`

-   `"symbol"`

-   `"function"`

    ```ts
    let obj: Function = () => {
    };
    if (typeof obj === "function") {
        console.log("对的对的");
    }
    ```

-   否则, 不会被增强



### instanceof

instanceof是JavaScript的原生功能

由于接口不被编译, 因此instanceof的右边只能是constructor

```js
object instanceof constructor
```

但是有提供类型保护

```ts
class Person {
    constructor(public name: string = "P") {
    }
}

class Teacher extends Person {
    constructor(name: string = "T", public salary: number = 0) {
        super(name);
    }
}

class Student extends Person {
    constructor(name: string = "S", public grade: number = 0) {
        super(name);
    }
}

let obj: any = <any>(new Teacher());
if (obj instanceof Person) {
    console.log(obj.name);
    if (obj instanceof Teacher) {
        console.log(obj.salary);
    } else if (obj instanceof Student) {
        console.log(obj.grade);
    }
}
```

### 类型断言去除`null`和`undefined`

编译开启`--strictNullChecks`之后, 使用类型断言, 在变量后加上`!`, 表示**断言**这个对象不是`null`或`undefined`

```ts
function fixed(name: string | null): number {
    return name!.length/*OK*/ + name.length/*ERROR*/;
}
```

### 非0数

以此为例, 用于任何较为复杂的类型判断

```typescript
// 非0数类型
type NonZeroNumber = number & { readonly __nonZero: unique symbol /*有一个唯一标识, 这样就没有任何类能够事项一样的类型了*/ };
// 前面一个 number 是因为 arg is Type ,is 后面的Type 要 assignable to is 前面的参数arg的类型
// arg的类型是number, 那么 NonZeroNumber 应该 assignable to number

function isNonZeroNumber(n: number): n is NonZeroNumber {
	// 编写复杂逻辑
    return n !== 0;
}

// 使用示例
function divide(a: number, b: NonZeroNumber): number {
    return a / b;
}

const num1 = 10;
if (isNonZeroNumber(num1)) {
    // 仅通过类型保护才能进入
    divide(20, num1); // 编译通过
}

divide(20, 10); // ERROR, 普通数字不经过类型保护无法填入
```

逻辑复杂一些也可以实现"质数类"等等...

联合使用正则可以表示"符合某一正则的字符串类"

但其实没啥高大上的, 编译器只是做了两步

1.   要求你调用特定函数判断一下是否符合类型
2.   隐式地做了一下断言

## 类型别名

```ts
type TypeAlias = some_type;
```

```ts
type Name = string;
type NameResolver = () => string;
type NameOrResolver = Name | NameResolver;
function getName(n: NameOrResolver): Name {
    if (typeof n === 'string') {
        return n;
    }
    else {
        return n();
    }
}
```

起别名是对类型的引用, 而不是创建类型

别名也可以定义泛型

```ts
type Container<T> = { value: T };
```

允许在别名的定义中引用自己

```ts
type Tree<T> = {
    value: T;
    left: Tree<T>;
    right: Tree<T>;
}
type LinkedList<T> = T & { next: LinkedList<T> };
```

类型别名和interface一样不会被编译, 只是一个提示的作用

和interface的区别

-    `extends` 的`interface`通常比`&`+`alias` 性能更好

-   错误信息就不会使用别名, 而是显示引用目标

-   interface有声明合并, alias没有

    ```ts
    interface A {
        value: number;
    }
    
    interface A {
        msg: string;
    }
    
    let a: A = {
        msg: "msg",
        value: 1,
    };
    // 下面的两个TypeAlias是Error
    type B = {
        value: number;
    }
    
    type B = {
        msg: string;
    }
    ```

## 字面量类型

### 字符串

```ts
function animate(easing: "ease-in" | "ease-out" | "ease-in-out") {
    if (easing === "ease-in") {
        // ...
    } else if (easing === "ease-out") {
    } else if (easing === "ease-in-out") {
    } else {
    }
}

animate("ease-in"); // OK
animate("uneasy"); // ERROR
animate(null); // OK
```

可以作为简单的枚举, 而且这种类型不会被编译

还可以作为函数重载

```ts
function animate(easing: "ease-in"): void;
function animate(easing: "ease-out"): void;
function animate(easing: "ease-in-out"): void;
function animate(easing: string): void {
    // ...
}

animate("ease-in"); // OK
animate("uneasy"); // ERROR
animate(null); // OK
```

### 数字

数字字面量和字符串字面量可以混用

```ts
function toBoolean(value: 0 | 1 | "true" | "false"): boolean {
    if (value === 0 || value === "false") {
        return false;
    } else if (value === 1 || value === "true") {
        return true;
    } else {
        return undefined;
    }
}
```

## 单例类型

-   Enum
-   number字面量
-   string字面量

## Discriminated Unions

可辨识联合/*标签联合*/*代数数据类型*

1.  都具有一个**单例类型的属性**— *可辨识的特征*。
2.  多个含有可辨识特征的类型的Union
3.  此属性上的类型保护

```ts
interface Square {
    kind: "square"; // 单例特征
    size: number;
}
interface Rectangle {
    kind: "rectangle"; // 单例特征
    width: number;
    height: number;
}
interface Circle {
    kind: "circle"; // 单例特征
    radius: number;
}
function area(s: Square | Rectangle | Circle) {
    switch (s.kind/*Union能够筛选出统一的成员*/) {
        case "square": return s.size * s.size;// OK, 产生了类型保护
        case "rectangle": return s.height * s.width;// OK, 产生了类型保护
        case "circle": return Math.PI * s.radius ** 2;// OK, 产生了类型保护
    }
}
```

如果要增加一个类型, 而开启 `--strictNullChecks`之后, 就会在返回值类型上显示异常(没有default, 但是case包含全部的情况, 也不会在返回值类型上报错)

```ts
type Shape = Square | Rectangle | Circle | Triangle;
function area(s: Shape):number/*ERROR, 没有Triangle的case*/ {
    switch (s.kind) {
        case "square": return s.size * s.size;
        case "rectangle": return s.height * s.width;
        case "circle": return Math.PI * s.radius ** 2;
    }
}
```

如果异常发生在return上, 异常发生在函数体里的哪里都有可能

使用never来检查, 这种就直接显示在switch语句里面了, 也就是说错误多半就是在switch这里发生的了

```ts
type Shape = Square | Rectangle | Circle | Triangle;
function assertNever(x: never): never {
    throw new Error("Unexpected object: " + x);
}
function area(s: Shape) {
    switch (s.kind) {
        case "square": return s.size * s.size;
        case "rectangle": return s.height * s.width;
        case "circle": return Math.PI * s.radius ** 2;
        default: return assertNever(s); // Error, 因为Triangle不能赋值给never
    }
}
```

## 多态的this返回值

```ts
class BasicCalculator {
    public constructor(protected value: number = 0) { }
    public currentValue(): number {
        return this.value;
    }
    public add(operand: number): this {
        this.value += operand;
        return this;
    }
}
class ScientificCalculator extends BasicCalculator {
    public constructor(value = 0) {
        super(value);
    }
    public multiply(operand: number): this {
        this.value *= operand;
        return this;
    }
}


let value = new ScientificCalculator(2)
    .add(1) // 返回的是子类, 说明在继承的过程中, 声明的返回值this也变成子类的this了
    .multiply(5)
    .currentValue();
```

## Index Types

将对象的字段名都集合在一起, 组成的一个类型

```ts
keyof type;
```

其等效于所有字段的[常量字面量](#字面量类型)的Union集合(完全等效)

```ts
interface Person {
    name: string;
    age: number;
}

let key1: keyof Person = "name";
let key2: keyof Person = "age";
let key3: keyof Person = "";//Error
let key4: keyof Person = "?";//Error
```

同时还可以用

```ts
type[keyof type];
```

表示值的集合的类型

```ts
interface Person {
    name: string;
    age: number;
}

let p: Person[keyof Person] = "";
let x: string | number = "";
p = x;
x = p;
```

### 索引类型和索引签名

索引签名表示了**一系列**字段的类型, 当一个含有索引字段的类型, 使用keyof之后, 获取的类型集合, 可以认为是无穷多字段名, 字符串字面量的集合, 就是字符串本身

```ts
interface StringMap {
    [key: string]: number;
}

let keys: keyof StringMap; // string
let values: StringMap['key...']; // number
```







### 动态获取字段及其值

可以用来动态的, 带检查地, 用字段获取值

```ts
function get<T, K extends keyof T>(o: T, field: K): T[K] {
    return o[field];
}

function pluck<T, K extends keyof T>(o: T, fields: K[]): T[K][] {
    return fields.map((field: K): T[K] => get(o, field));
}

```

测试使用一下两个API

```ts
let strings: string[] = pluck(person, ['name']); // ok
let numbers: number[] = pluck(person, ['age']); // ok
let members1: (number | string)[] = pluck(person, ['age', 'name']); // ok
let members2: Person[keyof Person][] = pluck(person, ['age', 'name']); // ok
let members3: never[] = pluck(person, []); // ok
```

## 映射类型

$$
\begin{array}{l}
map_{type\_map} → type \space\space typeIdentifier \space = \space \{\\
\space\space\space\space\space\space\space\space[identifier \space in \space type_{union}]: type; 
\\
\text{\}}\\
\\
type_{union} → string|number|字面量|type_{union}
\end{array}
$$

必须在type alias 的定义中声明

和索引签名有点像, 也是用一个声明语句表示一类字段

```ts
type Student = {
    [P in "id" | "grade" | "age"]: number;
}
let s: Student = {
    id:1,
    grade:1,
    age:1,
};
```

in后面的集合会遍历一次绑定到每个属性



### 只读封装和可选封装

如果要把所有字段都封装成只读的, 或者可选的

```ts
type ReadonlyWarp<T> = {
    readonly [P in keyof T]: T[P];
}
type PartialWarp<T> = {
    [P in keyof T]?: T[P];
}
```

使用

```ts
type Person = {
    name: string;
    age: number;
}
type ReadonlyPartialPerson = ReadonlyWarp<PartialWarp<Person>>
let p: ReadonlyPartialPerson = {name: "n",/*age: 12 可选*/};
console.log(p.age);
// p.age = 2; ERROR
```

由于映射类型的签名和泛型T所指代的类型是*同态*的, 映射只作用于T

因此编译器会拷贝原有的**属性修饰符**, 即原来有readonly修饰后也会有readonly, partial 亦如此

无法unwarp readonly

```ts
type ReadonlyUnwarp<T> = {
    [P in keyof T]: T[P];
}
type Person = {
    readonly name: string;
    readonly age: number;
}
let p: ReadonlyUnwarp<Person> = {name: "n", age: 12};
// p.age = 33; ERROR
```

无法unrawp partial

```ts
type PartialUnwarp<T> = {
    [P in keyof T]: T[P];
}
type Person = {
    name?: string;
    age?: number;
}
let p: PartialUnwarp<Person> = {name: "n"};
```

### 封装proxy

```ts
type ProxiedProps<T> = {
    get(): T;
    set(value: T): void;
}
type ProxiedObject<T> = {
    [P in keyof T]: ProxiedProps<T[P]>;
}

function proxy<T extends object>(o: T): ProxiedObject<T> {
    type K = keyof T;
    let result: ProxiedObject<T> = <ProxiedObject<T>>{};
    for (let field of Object.keys(o)) {
        let _value: T[K] = o[field];
        result[field] = {
            get: (): T[K] => {
                console.log(`get ${field} = ${_value}`);
                return _value;
            },

            set: (value: T[K]) => {
                console.log(`set ${field} from ${_value} to ${value}`);
                _value = value;
            }
        };
    }
    return result;
}

function unProxy<T>(t: ProxiedObject<T>): T {
    let result = {} as T;
    for (const k in t) {
        result[k] = t[k].get();
    }
    return result;
}
// ----测试----
type Person = { name: string, age: number };
let person = {name: "A", age: 12};
let proxiedPerson: ProxiedObject<Person> = proxy(person);
console.log(proxiedPerson.name.get()/*get name = A*/);// A
proxiedPerson.name.set("B") // set name from A to B
console.log(proxiedPerson.name.get()/*get name = B*/); // B
person.name = "C";
console.log(proxiedPerson.name.get()/*get name = B*/); // B
```

实现vue的ref, 往get和set里面加plugin....



### 标准库中的映射

TypeScript的标准库

```ts
type Pick<T, K extends keyof T> = {
    [P in K]: T[P];
}
type Record<K extends string, T> = {
    [P in K]: T;
}
type Readonly<T> = {
    readonly [P in keyof T]: T[P];
}
type Partial<T> = {
    [P in keyof T]?: T[P];
}
```



```ts
let x: Record<"a" | "b" | "c", number> = {
    a: 1,
    b: 2,
    c: 3,
}
```

Record 不是同态的, 因此不会拷贝属性修饰符

```ts
type Person = {
    readonly name: string;
    readonly age: number;
}
let p: Record<keyof Person, string | number> = {
    name: "",
    age: 2
}
p.age = 3;//OK
```

## 预定义的有条件类型

-   `Exclude<T, U>` -- 从`T`中剔除可以赋值给`U`的类型
-   `Extract<T, U>` -- 提取`T`中可以赋值给`U`的类型
-   `NonNullable<T>` -- 从`T`中剔除`null`和`undefined`
-   `ReturnType<T>` -- 获取函数返回值类型
-   `InstanceType<T>` -- 获取构造函数类型的实例类型

[类型推断和截取](Day02-类型推断.md#类型判断语句)

