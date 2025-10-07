# Scss

-   更自由
-   提高CSS复用
-   更好的维护
-   预处理成CSS

```shell
npm install -g sass
$ sass --version
```

```shell
$ sass file.scss [file.css]
```

## 注释

`// 注释内容 `单行注释, 转化成CSS时删除, 称为*静默注释*

`/* 注释内容 */`多行注释, 压缩模式下转化成CSS删除, 否则不删除

`/*! 注释内容 */ `不会在转化中删除, 由此称为*响亮注释*



## 变量

可存储

-   字符串
-   数字
-   颜色
-   bool
-   列表
-   null

### 语法

```scss
$variable_identifier: value;
```

### 示例

```scss
$myFont: Helvetica, sans-serif;
$myColor: red;
$myFontSize: 18px;
$myWidth: 680px;

body {
  font-family: $myFont;
  font-size: $myFontSize;
  color: $myColor;
}

#container {
  width: $myWidth;
}
```

## 隐藏声明

Sass 不会将值为`null`或空字符串(不带引号)的属性声明编译为 CSS, 会直接去除这一条属性
