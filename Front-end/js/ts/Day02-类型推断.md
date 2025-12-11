# 类型推断

## 基础

```ts
let x = 3;// 类型判断: number
x = "3";// ERROR
```

## 最佳通用类型

考虑所有元素的类型

考虑所有的候选类型，最终给出一个兼容所有候选类型的类型

```ts
let x = [0, 1, null];
```

有`number`和`null`, 最终判断为`number[]`

但并不是这么智能

```ts
class Animal{}
class Lion extends Animal{}
class Elephant extends Animal{}
class Snake extends Animal{}
let zoo = [new Lion(), new Elephant(), new Snake()];
```

编译器不能判断出它们的父类是`Animal`, 只能认为是`(Lion|Elephant|Snake)[]`, 只能手动标记`Animal`

```ts
let zoo:Animal[] = [new Lion(), new Elephant(), new Snake()];
```

## 上下文类型

```ts
let consumer: (value: number) => number | null = null;
consumer = (n) => {
    return n.length; // Error, 没有length
}
```

依据consumer的类型, 推断出n的类型应该是number

```ts
let consumer: (value: number) => number | null = null;
consumer = (n:any) => {
    return n.length; // 不报错
}
```

标注类型any之后, 就不报错了

## 类型判断语句

>   extends ? :

判断是否是子类型, 而后给出其他类型

```typescript
type IsString<T> = T extends string ? "yes" : "no";

type A = IsString<string>; // "yes"
type B = IsString<number>; // "no"
type C = IsString<string | number>; // "yes" | "no" 联合类型会分发判断
```

用`infer`关键字从类型判断语句中定义某一符合类型的实体

`infer`用于让 TypeScript 自动推断一个类型并命名为

```typescript
type T1 = string extends infer S ? "yes" : "no"; // 永远都是"yes"
```

提出数组元素

```ts
type ArrayElement<T> = T extends Array<infer E> ? E : T; // 拆一波array的包装
type ArrayElement<T> = T extends (infer E)[] ? E : never;
```

提取参数列表

```ts
type T3<T> = T extends (...args: infer Args) => any ? Args : never; // 拆一波函数参数的包装
```

递归解除泛型封装

```ts
type UnwrapPromise<T> = T extends Promise<infer P> 
  ? UnwrapPromise<P>  // 如果 P 还是 Promise，递归解析
  : T;                // 否则直接返回
```

提取对象中某个属性的类型

```typescript
// 提取对象中某个属性的类型（支持一级属性）
type GetPropType<T, K extends string | number | symbol> = T extends { [key in K]: infer Prop } ? Prop : never;
```

递归提取深层属性

```typescript
type GetDeepProp<T, Path extends string | number | symbol> =
    Path extends `${infer Key}.${infer Rest}`
        ? GetDeepProp<GetPropType<T, Key>, Rest>  // 递归处理剩余路径
        : GetPropType<T, Path>;                   // 处理最后一级属性

// 提取深层属性类型
type UserNameType = GetDeepProp<User, "info.name">; // string
type UserAgeType = GetDeepProp<User, "info.age">; // number
```

拆分固定格式的字符串

```typescript
// 拆分 "key:value" 格式的字符串，提取 key 和 value
type SplitKeyValue<T> = T extends `${infer Key}:${infer Value}` ? { key: Key; value: Value } : never;

type Res1 = SplitKeyValue<"name:Alice">; // { key: "name"; value: "Alice" }
type Res2 = SplitKeyValue<"age:30">; // { key: "age"; value: "30" }
```

非空字符串类型

```ts
type NonEmptyString =
    string extends infer S ?
        S extends "" ? never : S
        : never;
```

