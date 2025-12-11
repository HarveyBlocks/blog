# 变量

>   `$var: value`

## 字面量

-   数字 可能带单位, 也可能不带
-   字符串 可能有引号, 可能没有
-   颜色 可以以任何颜色的形式表达
-   bool true or false
-   单例 null
-   值列表 例如 1px solid black
-   映射 `("background": red, "foreground": pink)`

## 运算

-   `==`和`!=`
-   `+` `-` `*` `/` `%`
-   `<` `<=` `>` `>=`
-   `and` `or` `not`
-   `+` `-` `/`用于链接string

## 默认值

为变量赋值时，如果该变量已经有值，则其旧值将被覆盖

但是SCSS的变量在变成CSS之后就不能改了

`!default`允许用户在使用`@use`时对值进行修改

```scss
$border-radius: 0.25rem !default;

code {
  border-radius: $border-radius;
}
```

```scss
@use 'library' with (
  $border-radius: 0.1rem
);
```

## 作用域

样式文件顶层的变量是全局的, 所有模块都能使用

在块内也可以定义样式, 但作用域就是块了

```scss
$global-variable: global;

.content {
  $local-variable: local;
  border: $global-variable;
  padding: $local-variable;
}

.sidebar {
  border: $global-variable;
  // padding: $local-variable; 失败
}
```

### !global

不同作用域的变量同名, 则赋值不会对外面作用域的变量发生

```scss
$variable: global;

.content {
  $variable: local;
  border: $variable; // local
}

.sidebar {
  border: $variable; // global
}
```

如果希望能够更改, 那就使用`!global`标记

不过只能修改全局的变量, 外层作用域但不是全局的变量**不能修改**

```scss
$variable: global;

.content {
  $variable: local !global;
  border: $variable; // local
}

.sidebar {
  border: $variable; // local
}
```

### 流程控制规则的作用域

```scss
$dark-theme: true !default;
$primary-color: #f8bbd0 !default;

@if $dark-theme {
  $primary-color: darken($primary-color, 60%); // h
}

.button {
  background-color: $primary-color;
}
```

