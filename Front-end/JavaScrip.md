# JavaScrip

-   脚本语言
    -   直接翻译
    -   不需要编译
-   面向对象,跨平台
    -   Java也是面向对象,跨平台

## 引入方式

### 内部脚本

在Html页面里

用\<scripe>标签

-   没有数量限制

-   ```html
    <script>
        alert("Hello JavaScripe");
    </script>
    ```

    任何位置(\<body>里面不可,依次类推)

    -   一般放在\<body>后面

        因为从用户体验角度,先加载html的body再加载scripe更好

### 外部脚本

-   demo.js

```javascript
alert("Hello JavaScripe");//弹出警告窗
```

-   引入外部文件

![image-20231113185138492](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/JavaScrip/image-20231113185138492.png)

```html
<script src="testJS.js"></script>
```

![image-20231113185734025](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/JavaScrip/image-20231113185734025.png)

-   **这个自闭和是不会生效的!**

## 基础语法

-   区分大小写
-   分号可有可无
-   //单行注释,/\*多行注释\*/
-   {表示代码块}

### 输出语句

```js
window.alert("弹出警告框");
document.write("写入html");
console.log("写入浏览器控制台(F12)")
```

### 变量和数据类型

-   命名规则一样

-   命名习惯--驼峰命名法

-   弱类型语言(瞎搞)

    ```JS
    var test = 20;
    test = "张三";
    ```

### 变量作用域

#### var

-   无论如何都是全局
-   可以重复定义

#### let

-   Java型作用域
-   不可以重复定义

![image-20231113191204275](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/JavaScrip/image-20231113191204275.png)

-   **报错信息这里看的干活**

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/JavaScrip/image-20231113191341817.png" alt="image-20231113191341817" style="zoom:50%;" />

-   真是屌炸了

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/JavaScrip/image-20231113191719108.png" alt="image-20231113191719108" style="zoom:50%;" />

-   这也是不行的

### const表示常量

### 原始类型和引用类型

![image-20231113191559389](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/JavaScrip/image-20231113191559389.png)

![image-20231113191636543](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/JavaScrip/image-20231113191636543.png)

#### number

```js
var age = 20;
var price = 9.9;
alert(typeof age);//number
alert(typeof price);//number
```

-   NaN

    ```js
    alert(typeof +"num");//number
    alert(+"num");//NaN
    ```

#### string

```js
var ch = 'a';
var name = '张三';
var str = 'Hello World';
alert(typeof ch);//string
alert(typeof name);//string
alert(typeof str);//string
```

#### boolean

```js
var flag1 = true;
var flag0 = false;
alert(typeof flag1);//boolean
alert(typeof flag0);//boolean
```

#### null?

```
var obj = null;
alert(typeof obj);//object
```

-   JavaScripe的奇妙错误

#### 类型转换

-   其他类型转number

    -   string转number

        -   按照字符串的字面值转为数字
        -   如果字面值不是数字就转为**NaN**

        ```js
        alert(typeof +"num");//number
        alert(+"num");//NaN
        alert(parseInt("21")-1);//20
        ```

    -   boolean转number

        -   true->1
        -   false->0

        ```js
        alert(typeof +false);//number
        alert(+false);//0
        ```

-   其他类型转成boolean类型

    -   number

        -   NaN->false
        -   0->false
        -   其他->true

	-   string

        -   ""(空字符串)->false
        -   其他->true

    -   null->false

    -   undefine->false
    -   其他->true

    用处:

    ![image-20231113194717753](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/JavaScrip/image-20231113194717753.png)

    ![image-20231113194726287](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/JavaScrip/image-20231113194726287.png)

    简化了

### 运算符

-   自增自减
-   取余
-   与或非
-   条件运算符

### ===全等于运算符

```js
alert(20=="20");//true
```

-   判断类型是否一样
    -   不一样就尝试类型转换

```js
alert(20==="20");//false
```

-   判断类型是否一样
    -   不一样就返回false

### 控制语句

-   if-else

-   ```js
    switch(num){
        case 1:{
    		break;
        }
        default:{
    	}
    }
    ```

-   for(){}

-   while(){}

-   do{}while;

### 函数

```js
function 函数名(参数1,参数){//形参不需要语言,就是 var
    代码
    return 返回值;
}
```

```js
var 函数名 = function(参数1,参数2){
	return;
}
```

-   可以传任意参数的个数

-   ![image-20231113195800520](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/JavaScrip/image-20231113195800520.png)

-   输出都是3

    ```js
    alert(add(1));//NaN
    ```

## 常用对象

[JavaScript 参考手册](https://www.w3school.com.cn/jsref/index.asp)

### Array数组

![image-20231113201627399](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/JavaScrip/image-20231113201627399.png)

-   中括号!不是大括号!
-   变长变类型
-   有属性length:`arr.length`
-   添加元素`arr.push(20)`
-   删除元素`arr.splice(0,1)` 左闭右开

### String字符串

![image-20231113202356679](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/JavaScrip/image-20231113202356679.png)

-   去除前后空白字符`str4.trim()`

### 自定义对象

![image-20231113202746976](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/JavaScrip/image-20231113202746976.png)

## BOM对象

>   **B**rowser **O**bject **M**odel 浏览器对象模型

![image-20231113203322614](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/JavaScrip/image-20231113203322614.png)

### Window

![image-20231113203434829](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/JavaScrip/image-20231113203434829.png)

-   `conform`点确认返回true,点取消返回false

-   `setTimeout(函数,毫秒值)`时间间隔后,执行一次

-   `setInterval(函数,毫秒值)`时间间隔后,循环执行

    ```js
    setTimeout(function(){
    	alert("hi");
    },1000);
    ```

    定时器效果,轮流输出0和1

    ```JS
    setInterval(function(){
    	setTimeout(function(){
    		alert(0);
    	},1000);
    	setTimeout(function(){
    		alert(1);
    	},1000);
    },0);
    ```

    -   非常的折磨人

### History和Location

![image-20231113205030333](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/JavaScrip/image-20231113205030333.png)

![image-20231113205043873](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/JavaScrip/image-20231113205043873.png)

![image-20231113205128144](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/JavaScrip/image-20231113205128144.png)

## DOM对象

>   Document Object Model 文档对象模型

-   将标记语言的各个部分封装为对象
-   Document
-   Element
-   Attribute属性对象
-   Text
-   Comment

![image-20231113221442557](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/JavaScrip/image-20231113221442557.png)

![image-20231113221902768](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/JavaScrip/image-20231113221902768.png)

## 事件监听

Html事件是发送再Html的事情

例如**按钮被点击**,**鼠标移到AAA上了**

1.  检测
2.  执行代码

### 事件绑定

-   通过HTML标签中的事件属性进行绑定

    ```html
    <input type="button" onclick='on()'>
    ```

    ```js
    function on(){
        alert("点击!");
    }
    ```

    HTML和JS代码耦合

-   通过DOM元素属性绑定

    ```html
    <input type="button" id='btn'>
    ```

    ```js
    document.getElementById("btn").onclick=function(){
    	alert("点击!");
    }
    ```

    解耦合

### 常见事件

表单验证

onsubmit事件

## 案例:表单验证

要求:表单不符合要求,就不能submit

