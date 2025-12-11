# 赋值兼容性

>   assignablity

类型兼容新基于赋值兼容性来实现, 包括extends和implement的类型兼容性

## 介绍

TypeScript里的类型兼容性是基于**结构类型**的, 与**名义（nominal）类型**不同

```ts
class Student {
    name: string;
}

class Teacher {
    name: string;
}

let p: Student = new Teacher();
```

这就是基于结构

C#/Cpp/Java就是基于名义

Typescript编译器检查`Student`中的每个属性，看是否能在`Teacher`中也找到对应属性

这个比较过程是递归进行的，检查每个成员及子成员

## 函数检查

检查函数使用相同规则

### 参数

```ts
let x: (a: number) => number = (b: number, s: string) => 0;// ERROR
let y: (b: number, s: string) => number = (a: number) => 0;// OK
```

右边的每个参数必须能在左边里找到对应类型的参数, 否则左边将永远无法提供实参值, 例如

```ts
items.forEach((item, index, array) => console.log(item));
// 赋值的右边缺少参数也应该是对的
items.forEach((item) => console.log(item));
```

### 返回值

```ts
let x: () => { a: number, b: number } = (): { a: number } => ({a: 0}); // ERROR
let y: () => { a: number } = (): { a: number, b: number } => ({a: 0, b: 1}); // OK
```

返回值可以多, 但不能少

### 参数双向协变

在比对每个函数参数类型时, 需要的参数能赋值到提供的参数, 或者提供的参数能赋值到需要的参数, 都算匹配

```ts
interface Person {
    name: string;
}

interface Student extends Person {
    grade: number;
}

interface Undergraduate extends Student {
    credit: number;
}

interface Graduate extends Student {
    salary: number;
}

let func1: (student: Student) => void = (stu: Person): void => {
};// OK

let func2: (student: Student) => void = (stu: Undergraduate): void => {
    console.log(stu.credit);
}; // OK
```

这么设计是因为这两种情况在Js中都可能发生

对于`func1`, 将来调用func1, 必须提供Student, 但只需要Person, 需要的数据比提供的参数少, 完全可以通过检查

对于`func2`, 将来调用func2, 可能提供Undergraduate, 也可能提供其他的Student, 但是需要Undergraduate是表达了一种"希望", 可能在运行的时候带来隐患, 但是能够在回调函数的函数体里更方便地进行对子类成员的调用

```ts
function hook(callback: (stu: Student) => void) {
    // callback作为hook的参数, hook会给callback传入参数
    // hook 传入的参数可能是Graduate, 也可能是Undergraduate
    // callback的提供者, 可以将Graduate作为参数, 也可以将Undergraduate作为参数
    // Typescript给了这样的自由
    let student1: Undergraduate = {name: "", grade: 0, credit: 0};
    let student2: Graduate = {name: "", grade: 0, salary: 0};
    callback(student1); // 产生隐患
    callback(student2);
}

hook((stu: Undergraduate): void => {
    // 表达了一种"希望", 表示这个callback是专门处理Undergraduate的
    // 接下来也能直接调用Undergraduate的成员了
    console.log(stu.credit);
});
```

在Java里一般是使用强制类型转换, 例如`HttpResponse`强转成`ServletHttpResponse`

对于Typescript来说, 也可以用断言表达类似强转的逻辑

```ts
hook((stu: Student): void => {
    console.log((<Undergraduate>stu).credit);
});
```

但是Typescript还是和Java不同, 检查较弱, 允许使用传入函数的参数是提供参数的子类, 于是就省去断言这一步了

### 可选参数和默认参数和不定参数

默认参数在必选参数之后的情况

```ts
let func = (a: number = 0, b?: string): void => {
    console.log(a);
    console.log(b);
};
let func1: () => void = func;
let func2: (a: number) => void = func;
// let func3: (b: string) => void = func; ERROR. 
// ``(a?: number,b?:string) => void` can not assign to ``(a: number) => void`
let func4: (a: number,b:string) => void = func;
let func5: (a: number,b?:string) => void = func;
// let func6: (a?: number,b:string) => void = func; ERROR. 可选不能在必选之前
let func7: (a?: number,b?:string) => void = func; // 最终形态
```

默认参数在必选参数之前的情况

```ts
let func = (a: number = 0, b: boolean, c?: string): void => {
    console.log(a);
    console.log(b);
    console.log(c);
};
// let func0: () => void = func;
// Type '(a: number, b: boolean, c?: string) => void' is not assignable to type '() => void'.
let func1: (a: number, b: boolean) => void = func;
let func2: (a: number, b: boolean, c: string) => void = func;
let func3: (a: number, b: boolean, c?: string) => void = func;
```

当一个函数有剩余参数时，它被当做无限个可选参数

### 函数重载

对于有重载的函数

参数的规则是, 赋值的左边表达参数列表的类型必须是每个重载参数列表的超集

返回值的规则是, 赋值的左边的返回值类型必须和重载列表返回值的并集有交集, 可以返回其中的一个, 可以不返回, 可以返回并集及其超集, 但不能返回不存在的

```ts
function pickCard(a: string): number;
function pickCard(b: number): string;
function pickCard(x: any): any {
    if (typeof x == "string") {
        return x.length;
    } else if (typeof x == "number") {
        return x + "";
    }
}

let func1: (m: string | number) => any = pickCard;
let func2: (m: string | number) => void = pickCard;
let func3: (m: string | number) => string = pickCard;
let func4: (m: string | number) => number = pickCard;
// let func5: (m: string | number) => boolean = pickCard; ERROR
```

## 枚举

-   枚举类型与数字类型兼容
-   数字类型与枚举类型兼容
-   不同枚举类型之间不兼容

## 类

-   比较两个类类型的对象时，只有实例的成员会被比较

-    静态成员和构造函数不比较

-   如果类型存在private或者protected成员, 则只有通过继承链才允许赋值

    ```ts
    class C {
    }
    class A extends C {
        private b: number;
    }
    
    class B {
        private b: number;
    }
    
    let a: A;
    let b: B;
    let c: C;
    b = a; // ERROR
    c = a;
    ```

## 泛型

```ts
interface Empty<T> {
}

let x: Empty<number>;
let y: Empty<string>;
x = y; // OK
```

允许, 因为TS的类型检查是基于结构的

对于未指定泛型类型的泛型参数时，会把所有泛型参数当成`any`比较

```ts
let identity = function<T>(x: T): T {
    // ...
}

let reverse = function<U>(y: U): U {
    // ...
}

identity = reverse; // OK
```





## any

允许any来回赋值

```ts
let a: any = "x";
let b: number = a;
```

