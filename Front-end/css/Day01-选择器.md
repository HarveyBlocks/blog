# 选择器

CSS 通过**选择器**匹配需要被样式渲染的元素

## 标签选择器

直接用标签, 所有链接该CSS的所有标签都将被匹配

```css
span {
    color: black;
    font-size: 20px;
    font-family: "Times New Roman";
}
```

## id 选择器

>`#id`

HTML 中的任何标签都具有`id`属性, 依据这个属性来进行匹配

在html中设置id, id应当不以数字开头

```html
<span id="normal-text">文本</span>
```

CSS 中的选择器应当是`#id`的形式

```css
#normal-text {
    color: black;
    font-size: 20px;
    font-family: "Times New Roman";
}
```

## class 选择器

>   `.class`

类名不应以数字开头

```html
<span class="normal-text">文本</span>
```

```css
.normal-text {
    color: black;
    font-size: 20px;
    font-family: "Times New Roman";
}
```

下面的选择器, 将只匹配span元素中, 是normal-text类型的元素

```css
span.normal-text {
    color: black;
    font-size: 20px;
    font-family: "Times New Roman";
}
```

## 选择器通配符

```css
* {
   margin: 0;
   padding: 0;
}
```

表示所有元素

```css
div * {
   margin: 0;
   padding: 0;
}
```

表示 div 下的所有元素

## 组合选择符

-   ` ` 后代选择器
-   `>` 子元素选择器
-   `+` 相邻兄弟选择器
-   `~` 普通兄弟选择器
-   `, ` 两个独立的选择器共用一个样式

### 后代选择器

选取某元素的后代元素

后代, 即儿子+儿子的儿子+.....

```css
div p {
  background-color:yellow;
}
```

表示所有的`<div>`标签下的所有`<p>`标签被匹配

### 子元素选择器

>   Child selectors

只会匹配一级子元素

```css
div>p {
  background-color:yellow;
}
```

所有`<div>`的孩子`<p>`元素被匹配

### 相邻兄弟选择器

>   Adjacent sibling selector

匹配紧接在另一元素后的元素，且二者有相同父元素

```css
div+p {
  background-color:yellow;
}
```

### 普通兄弟选择器

匹配所有之后的兄弟元素

```css
div~p {
  background-color:yellow;
}
```

### 联合类选择器

- 选择器直接相连, 中间无空格
- 元素必须同时拥有所指定的类型
- 常用于组件的组合状态和样式复用

```css
.red {
    color: red;
}

.alert {
    /*随便选一个字体, 用于测试*/
    font-family: "Berlin Sans FB Demi", serif;
}

.alert.red {
    background-color: black;
}
```

```html
<body>
<h1 class="red">A red heading</h1>
<h1 class="alert">An alert</h1>
<h1 class="red alert">Red alert</h1>
</body>
</html>
```

## 伪类

>   Pseudo-classes

### 语法

```css
selector:pseudo-class {property:value;}
```

或带有类型的匹配

```CSS
selector.class:pseudo-class {property:value;}
```

### 伪类表

伪类的名称不区分大小写

| 选择器                 | 示例                  | 示例说明                                   |
| :--------------------- | :-------------------- | :----------------------------------------- |
| `:checked`             | input:checked         | 被选中的表单元素                    |
| `:disabled`            | input:disabled        | 被禁用的表单元素                    |
| `:enabled`             | input:enabled         | 被启用的表单元素                    |
| `:empty`               | p:empty               | 没有子元素的` <p>`元素            |
| `:first-of-type`       | p:first-of-type       | 在同级元素中的*第一个*` <p>`元素 |
| `:out-of-range`        | input:out-of-range    | 指定范围以外的值的元素属性             |
| `:in-range`            | input:in-range        | 元素指定范围内的值                     |
| `:invalid`             | input:invalid         | 无效的元素                         |
| `:last-child`          | p:last-child          | ` <p>`元素的最后一个子元素        |
| `:last-of-type`        | p:last-of-type        | 在同级元素中的*最后一个*` <p>`元素 |
| `:not(selector)`       | :not(p)               | ` <p>`以外的元素                  |
| `:nth-child(n)`        | p:nth-child(2)        | 同级元素中的*第二个*元素 |
| `:nth-last-child(n)`   | p:nth-last-child(2)   | 同级元素中的*倒数第二个*元素 |
| `:nth-of-type(n)`      | p:nth-of-type(2)      | 同级元素中的*第二个*` <p>`元素 |
| `:nth-last-of-type(n)` | p:nth-last-of-type(2) | 同级元素中的*倒数第二个*` <p>`元素 |
| `:only-of-type`        | p:only-of-type        | 仅有一个(` <p>`p&子)元素的` <p>`元素 |
| `:only-child`          | p:only-child          | 仅有一个子元素的` <p>`p元素        |
| `:optional`            | input:optional        | 没有"required"的元素属性               |
| `:read-only`           | input:read-only       | 只读属性的元素属性                     |
| `:read-write`          | input:read-write      | 没有只读属性的元素属性                 |
| `:required`            | input:required        | 有"required"属性指定的元素属性         |
| `:root`                | root                  | 文档的根元素                           |
| `:target`              | #news:target          | 当前活动#news元素(点击URL包含锚的名字) |
| `:valid`               | input:valid           | 所有有效值的属性                       |
| `:link`                | a:link                | 未访问链接                         |
| `:visited`             | a:visited             | 访问过的链接                       |
| `:active`              | a:active              | 正在活动链接                           |
| `:hover`               | a:hover               | 把鼠标放在链接上的状态                     |
| `:focus`               | input:focus           | 元素输入后具有焦点                     |
| `:first-letter`        | p:first-letter        | `<p>` 元素的第一个字母             |
| `:first-line` | p:first-line          | `<p>` 元素的第一行                      |
| `:first-child` | p:first-child         | 任意元素的(第一个子元素&` <p>`) 元素   |
| `:before`     | p:before              | 在每个`<p>`元素之前插入内容                     |
| `:after`       | p:after               | 在每个`<p>`元素之后插入内容                     |
| `:lang(*language*)` | p:lang(it)            | 为`<p>`元素的lang属性选择一个开始值             |

### 超链接的不同状态

```css
a:link {color:#FF0000;} /* 未访问的链接 */
a:visited {color:#00FF00;} /* 已访问的链接 */
a:hover {color:#FF00FF;} /* 鼠标划过链接 */
a:active {color:#0000FF;} /* 已选中的链接 */
```

`a:hover `必须被置于 `a:link` 和 `a:visited` 之后，才是有效的。

`a:active` 必须被置于 `a:hover` 之后，才是有效的。

### 不同语言下的引号

由于lang 可以在html的head中设置, 不止是可以在attribute上设置

```css
q:lang(en) {
    quotes: '"' '"';
}
q:lang(no) {
    quotes: "~" "~";
}

q:lang(cn) {
    quotes: "“" "”";
}

q:lang(jp) {
    quotes: "⌈" "⌋";
}
```

```html
<div>有一句叫做<q lang="en">识时务者为俊杰</q>的话</div>
<div>有一句叫做<q lang="no">识时务者为俊杰</q>的话</div>
<div>有一句叫做<q lang="cn">识时务者为俊杰</q>的话</div>
<div>有一句叫做<q lang="jp">识时务者为俊杰</q>的话</div>
```

![image-20250804204822938](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/css/Day01-选择器/image-20250804204822938.png)

### 表格斑马纹

在偶数行的颜色进行细微改变

使用伪类`nth-child`, 然后给定`even`偶数

```css
tr:nth-child(even) {background-color: #f2f2f2;}
```

![image-20250805203436758](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/css/Day01-选择器/image-20250805203436758.png)

## 伪元素

>   pseudo-element

### 语法

```css
selector::pseudo-element {
    property: value;
}
```

或联合类型

```css
selector.class::pseudo-element {
    property: value;
}
```

### 伪元素表

| 选择器                                                       | 实例            | 实例描述                      |
| :----------------------------------------------------------- | :-------------- | :---------------------------- |
| `::after`      | p::after        | 在元素之后插入内容 |
| `::before`    | p::before       | 在元素之前插入内容 |
| `::first-letter` | p::first-letter | 元素的首字母   |
| `::first-line` | p::first-line   | 元素的首行     |
| `::selection` | p::selection    | 选择用户选择的元素部分      |

### 将鼠标选中的部分变色

```css
::selection {
    color: red;
    background: yellow;
}
```

![image-20250804210229045](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/css/Day01-选择器/image-20250804210229045.png)

### 每段话结尾加上图片

```css
div.s::after {
    content: url("public/javascript.svg");
}
```

![image-20250804211633061](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/css/Day01-选择器/image-20250804211633061.png)

## 属性选择器

用于匹配属性, 或属性值满足某一条件的元素

-   **带有**某`attribute`属性的元素

    ```css
    selector[attribute] {
        property: value;
    }
    ```

-   `attribute`属性的值**等于**`value`的元素

    ```css
    selector[attribute="value"] {
        property: value;
    }
    ```

-   `attribute`属性的值**包含**`value` 的元素 

    ```css
    selector[attribute*="value"] {
        property: value;
    }
    ```

-   `attribute`属性的值**包含**`value`**单词**的元素

    ```css
    selector[attribute~="value"] {
        property: value;
    }
    ```

    会匹配 "flower"、"summer flower" 以及 "flower new"，但不匹配"my-flower" 或"flowers" 

-   `attribute`属性的值以`value`**为开头**的元素, 且**属性值后必须跟连字符**

    ```css
    selector[attribute|="value"] {
        property: value;
    }
    ```

    例如 值为 "value" 或 "value-a"

-   `attribute`属性的值以`value` **为开头**的元素 

    ```css
    selector[attribute^="value"] {
        property: value;
    }
    ```

-   `attribute`属性的值以`value` **为结尾**的元素 

    ```css
    selector[attribute$="value"] {
        property: value;
    }
    ```

