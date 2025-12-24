# 类型

## 数据类型

标量类型

- boolean
- float/double
- integer
- string

复合类型

- array
- object

特殊类型

- resource
- NULL

## 类型转换

### 字符串到数值

字符串参与算术运算时, PHP会将字符串转换位数值

一个字符串包含 e 或是 E，它将被转换成浮点数，否则转换成整数

如果有正负号开头, 会被解析

```php
echo 1 + '-1.2'; // -0.2
echo 1 + '-1.2e3'; // -1199
echo 1 + '.3'; // 1.3
```



如果果字符串没有以一个符号或是数字开始，那么会被当成 0 (测试出来不是这样的)



### 显式类型转换



```php
$total = (int)'123';
$total = intval('123');
$total = '123';
settype($total, 'integer');
echo gettype($total); // integer
settype($total, 'int');
echo gettype($total); // integer
```



`gettype` 可能返回 `unknown`

