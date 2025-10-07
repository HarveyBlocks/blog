# 多文件

## ES6

暴露代码

```js
export function greet(name) {
  return `Hello, ${name}!`;
}
```

导入

```js
import { greet } from './hello.js';
```

### import

```js
import defaultExport from "module-name";
import * as alias from "module-name";
import { export1 } from "module-name";
import { export1 as alias1 } from "module-name";
import { default as alias } from "module-name";
import { export1, export2 } from "module-name";
import { export1, export2 as alias2, /* … */ } from "module-name";
import { "string name" as alias } from "module-name";
import defaultExport, { export1, /* … */ } from "module-name";
import defaultExport, * as name from "module-name";
import "module-name";
```

-   `default as alias ` 表示在module中用`export default`的, 将被重命名为alias

-   defaultExport 表示引入用`export default`导出的

    也就是说

    ```js
    import foo from './value_from.js';
    ```

    和

    ```js
    import {foo} from './value_from.js';
    ```

    是有区别的, 在./value_from.js在以下情况下

    ```js
    export default function (msg) {
      console.log("Hi " + msg);
    }
    
    export function foo(msg) {
      console.log("Hello " + msg);
    }
    ```

    `import foo from './value_from.js';`无论如何都走的是`Hi`的逻辑



### 提升

导入的声明会被提升

在代码中间进行导入, 也会在全作用域有效

这个作用域指文件域 或 函数域, 而不是块

### export

导出声明

```js
export var|const|let|function|class identifier [....];
```

导出列表

```js
export { var_name1,var_name2, /* …, */ var_nameN };
export { variable1 as name1, variable2 as name2, /* …, */ nameN };
export { variable1 as "string name" };
export { var_name1 as default /*, … */ };
```

-   `export { var_name1 as default};` 与 `export default var_name1`等价
-   var_name1 表示变量, 已经在export语句外被声明

默认导出(**一个文件只允许一个默认导出**)

```js
export default var|const|let|function|class identifier [....];
export default function|class [....]; // 可以匿名
export default expreesion;
```

-   当import时不知道import了个啥时, 就会认为import了这个default

    ```js
    export default function foo(msg) {
      console.log("Hi " + msg);
    }
    ```

    ```js
    import foo from "./value_from.js";
    import not_foo from "./value_from.js";
    foo('A')
    not_foo('B')
    ```

    能正确执行

重导出/聚合

```js
export {name1,name2 } from "module-name";
export * from "module-name";
```

-   表示name1和name2不会在本模块中使用, 但是希望调用本模块的人能直接使用`module-name`中的name1和name2

-   `*` 表示任意`module-name` 中的导出的内容

-   凡是[import后面能跟的语句](#import), export 之后都能跟

    ```js
    export defaultExport from "module-name";
    export * as alias from "module-name";
    export { export1 } from "module-name";
    export { export1 as alias1 } from "module-name";
    export { default as alias } from "module-name";
    export { export1, export2 } from "module-name";
    export { export1, export2 as alias2, /* … */ } from "module-name";
    export { "string name" as alias } from "module-name";
    export defaultExport, { export1, /* … */ } from "module-name";
    export defaultExport, * as name from "module-name";
    // export "module-name"; 这个不行
    ```

    用这种方式导出的, 本模块并**不能**直接使用中继出去的声明, 必须在import一次





## CommonJS 模块

CommonJS是Node.js中常用的模块系统，通过 `require` 和 `module.exports` 语法来管理模块



暴露

```javascript
function greet(name) {
    return `Hello, ${name}!`;
}
module.exports = {
    greet
};
```

导入

```javascript
const { greet } = require('./hello.js');
console.log(greet('World'));  // Output: Hello, World!
```

## AMD

>   Acsynchronous Module Definition
>
>   异步模块定义

```js
require(['module1', 'module2'], function (module1, module2) {
// 模块加载完成后的回调函数
});
```

或者使用export和request

```js
export = MyModuleObject;
```

```js
import obj = require("./MyModuleObject");
```



## CMD

>   Common Module Definition

异步引入模块

依赖就近, 延迟执行

```ts
define(function (require, exports, module) {
var module1 = require('module1');
var module2 = require('module2');
// 模块代码
});
```







## **import()** 

-   ES2020引入的功能
-   它返回一个Promise

```javascript
try {
    const module = await import('./helper.js');
    console.log(module.greet('World'));  // Output: Hello, World!
} catch (error) {
    console.error('Error loading module:', error);
}
```

