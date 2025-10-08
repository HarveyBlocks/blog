# this

-   在对象(对象或类的实例)的方法中，this 指的是方法所处的对象
-   在全局，this 指的是`[object Window]`
-   在函数中，this 指的是`[object Window]`
-   在函数中，严格模式下，this 是 undefined
-   在事件中，this 指的是接收事件的元素
-   像 call() 和 apply() 这样的方法可以将 this 引用到任何对象

## 常规函数和箭头函数的this的区别

-   常规函数的this指向函数的调用者
-   箭头函数的this指向函数的声明处
