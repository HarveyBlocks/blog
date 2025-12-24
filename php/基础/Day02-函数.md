# 函数

函数不能被重载、重复定义

**函数的标识符大小写不敏感**

```php
function F() {}
function f() {} // ERROR
```

## 函数原型

内部函数有函数原型

```php
string strtoupper(string subject);
string date(string format [, integer timestamp]);
```

## 参数

默认参数, 不定参数

```php
function func($a, $b = 1, ...$c) {
    foreach ($c as &$e) echo $e . '&nbsp;';
}

func(1, 2, "a", "b", "c");
```

## 引用传递和值传递

```php
function func1(&$a) { // 引用传递
    $a++;
}

function func2($a) { // 值传递
    $a++;
}

$a = 0;
func1($a);
echo $a; // 1
func2($a);
echo $a; // 1
// func2(&$a) 调用时通过引用传递在 PHP 5.4 中已被移除
```



## 返回引用

必须在函数声明和指派返回值给一个变量时都使用引用运算符

```php
function &returns_reference() {
    static $src = "hello";
    return $src;
}

$new_ref =& returns_reference();
echo $new_ref; // hello
$new_ref = "world";
echo $new_ref; // world
$new_ref =& returns_reference();
echo $new_ref; // world
```