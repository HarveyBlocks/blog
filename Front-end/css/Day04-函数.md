# 函数

[CSS 值函数 ](https://developer.mozilla.org/zh-CN/docs/Web/CSS/CSS_Values_and_Units/CSS_Value_Functions)

## 语法

```js
selector {
  property: function([argument]? [, argument]!);
}
```

## 函数表

| 函数   | 描述 |
| :--------- | :--------- |
|  `attr() `   | 返回所选元素的属性值。   |
|  `calc() `   | 允许您执行计算来确定 CSS 属性值。    |
| `min()` | 计算一系列值的最小值。 |
| `max()` | 计算一系列值的最大值。 |
|  `rgb() ` | 使用红-绿-蓝模型（RGB）定义颜色。    |
|  `rgba() `   | 使用红-绿-蓝-阿尔法模型（RGB）定义颜色。 |
|  `var() ` | 插入自定义属性的值。 |

## attr

下面的例子在每个链接后的括号中插入 href 属性的值:

```css
a:after {
 content: " (" attr(href) ")";
}
```

## var

变量

### 语法

```css
var(name, value)
```

| 值      | 描述                                             |
| :------ | :----------------------------------------------- |
| *name*  | 必需。自定义属性的名称（必须以两个破折号开头）。 |
| *value* | 可选。回退值（在自定义属性无效时使用）。         |

### 使用

```css
:root {
  --main-bg-color: coral;  
}

#div1 {
  background-color: var(--main-bg-color);
  padding: 5px;  
}

#div2 {
  background-color: var(--main-bg-color);
  padding: 5px;
}
```

## calc 计算

```css
#div1 {
  position: absolute;
  left: 50px;
  width: calc(100% - 100px);
  border: 1px solid black;
  text-align: center;
}
```

## rgb 和 rgba

颜色, 此处略

## 比较函数

>   min() 和 max()

```css
.logo {
  width: min(50%, 300px);
}
```

