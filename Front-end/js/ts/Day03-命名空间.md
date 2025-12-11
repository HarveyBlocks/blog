# 命名空间

Javascript原生的模块可以理解为外部模块, 命名空间就是内部模块

不要将命名空间出现以下情况

-   文件的顶层声明是`export namespace Foo { ... }` （删除`Foo`并把所有内容向上层移动一层）
-   多个文件的顶层具有同样的`export namespace Foo {` （不要以为这些会合并到一个`Foo`中！）

## 语法

使用export允许命名空间内部的元素能被外界访问

```ts
namespace ProxyAdvice {
    type ProxiedProps<T> = { /*...*/ } // 外界不可访问

    export type ProxiedObject<T> = { /*...*/ }

    export function proxy<T extends object>(o: T): ProxiedObject<T> { /*...*/ }

    export function unProxy<T>(t: ProxiedObject<T>): T { /*...*/ }

}
```

访问

```ts
let proxiedPerson: ProxyAdvice.ProxiedObject<Person> = ProxyAdvice.proxy(person);
```

## 原理

是用函数闭包, 把元素封装到一个对象里去, 语法类似Enum的编译

而且type, interface等不需要编译的, 直接去掉了

```ts
var ProxyAdvice;
(function (ProxyAdvice) {
    function proxy(o) { /*...*/ }
    ProxyAdvice.proxy = proxy;
    function unProxy(t) { /*...*/ }
    ProxyAdvice.unProxy = unProxy;// 就这样暴露给外界
})(ProxyAdvice || (ProxyAdvice = {}));
```

因此可以做很多逆天的操作

```ts
ProxyAdvice['proxy'] = function <T extends object>(o: T): ProxyAdvice.ProxiedObject<T> {
    return undefined;
}// 直接写
```

## 同名命名空间

同一个文件中

```ts
namespace Shapes {
    export class Circle { }
    export class Rectangle { }
}
namespace Shapes {
    export class Triangle { }
    export class Square { }
}
```

编译后

```ts
var Shapes;
(function (Shapes) {
    class Circle {
    }
    Shapes.Circle = Circle;
    class Rectangle {
    }
    Shapes.Rectangle = Rectangle;
})(Shapes || (Shapes = {}));
(function (Shapes) {
    class Triangle {
    }
    Shapes.Triangle = Triangle;
    class Square {
    }
    Shapes.Square = Square;
})(Shapes || (Shapes = {}));
```

也就是说, 同名命名空间中调用另一个命名空间的成员不加命名空间前缀

但是, ==即使是同一个命名空间, 也不能访问同名的另一个命名空间的不导出成员==

## 别名

```ts
namespace Shapes {
    export namespace Polygons {
        export class Triangle { }
        export class Square { }
    }
}

import polygons = Shapes.Polygons; // 取别名
let sq = new polygons.Square(); // new Shapes.Polygons.Square()
```

编译后是

```js
var polygons = Shapes.Polygons;
```

我认为还不如直接在ts

```ts
const polygons = Shapes.Polygons;
```

也可以命名别名

