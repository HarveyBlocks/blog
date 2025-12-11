# 基础类型

-   boolean

-   number

-   string

-   数组

    -   表示为 `type[]`
    -   泛型表示 `Array<type>`
    -   元素同类型的数组

-   元组

    -   表示为`[type, ...]` 表示各个元素对应的类型

    -   元素不同类型的数组

    -   当访问一个越界的元素, 此元素被判定为*联合元素*

    -   如果要表示整个参数列表也是使用元组

        ```ts
        const callback1: (...args: [name: string, age: number]) => void = 
              function (name: string, age: number): void {
            console.log(`name: ${name}, age: ${age}`);
        };
        // c
        const callback2: (...args: [string, number]) => void = 
              function (name: string, age: number): void {
            console.log(`name: ${name}, age: ${age}`);
        };
        ```

-   枚举

-   any 任意类型

-   void 空类型, 一般用作函数返回值, 表示函数没有返回值

-   null/undefine 在typescript中作为类型, 而不仅仅是单例

    ```typescript
    let u: undefined = undefined;
    let n: null = null;
    ```

    -   默认情况下`null`和`undefined`是所有类型的子类型
    -   使用`--strictNullChecks`编译参数, null和undefined 只能赋值给自身的类型, 或者void, 用于检查错误

-   never 用于表示函数永不会到达终点, 例如死循环和抛出异常

    ```typescript
    // 返回never的函数必须存在无法达到的终点
    function error(message: string): never {
        throw new Error(message);
    }

    // 推断的返回值类型为never
    function fail() {
        return error("Something failed");
    }

    // 返回never的函数必须存在无法达到的终点
    function infiniteLoop(): never {
        while (true) {
        }
    }
    ```

-   object 以上基本类型不会被判断为object, 会判错

## 类型断言

断言在编译后被删去

断言只是表达一种"希望", 并不能检查语法, 也不能限制类型

它只是在断言类型了之后, 让后面的逻辑使用这个断言

### angle-bracket语法

```typescript
let someValue: any = "this is a string";

let strLength: number = (<string>someValue).length;
```

### as 语法

```typescript
let someValue: any = "this is a string";

let strLength: number = (someValue as string).length;
```

## 只读数组

只读数组, 去除了所有可变的方法, 确保数组创建后不被修改

在数组前加readonly, 构成新的类型

```typescript
let a: number[] = [1, 2, 3, 4];
let array: readonly number[] = a;
array[0] = 12; // error!
array.push(5); // error!
array.length = 100; // error!
a = array; // error! 不允许不可写的对象赋值给可写的对象
```

这种检查是typescript编译器进行的

```typescript
let a: number[] = [1, 2, 3, 4];
let array: readonly number[] = a;
a[0] = 12;
console.log(a); // [ 12, 2, 3, 4 ]
console.log(array); // [ 12, 2, 3, 4 ]
```

同时还有ReadonlyArray可以表示只读数组, 是一致的

```typescript
let a: number[] = [1, 2, 3, 4];
let array: ReadonlyArray<number> = a;
array[0] = 12; // error!
array.push(5); // error!
array.length = 100; // error!
a = array; // error! 
```

使用断言将只读变量赋值给可写变量

```typescript
let a: number[] = [1, 2, 3, 4];
let array: ReadonlyArray<number> = a;
a = array as number[];
```

### const 和 readonly

readonly检查是typescript编译器进行的, 而const是用JaveaScript解释器来判断的

readonly是对属性的只读, 而const是指对变量本身的只读

```typescript
let a: number[] = [1, 2, 3, 4];
let array: readonly number[] = a;
array = a;// 可以写变量, 而不能改成员属性/元素
```

而const没有让成员只读的能力, 而readonly能让成员只读

