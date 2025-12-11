# DOM

>   **D**ocument **O**bject **M**odel

DOM 是 W3C 定义的一套标准

用于 JavaScript 读写 HTML 文档的所有元素/CSS样式

当网页被加载时，浏览器创建页面的文档对象模型

## 对象树

![DOM HTML tree](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/js/front/Day03-DOM/pic_htmltree.gif)

## 方法

### getElementById 方法

### innerHTML 属性

用于获取或替换 HTML 元素的内容

## 文档元素

### 查找

| 方法                                    | 描述                          |
| :-------------------------------------- | :---------------------------- |
| document.getElementById(*id*)           | 通过元素 id 来查找元素        |
| document.getElementsByTagName(*name*)   | 通过标签名来查找元素          |
| document.getElementsByClassName(*name*) | 通过类名来查找元素            |
| document.querySelectorAll(*selector*);  | 通过CSS语法的选择器来查找元素 |

```js
let resourcePlace = document.getElementById('resource-place-2');
let pElements = resourcePlace.getElementsByTagName('p');
for (let pElement of pElements) {
  pElement.innerHTML = 'CONTENT';
}
```

`getElementsByTagName`是深度优先还是广度优先?

```js
let paragraphElements = document.getElementsByTagName('p');
for (let i = 0; i < paragraphElements.length; i++) {
  paragraphElements[i].innerHTML = i.toString();
}
```

![image-20250810001206336](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/js/front/Day03-DOM/image-20250810001206336.png)

**深度优先**

### 元素集合与节点列表

>   HTMLCollection NodeList

两者都可以用number作索引, 有length 属性

-   `getElementsByTagName() `方法返回 `HTMLCollection` 对象
-   `getElementsByClassName() `方法返回 `HTMLCollection` 对象
-    `getElementsByClassName() `方法，某些（老的）浏览器会返回 `NodeList` 对象
-   ` childNodes` 属性返回 `NodeList` 对象
-    `querySelectorAll()` 方法返回  `NodeList` 对象

`NodeList`存储`ChildNode`, `HTMLCollection`存储`HTMLElement`

`ChildNode`注重元素在文档中的位置

`HTMLElement` 注重文档自身的属性, 内部文本等信息

### 改变

| 属性                                       | 描述                   |
| :----------------------------------------- | :--------------------- |
| element.innerHTML = *new html content*     | 改变元素的 inner HTML  |
| element.attribute = *new value*            | 改变 HTML 元素的属性值 |
| element.setAttribute(*attribute*, *value*) | 改变 HTML 元素的属性值 |
| element.style.property = *new style*       | 改变 HTML 元素的样式   |

innerHTML 在改变的时候, 就会刷新整个document, 同时也会影响paragraphElements

```js
console.log('pushed');
let paragraphElements = document.getElementsByTagName('div');
let length = paragraphElements.length;
for (let i = 0; i < length; i++) {
  paragraphElements[i].innerHTML =
    i.toString() + paragraphElements[i].innerHTML + '<div></div>';
}

let paragraphElements2 = document.getElementsByTagName('div');
console.log(paragraphElements.length === paragraphElements2.length); // true
```

最终结果为true说明原paragraphElements已经被改变

原来:

![image-20250810003747644](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/js/front/Day03-DOM/image-20250810003747644.png)

之后:

![image-20250810003737329](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/js/front/Day03-DOM/image-20250810003737329.png)

### 添加和删除

| 方法                              | 描述             |
| :-------------------------------- | :--------------- |
| document.createElement(*tagName*) | 创建 HTML 元素   |
| document.removeChild(*element*)   | 删除 HTML 元素   |
| document.appendChild(*element*)   | 添加 HTML 元素   |
| document.replaceChild(*element*)  | 替换 HTML 元素   |
| document.write(*text*)            | 写入 HTML 输出流 |

### 添加事件处理程序

| 方法                                                     | 描述                            |
| :------------------------------------------------------- | :------------------------------ |
| document.getElementById(id).onclick = function(){*code*} | 向 onclick 事件添加事件处理程序 |

### DOM 属性

| 属性                         | 描述                                            |
| :--------------------------- | :---------------------------------------------- |
| document.anchors             | 返回拥有 name 属性的所有 `<a>` 元素。           |
| document.applets             | 返回所有` <applet> `元素（HTML5 不建议使用）    |
| document.baseURI             | 返回文档的绝对基准 URI                          |
| document.body                | 返回` <body>` 元素                              |
| document.cookie              | 返回文档的 cookie                               |
| document.doctype             | 返回文档的 doctype                              |
| document.documentElement     | 返回` <html> `元素                              |
| document.documentMode        | 返回浏览器使用的模式                            |
| document.documentURI         | 返回文档的 URI                                  |
| document.domain              | 返回文档服务器的域名                            |
| document.domConfig           | 废弃。返回 DOM 配置                             |
| document.embeds              | 返回所有 `<embed> `元素                         |
| document.forms               | 返回所有` <form>` 元素                          |
| document.head                | 返回 `<head>` 元素                              |
| document.images              | 返回所有 `<img>` 元素                           |
| document.implementation      | 返回 DOM 实现                                   |
| document.inputEncoding       | 返回文档的编码（字符集）                        |
| document.lastModified        | 返回文档更新的日期和时间                        |
| document.links               | 返回拥有 href 属性的所有 `<area>` 和 `<a>` 元素 |
| document.readyState          | 返回文档的（加载）状态                          |
| document.referrer            | 返回引用的 URI（链接文档）                      |
| document.scripts             | 返回所有 `<script>` 元素                        |
| document.strictErrorChecking | 返回是否强制执行错误检查                        |
| document.title               | 返回` <title> `元素                             |
| document.URL                 | 返回文档的完整 URL                              |

## 样式动画

### 样式

```js
document.getElementById(id).style.property = new style
```

```js
document.getElementById("p2").style.color = "blue";
```

### 动画

原理就是每隔5ms(`setInterval`)移动一点点的位置(依据style设置位置)

## 事件监听

给事件添加监听器

### 语法

添加

```js
element.addEventListener(event, action, useCapture);
```

-   event `string`
    -   change
    -   mouseover
    -   mouseout
    -   mousedown
    -   mouseup
    -   click
    -   keydown
    -   onload
-   action `function`
-   useCapture `bool`
    -   指定使用事件冒泡还是事件捕获
    -   此参数是可选的

删除

```js
element.removeEventListener("mousemove", action);
```

### 事件传播

>   冒泡和捕获

`<div>`元素中有`<p>`元素

`<div>`元素和`<p>`元素上都定义了`onclick`事件

那么当点击`<p>`元素的时候, 先触发`<div>`还是先触发`<p>`上的事件行为?

-   冒泡, 先触发内测元素(`<p>`), 再触发外侧元素(`<div>`)
-   捕获, 先触发外测元素(`<div>`), 再触发内侧元素(`<p>`)

## 导航

### 节点

-   整个文档是文档节点
-   每个 HTML 元素是元素节点
-   HTML 元素内的文本是文本节点
-   每个 HTML 属性是属性节点
-   所有注释是注释节点

![DOM HTML tree](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/js/front/Day03-DOM/pic_htmltree-17548146148672.gif)

### 节点关系

-   父、子和同胞，parent、child 以及 sibling
-   在节点树中，顶端节点被称为根/根节点
-   每个节点都有父节点，根节点没有父节点
-   节点能够拥有一定数量的子
-   同胞（兄弟或姐妹）指的是拥有相同父的节点

### 导航属性

使用节点属性在节点之间导航

-   `parentNode`
-   `childNodes[nodeIndex]`
-   `firstChild`
-   `lastChild`
-   `nextSibling`
-   `previousSibling`

### 节点值

`ChildNode.nodeValue`等价于`HTMLElement.innerHTML`

### 根节点

有两个特殊属性允许访问完整文档:

-   `document.body` - 文档的 body
-   `document.documentElement` - 完整文档

### 属性

#### nodeName

规定节点的名称。

-   nodeName 是只读的
-   元素节点的 nodeName 等同于标签名
-   属性节点的 nodeName 是属性名称
-   文本节点的 nodeName 总是 #text
-   文档节点的 nodeName 总是 #document

#### nodeValue

规定节点的值。

-   元素节点的 nodeValue 是 undefined
-   文本节点的 nodeValue 是文本文本
-   属性节点的 nodeValue 是属性值

#### nodeType

number

返回节点的类型

只读

| 节点               | 类型 | 例子                                 |
| :----------------- | :--- | :----------------------------------- |
| ELEMENT_NODE       | 1    | `<h1 class="heading">W3Schools</h1>` |
| ATTRIBUTE_NODE     | 2    | `class = "heading"` (弃用)           |
| TEXT_NODE          | 3    | `文本`                               |
| COMMENT_NODE       | 8    | `<!-- 这是注释 -->`                  |
| DOCUMENT_NODE      | 9    | `HTML 文档本身（<html> 的父）`       |
| DOCUMENT_TYPE_NODE | 10   | `<!Doctype html>`                    |

## 节点与元素

### 创建节点

创建元素para

创建文本节点

```js
var para = document.createElement("p");
var node = document.createTextNode("This is new.");
```

添加文本到元素

```js
para.appendChild(node);
```

添加元素到document

```js
var element = document.getElementById("div1");
element.appendChild(para);
```

### insertBefore

appendChild 将新元素作为父节点的最后一个孩子节点

```js
element.insertBefore(newElement, sibling);
```

### 删除当前节点

```js
element.remove();
```

可以先从文档中获取这个节点, 再删除这个节点

### 删除子节点

```js
parent.removeChild(child);
```

### 替换

`replaceChild()`

