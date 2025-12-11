# 样式

## 选择器嵌套

使用嵌套样式, 等同于CSS的**后代选择器**

```scss
nav {
  ul {
    margin: 0;
    padding: 0;
    list-style: none;
  }

  li { display: inline-block; }

  a {
    display: block;
    padding: 6px 12px;
    text-decoration: none;
  }
}
```

### 选择器列表

外界和内部使用较为复杂的选择器, 会分别转为各种选择器

```scss
.alert, .warning {
  ul, p {
    margin-right: 0;
    margin-left: 0;
    padding-bottom: 0;
  }
}
```

转化为CSS

```scss
.alert ul, .alert p, .warning ul, .warning p {
  margin-right: 0;
  margin-left: 0;
  padding-bottom: 0;
}
```

### 选择器组合器

```scss
ul > {
  li {
    list-style-type: none;
  }
}

h2 {
  + p {
    border-top: 1px solid gray;
  }
}

p {
  ~ {
    span {
      opacity: 0.8;
    }
  }
}
```

## 插值

插值用于将文本接在非值上, 例如选择器上

```scss
$name: aaa; // 定义变量

.data-#{$name} { // 用#{}插入变量. 最终变成data-aaa
  background: yellow;
}
```

```css
.data-aaa { // 用#{}插入变量. 最终变成data-aaa
  background: yellow;
}
```

插值可以用于

-   选择器, 或其一部分
-   变量名, 或其一部分
-   属性, 或其一部分
-   CSS `@`规则
-   `@extend` , 或其一部分
-   纯CSS `@ipmort`
-   字符串内部(无论带有字符串与否)
-   纯CSS函数名
-   响亮注释`/**/`

### 带引号的值

直接插值会删除带引号的值的引号

使用`meta.inspect()`函数来保留引号

## 父选择器

>   &

在嵌套选择器中用于引用外部选择器

```scss
.alert {
  // 用于伪类
  &:hover {
    font-weight: bold;
  }

  // 父选择器被用作后代
  [dir=rtl] & {
    margin-left: 0;
    margin-right: 10px;
  }

  // 用在类选择器的参数上
  :not(&) {
    opacity: 0.8;
  }
}
```

```css
.alert:hover {
  font-weight: bold;
}
[dir=rtl] .alert {
  margin-left: 0;
  margin-right: 10px;
}
:not(.alert) {
  opacity: 0.8;
}
```

### 添加后缀

只要外部选择器以字母数字结尾(简单的类, ID, 元素选择器), 父选择器还可以用来直接拼接后缀

```scss
.accordion {
  max-width: 600px;
  &_copy {
    display: none;

    &-open {
      display: block;
    }
  }
}
```

css

```css
.accordion {
  max-width: 600px;
}
.accordion_copy {
  display: none;
}
.accordion_copy-open {
  display: block;
}
```

### 值

```scss
.main aside:hover,
.sidebar p {
  parent-selector: &;
}
```

```css
.main aside:hover,
.sidebar p {
  parent-selector: .main aside:hover, .sidebar p;
}
```

### falsely

如果父选择器出现在不应该出现的地方, 就会转为null, 例如在没有父选择器的地方使用父选择器

```scss
#{if(&, '&.app-background', '.app-background')};
```

在没有父选择器的地方使用会变成

```scss
'.app-background'
```

## 占位符选择器

>   %

一般情况下不被编译入css

```scss
.alert:hover, %strong-alert {
  font-weight: bold;
}

%strong-alert:hover {
  color: red;
}
```

转为css

```css
.alert:hover {
  font-weight: bold;
}
```

使用`@extends`使用选择器

```scss
%toolbelt {
  box-sizing: border-box;
  border-top: 1px rgba(#000, .12) solid;
  padding: 16px 0;
  width: 100%;

  &:hover { border: 2px rgba(#000, .5) solid; }
}

.action-buttons {
  @extend %toolbelt;
  color: #4285f4;
}
```

转化为css

```css
.action-buttons {
  box-sizing: border-box;
  border-top: 1px rgba(0, 0, 0, 0.12) solid;
  padding: 16px 0;
  width: 100%;
}
.action-buttons:hover {
  border: 2px rgba(0, 0, 0, 0.5) solid;
}

```

