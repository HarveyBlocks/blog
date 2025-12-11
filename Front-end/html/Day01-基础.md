# 元素

-   起始标签
-   闭合标签
-   内容
-   属性

## Element

[HTML 标签简写及全称](https://www.runoob.com/html/html-tag-name.html)

-   body
-   p 段落
-   br 换行
-   hr 分割线
-   a 超文本链接

## 属性

| 属性名        | 适用元素                                          | 说明                                                         |
| :------------ | :------------------------------------------------ | :----------------------------------------------------------- |
| `id`          | 所有元素                                          | 为元素指定唯一的标识符。                                     |
| `class`       | 所有元素                                          | 为元素指定一个或多个类名，用于 CSS 或 JavaScript 选择。      |
| `style`       | 所有元素                                          | 直接在元素上应用 CSS 样式。                                  |
| `title`       | 所有元素                                          | 为元素提供额外的提示信息，通常在鼠标悬停时显示。             |
| `data-*`      | 所有元素                                          | 用于存储自定义数据，通常通过 JavaScript 访问。               |
| `onclick`     | 所有元素                                          | 当用户点击元素时触发 JavaScript 事件。                       |
| `onmouseover` | 所有元素                                          | 当用户将鼠标悬停在元素上时触发 JavaScript 事件。             |
| `onchange`    | 表单元素                                          | 当元素的值发生变化时触发 JavaScript 事件。                   |
| `readonly`    | 表单元素                                          | 使输入框只读。                                               |
| `required`    | 表单元素                                          | 指定输入字段为必填项。                                       |
| `disabled`    | 表单元素                                          | 禁用元素，使其不可交互。                                     |
| `href`        | `<a>`, `<link>`                                   | 指定链接的目标 URL。                                         |
| `src`         | `<img>`, `<script>`, `<iframe>`                   | 指定外部资源（如图片、脚本、框架）的 URL。                   |
| `alt`         | `<img>`                                           | 为图像提供替代文本，当图像无法显示时显示。                   |
| `type`        | `<input>`, `<button>`                             | 指定输入控件的类型（如 `text`, `password`, `checkbox` 等）。 |
| `value`       | `<input>`, `<button>`, `<option>`                 | 指定元素的初始值。                                           |
| `checked`     | `<input type="checkbox">`, `<input type="radio">` | 指定复选框或单选按钮是否被选中。                             |
| `placeholder` | `<input>`, `<textarea>`                           | 在输入框中显示提示文本。                                     |
| `target`      | `<a>`, `<form>`                                   | 指定链接或表单提交的目标窗口或框架（如 `_blank` 表示新标签页）。 |
| `autoplay`    | `<audio>`, `<video>`                              | 自动播放媒体。                                               |

### data-*

于存储自定义数据，通常通过 JavaScript 访问。

```
<div data-user-id="12345">User Info</div>
```

### 布尔属性

无值, 存在表示true, 不存在表示false

-   disabled
-   readonly
-   required
-   autoplay
    -   `<video>`/`audio`

例如`readonly`

```html
<input type="text" readonly>
```

### 事件处理

-   **onclick**
    -   点击元素时
-   **onmouseover**
    -   鼠标悬停时
-   **onchange**
    -   元素的值发生变化时

```html
<input type="text" onchange="alert('Value changed!')">
```

## 文本格式化

| 标签   | 描述         |
| :----- | :----------- |
| b      | 定义粗体文本 |
| em     | 定义着重文字 |
| i      | 定义斜体字   |
| small  | 定义小号字   |
| strong | 定义加重语气 |
| sub    | 定义下标字   |
| sup    | 定义上标字   |
| ins    | 定义插入字   |
| del    | 定义删除字   |

```html
<big>文本字体放大</big>
```

```html
<small>文本字体缩小</small>
```

```html
<b>加粗(blod)</b>
```

```html
<i>斜体(italic)</i>
```

```html
<strong>浏览器来决定要如何"强调", 浏览器来渲染决定, 一般是加粗</strong>
```

```html
<em>embrace强调, 同strong, 一般是斜体</em>
```

```html
<sub>下标</sub>
```

```html
<sup>上标</sup>
```

### 预格式

>   `<pre>`

对空行, 空格, 制表符保持渲染

```html
<pre>这是一段文本
多换行保留渲染
对空 格  保    持     渲染
对制表符	保持	渲染	啊
对一些特殊字符如>保持渲染, 但不建议
</pre>
```

### 缩写

>   `<acronym>`
>
>   `<abbr>`

指针移动到元素上会有提示

```html
<abbr title="这是一个问候">你好</abbr>
```

<abbr title="这是一个问候">你好</abbr>

其中, `acronym` 由于浏览器的更新换代, 已被弃用

### 删除和插入

```html
<del>blue</del> <ins>red</ins>
```

插入就是下划线

<del>blue</del> <ins>red</ins>

## img图像

还支持gif文件格式

### src属性和alt属性

都是必须属性

src链接到图片的URL

alt提示图片失效时的文本

```html
<img src="public/background.svg"
     alt="background图像失效"
     width="300" height="200">
<br>
<img src="public/background.svg2"
     alt="background图像失效"
     width="300" height="200">
```

![image-20250730151705619](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/html/Day01-基础/image-20250730151705619.png)

### 高度和宽度

>   width属性和height属性, 单位默认为像素

### 超链接

需求: 点击图片, 然后跳转超链接

实现方法: `<a>`标签内嵌套`<img>`标签

```html
<a href="https://www.w3schools.com/js/"><img src="..\assets\Day01-基础\javascript.svg" alt="图片失效"></a>
```

<a href="https://www.w3schools.com/js/"><img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/html/Day01-基础/javascript.svg" alt="图片失效" ></a>

### 图像对齐文本

>   style中使用vertical-align属性

```html
<img src="public\javascript.svg" alt="图片失效" style="vertical-align: bottom; ">文本
<br>
<br>
<img src="public\javascript.svg" alt="图片失效" style="vertical-align: top">文本
<br>
<br>
<img src="public\javascript.svg" alt="图片失效" style="vertical-align: middle">文本
```

![image-20250730155041374](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/html/Day01-基础/image-20250730155041374.png)

## 脚本

```html
<script>
document.write("Hello World!")
</script>
```

如果浏览器禁用了脚本

```html
<script>
document.write("Hello World!")
</script>
<noscript>的浏览器不支持 JavaScript!</noscript>
```

## 字符实体

[HTML 实体参考](https://www.runoob.com/tags/ref-entities.html)

在html中一些字符被预留, 例如`<` 和 `>`, 字符实体渲染这些字符

如需显示`<`：`&lt;` 或 `&#60;` 或 `&#060;`

