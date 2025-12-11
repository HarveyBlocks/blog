# 简介

## 安装

```shell
npm install -g typescript
```





## Hello World

```ts
console.log('Hello World')
```

编译

```ts
tsc greeter.ts
```

![image-20250827091825585](../../assetss/Untitled/image-20250827091825585.png)

编译结果是一个js文件





## 类型注解

一下是有类型错误的Typescript代码, 会报错

```ts
function greeter(person: string) {
    return "Hello, " + person;
}

let user = [0, 1, 2];

console.log(greeter(user));
```

![image-20250827092321011](../../assetss/Untitled/image-20250827092321011.png)



空参也会错误

![image-20250827092427643](../../assetss/Untitled/image-20250827092427643.png)

## 实质性的类型检查

typescript只会对实质上不同的类型发出警告

```typescript
class A { }

class B { }

let a: A = new B(); // 不会警告
```

```typescript
class A { }

class B {name:string="0" }

let a: A = new B(); // 不会警告
```

```typescript
class A {name:string="0" }

class B { }

let a: A = new B(); // 错误! A有, B无
```

你可以给多的, 但不能不给

在很多判断类型的地方都是如此

## TODO

-   Day3
    -   模块
    -   命名空间
    -   模块解析
    -   声明合并
-   Day04
    -   声明文件

