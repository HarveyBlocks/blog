# CSS

>   层叠样式表 (**C**ascading **S**tyle **S**heets)

## 语法

```css
选择器 {
    属性: 值;
    属性组: 值 值 值; /*注释*/
    属性: 值; /*最后的分号`;`可选*/
}
```

```css
p {
    color:red;
    text-align:center;
}
```

### 简写属性

```css
body {
    background:#ffffff url('img.png') no-repeat right top;
}
```

等价于

```css
body {
    background-repeat: no-repeat;
    background-position: right top;
    background-image: url('img.png');
    background-color: #ffffff;
}
```



## 颜色

-   十六进制 - 如："#ff0000"
-   RGB - 如："rgb(255,0,0)"
-   颜色名称 - 如："red"
-   HSL - 指定 HSL 值，比如 "hsl(0, 100%, 50%)"

## 位置

-   left
-   right
-   center
-   bottom
-   top

可选择, 多个位置可以叠加, 例如

```css
body {
    background-position: right top;
    background-image: url('img.png');
}
```



## 大小

-   pt  点, 72分之一英寸
-   px 像素
-   em  相对单位，1em等于当前字体大小
-   cm  厘米, 绝对长度单位

## inherit 继承

指继承父元素(外级元素)的属性值

