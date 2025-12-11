# 响应式布局

>   **R**esponsive **W**eb **D**esign

允许 Web 页面适应不同屏幕宽度因素等，进行布局和外观的调整的一系列实践

## 响应式设计

Ethan Marcotte 在 2010 年首度提出的，他将其描述为三种技术的混合使用。

1.  **液态网格**
2.  **液态图像**
    -   将设置`max-width`属性设置为`100%`
    -   图像可以在包含它们的列变得比图像原始尺寸窄的时候，缩放得更小
    -   总不会变得更大
    -   图像可以被缩放，以被放到一个灵活尺寸的列，而不是溢出出去
    -   不会使图像变得太大以至于画质变得粗糙
3.  媒体查询

## 设计准则

用户习惯在台式机和移动设备上垂直滚动网站，而不是水平滚动

因此，如果迫使用户水平滚动或缩小以查看整个网页，则会导致不佳的用户体验

1.   不要使用较大的固定宽度元素

     比如, 如果图像的宽度大于视口的宽度，则可能导致视口水平滚动

     务必调整此内容以适合视口的宽度

2.   不要让内容依赖于特定的视口宽度来呈现好的效果

     因为以 CSS **像素计的屏幕尺寸**和宽度在设备之间变化很大

3.   使用 CSS **媒体查询**为小屏幕和大屏幕应用不同的样式

     -   为页面元素设置较大的 CSS 绝对宽度将导致该元素对于较小设备上的视口太宽

     -   **应该考虑使用相对宽度值，例如 width: 100%**
     -   要小心使用较大的绝对定位值，这可能会导致元素滑落到小型设备的视口之外

## 视口

>   viewport

HTML5 引入

```html
<meta name="viewport" content="width=device-width, initial-scale=1.0">
```

-   `width=device-width` 

    将页面的宽度设置为**跟随设备**的屏幕宽度

-   `initial-scale=1.0`

    用于在浏览器首次加载页面时，设置初始缩放级别

## 网格

>   grid-view

页面被分割为几列, 用于更好地布局

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/css/Day04-响应式布局/image-20250811231659388.png" alt="image-20250811231659388" style="zoom:33%;" />

响应式网格视图通常有 12 列

### 制作网格

1.   确保所有 HTML 元素的 box-sizing 属性设置为 border-box

     ```css
     * {
       box-sizing: border-box;
     }
     ```

2.   分割页面

     ```css
     .col-1 {width: 8.33%;}
     .col-2 {width: 16.66%;}
     .col-3 {width: 25%;}
     .col-4 {width: 33.33%;}
     .col-5 {width: 41.66%;}
     .col-6 {width: 50%;}
     .col-7 {width: 58.33%;}
     .col-8 {width: 66.66%;}
     .col-9 {width: 75%;}
     .col-10 {width: 83.33%;}
     .col-11 {width: 91.66%;}
     .col-12 {width: 100%;}
     ```

3.   所有这些列应向左浮动

     ```css
     [class*="col-"] {
       float: left;
       padding: 10px;
       border: 1px solid red; /*方便测试查看*/
     }
     ```

4.   元素都应被包围在 `<div>` 中。行内的列数总应总计为 12

     ```html
     <div class="row">
       <div class="col-3">...</div> <!-- 占用3格 -->
       <div class="col-9">...</div> <!-- 占用9g -->
     </div>
     ```

5.   行内的所有列全部都向左浮动，因此会从页面流中移出，并将放置其他元素，就好像这些列不存在一样

     比如

     ```html
     <div class="row">
       <div class="col-9">大量文本</div> <!-- 占用3格 -->
       <div class="col-3">少量文本</div> <!-- 占用9g -->
     </div>
     <p>
         大量文本
     </p>
     ```

     <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/css/Day04-响应式布局/image-20250811233942322.png" alt="image-20250811233942322" style="zoom: 40%;" />

     希望结构是:

     <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/css/Day04-响应式布局/image-20250811234225607.png" alt="image-20250811234225607" style="zoom:50%;" />

     在`class='raw'`的元素上**添加清除流的样式**, 然后将放在`class='col-*'`上的元素放入`class='raw'`中

     ```css
     .row::after {
       content: "";
       clear: both;
       display: table;
     }
     ```

### 网格布局模块

CSS Grid Layout Module 提供了带有行和列的基于网格的布局系统，而无需使用浮动和定位

 **display 属性设置为 `grid` 或 `inline-grid`** 时，它就会成为网格容器

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <style>
        .grid-container {
            display: grid;
            grid-template-columns: auto auto auto;
            background-color: #2196F3;
            padding: 10px;
        }
        .grid-item {
            background-color: rgba(255, 255, 255, 0.8);
            border: 1px solid rgba(0, 0, 0, 0.8);
            padding: 20px;
            font-size: 30px;
            text-align: center;
        }
    </style>
</head>
<body>
<div class="grid-container">
    <div class="grid-item">1</div>
    <div class="grid-item">2</div>
    <div class="grid-item">3</div>
    <div class="grid-item">4</div>
    <div class="grid-item">5</div>
    <div class="grid-item">6</div>
    <div class="grid-item">7</div>
    <div class="grid-item">8</div>
    <div class="grid-item">9</div>
</div>
</body>
</html>
```

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/css/Day04-响应式布局/image-20250812011109826.png" alt="image-20250812011109826" style="zoom:30%;" />

### 各组成

-   网格列 Grid Cols

    <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/css/Day04-响应式布局/grid_columns.png" alt="img" style="zoom:67%;" />

-   网格行 Grid Rows

    <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/css/Day04-响应式布局/grid_rows.png" alt="img" style="zoom:67%;" />

-   网格间隙 Grid Gaps

    -   `grid-column-gap`
    -   `grid-row-gap`
    -   `grid-gap`

    <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/css/Day04-响应式布局/grid_gaps.png" alt="img" style="zoom: 67%;" />

-   网格行Grid Lines

    -   `grid-column-start`
    -   `grid-column-end`
    -   `grid-row-start`
    -   `grid-row-end

    <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/css/Day04-响应式布局/grid_lines.png" alt="img" style="zoom:67%;" />

    把网格项目放在列线 1，并在列线 3 结束它

    ```css
    .item1 {
      grid-column-start: 1;
      grid-column-end: 3;
    }
    ```

    <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/css/Day04-响应式布局/image-20250812011657185.png" alt="image-20250812011657185" style="zoom: 33%;" />

### 网格容器

在网格容器中把 display 属性设置为 grid 或 inline-grid

#### grid-template-columns

属性定义网格布局中的列数，并可定义每列的宽度

如果希望设置n列, 则设置n个值

如果所有列都应当有相同的宽度，则设置为 "auto"

否则, 指定各个值

```css
.grid-container {
  display: grid;
  grid-template-columns: auto auto auto auto;
}
```

或者

```css
.grid-container {
  display: grid;
  grid-template-columns: 80px 200px auto 40px;
}
```

下面就是左侧导航栏固定宽度, 右侧内容栏自动改变长度

<video src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/css/Day04-响应式布局/演示column布局.mp4"></video>

#### grid-template-rows

设置行宽度

```css
.grid-container {
  display: grid;
  grid-template-rows: 80px 200px;
}
```

#### justify-content

在容器内**水平**对齐整个网格

| 值            | 描述                                  | 详细解释                                                   |
| :------------ | :------------------------------------ | ---------------------------------------------------------- |
| flex-start    | 默认值。项目位于容器的开头            | 项目之间的空间保留和行一样的空间, 项目尽可能往左边         |
| flex-end      | 项目位于容器的结尾                    | 项目之间的空间保留和行一样的空间, 项目尽可能往右边         |
| center        | 项目位于容器中央.                     | 项目之间的空间保留和行一样的空间, 项目尽可能往中间         |
| space-between | 项目在行与行之间留有间隔.             | 最左和最右边只保留和行一样的空间, 而项目之间的空间保持一致 |
| space-around  | 项目在行之前、行之间和行之后留有空间. | 项目A 和 项目B 之间的空间是最左边或者最右边的两倍          |
| space-evenly  | 列之间和它们周围提供相等的空间量.     | 项目A 和 项目B 之间的空间等于最左边或者最右边空间          |

==网格的总宽度必须小于容器的宽度，这样 justify-content 属性才能生效==

#### align-content

在容器内**垂直**对齐整个网格

值同`justify-content`

### 网格项目

网格容器的子元素

默认情况下，容器在每一行的每一列都有一个网格项目

可以设置网格项目的样式，让它们**跨越多个列和/或行**

#### grid-column

>   grid-column 属性是 grid-column-start 和 grid-column-end 属性的简写属性

定义将项目放置在哪一列上

引用列号（line numbers）来跨越列, 列号1开始, 到列号5结束

```css
.item1 {
  grid-column: 1 / 5;
}
```

使用关键字 "span" 来跨越列, 列号1开始, 跨越三个项目

```css
.item1 {
  grid-column: 1 / span 3;
}
```

#### grid-row

同理 grid-column

#### grid-area

>   用作 grid-row-start、grid-column-start、grid-row-end 和 grid-column-end 属性的简写属性

```css
.item8 {
  grid-area: 1 / 2 / 5 / 6;
}
```

-   row-line-start 1
-   column-line-start 2
-   row-line-end 5
-   column-line-end 6

```css
.item8 {
  grid-area: 1 / 2 / span 2 / span 3;
}
```

-   row-line-start 1
-   column-line-start 2
-   row-line 跨越 2
-   column-line  跨越 3

#### 顺序

使用`grid-column`, `grid-raw`, `grid-area`等属性, 可以让任何项目出现在容器的任何位置

这常常用来在不同媒体下设置不同的布局

#### grid-area和命名

在项目中式样`grid-area`来命名

在网格容器中的属性`grid-template-areas`依照项目的`grid-area`来布局

布局语句用`''`包围

 在`grid-template-areas`中用`.`来表示未命名的项目, 用于占位

```css
.grid-container {
    display: grid;
    grid-template-areas: 'item1 item1 . . .' 'item1 item1 . . . ' '. . . . .';
    background-color: #2196F3;
    padding: 10px;
}

div[class*='grid-item'] {
    background-color: rgba(255, 255, 255, 0.8);
    border: 1px solid rgba(0, 0, 0, 0.8);
    padding: 20px;
    font-size: 30px;
    text-align: center;
}

.grid-item-1 {
    grid-area: item1;
}
```

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/css/Day04-响应式布局/image-20250812025509386.png" alt="image-20250812025509386" style="zoom:30%;" />

```css
.item1 { grid-area: header; }
.item2 { grid-area: menu; }
.item3 { grid-area: main; }
.item4 { grid-area: right; }
.item5 { grid-area: footer; }

.grid-container {
  grid-template-areas:
    'header header header header header header'
    'menu main main main right right'
    'menu footer footer footer footer footer';
}
```

## 媒体查询

用于为不同的媒体类型/设备应用不同的样式。

媒体查询可用于检查许多事情，诸如:

-   视口的宽度和高度
-   设备的宽度和高度
-   方向（手机或平板电脑处于横屏还是竖屏模式?）
-   分辨率

### 语法

```css
@media [media-types] [and] [media-features] {
  CSS-Code;
}
```

-   and

    -   将媒体特性与媒体类型或其他媒体特性组合在一起
    -   如果只有`(media-feature)` 或者 只有`media-type`, 则不需要这个`and`

-   media-types

    `[not|only] media-type[,media-type,...]`

    -   可选零个或多个media-type

    -   not

        除了这个`media-type`之外的`media-type`会匹配规则

    -   only

        防止旧版浏览器应用指定的样式

        旧浏览器不支持带媒体特性的媒体查询

        它对现代浏览器没有影响

    -   当有多个`media-type`时, 用逗号分割

-   media-features

    `(media-feature) [and|or|not media-feature ...]`

    -   可选零个或多个media-feature
    -   当有多个`media-feature`时, 用`and|or|not `分割

### 样式表文件的媒体

```html
<link rel="stylesheet" media="screen and (min-width: 900px)" href="widescreen.css">
<link rel="stylesheet" media="screen and (max-width: 600px)" href="smallscreen.css">
```

### Media Types

| 值     | 描述                                   |
| :----- | :------------------------------------- |
| all    | 默认。用于所有媒体类型设备。           |
| print  | 用于打印机。                           |
| screen | 用于计算机屏幕、平板电脑、智能手机等。 |
| speech | 用于朗读页面的屏幕阅读器。             |

### Media Feature

形如`max-width: 600px`

具体可查阅[media rule](https://www.w3ccoo.com/cssref/css3_pr_mediaquery.html)

| 值          | 描述                                                      |
| :---------- | :-------------------------------------------------------- |
| max-width   | 屏幕宽度不超过指定值的, 被匹配                            |
| min-width   | 屏幕宽度不小于指定值的, 被匹配                            |
| orientation | 处于指定方向(横屏`landscape` or 竖屏`portrait`)下, 被匹配 |

### 示例

```css
@media only screen and (max-width: 600px) {
  body {
    background-color: lightblue;
  }
}
```

`max-width`表示屏幕宽度的不超过600px的, 匹配规则

### 模板

[响应式网页设计模板](https://www.w3ccoo.com/css/css_rwd_templates.html)

