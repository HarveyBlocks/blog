# 布局

## 区块 `<span>` 和 `<div>`

-   块级元素
    -   通常会分行
    -   例如 `<h1>` `<p>` `<ul>` `<table>`
-   内联元素
    -   通常不会分行
    -   例如 `<b>` `<td>` `<a>` `<img>`
-   其中, `<div>`是**块级元素**, `<span>`是**内联元素**
-   区块 `<span>` 和 `<div>`  用作文本的容器, 没有特定的含义
-   `<div>`通常用于文档布局
-   `<table>`也可以用来布局, 但是不建议



## 布局

使用div布局

```css
#menu {
    background-color: rgba(255, 215, 0, 0.43);
    height: 200px;
    width: 100px;
    float: left;
}

#content {
    background-color: #8c8b8b;
    color: aliceblue;
    height: 200px;
    width: 400px;
    float: left;
}
```

```css
<div id="container" style="width:500px;background: rgba(255,165,0,0.33)">

    <div id="header">
        <h1 style="margin-bottom/*底部边距*/:0;">主要的网页标题</h1>
    </div>

    <div id="menu">
        <span style="font-weight: bold">菜单</span><br>
        <span>HTML</span><br>
        <span>CSS</span><br>
        <span>JavaScript</span>
    </div>

    <div id="content">
        <span>内容在这里</span>
    </div>

    <div id="footer" style="clear:both;text-align:center;">
        <span>脚标</span>
    </div>

</div>
```

