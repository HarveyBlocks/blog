# Hoisting

-   JavaScript 将所有**声明**提升到当前作用域顶部的**默认行为**
-   之后声明会被提前, 而初始化不会被提前

## 声明提升

在使用`var`声明变量的时候

在使用变量之后对其进行声明

```js
function f1() {
  var x; // 声明 x
  x = 5; // 将 5 分配给 x
  console.log(x);
}

function f2() {
  x = 5; // 将 5 分配给 x
  console.log(x); // 在元素中显示 x
  var x; // 声明 x
}

function f3() {
  console.log(x); // 在元素中显示 x, undefined
  var x = 5; // 声明 x, 将 5 分配给 x
}

const simpleTest = f2;

```

f1和f2等价

## let 和 const

-   用 `let` 和 `const` 声明的变量被提升到块的顶部
-   从块的开始到声明，变量处于**暂时性死区**`temporal dead zone`
-   暂时性死区, 这些变量**可以写, 但不能读**
-   在声明之前读 `let` 或 `const` 变量将导致**引用错误**（`ReferenceError`）

## 严格模式

### 语法

在**脚本或函数**的**开头**添加`"use strict"` 定义该作用域下将使用"严格模式"

```js
"use strict";

function f(){
    "use strict";
}
```

### 严格模式的禁止事项

-   不允许在不声明变量的情况下使用变量

-   不允许删除变量/函数/对象

    ```js
    "use strict";
    var x = 3.14;
    delete x;                // 这将导致错误
    ```

-   不允许重复参数名

-   不允许八进制数值文本

-   不允许转义字符

-   不允许写入只读属性

    ```js
    "use strict";
    var obj = {};
    Object.defineProperty(obj, "x", {value:0, writable:false});
    
    obj.x = 3.14;            // 这将导致错误
    ```

-   不允许写入只能获取的属性

-   不允许删除不可删除的属性

-   不允许字符串 `eval` ,  `arguments` 用作变量

-   不允许with 语句

-   不允许 eval() 在其被调用的作用域中创建变量

-   在函数中使用this, 将返回 `undefined` 

-   

    