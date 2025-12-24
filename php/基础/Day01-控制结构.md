

# 控制结构

## 顺序

### 变量

#### 声明变量

变量用一个 `$` 符后加变量名来表示

变量名前加 `&` 表示引用 reference

```php
$foo = 'Bob';
$bar = &$foo;
$bar = 'My name is $bar';
echo $bar . '<br/>';
echo $foo . '<br/>';
```

![image-20251223191227049](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/php/基础/Day01-控制结构/image-20251223191227049.png)

PHP的引用更接近Cpp中引用的概念, 而不是C中指针的概念

PHP的引用可以理解为给变量取别名, 而没有拷贝整个变量的值




#### 弱类型

PHP是一种给弱类型的程序语言

一个变量更可以存储任意类型的数据

使用变量之前无需声明变量是字符串类型还是整形

```php
$foo = "0";
echo $foo . "<br>"; // 0
$foo += 2;
echo $foo . "<br>"; // 2
$foo = $foo + 1.3;
echo $foo . "<br>"; // 3.3
$foo = 5 + "10";
echo $foo . "<br>"; // 15
$foo = 5 + "10 abc"; // WARN
echo $foo . "<br>";  // 15
$foo = 5 + "abc 10 xyz"; // TypeError
```

#### 变量相关函数

- `unset` 将一个变量设置为 **`NULL`**
- `isset` 判断一个变量是否为 **`NULL`**
- `gettype` 获取变量的类型
- `settype` 设置变量的类型

### 常量

常量使用 `define` 定义

```php
define("CONSTANT", "AAA");
echo CONSTANT;
CONSTANT = 21; // ERROR
```

- 常量前面没有 `$`
- 常量只能用 `define()` 函数定义, 不能通过赋值语句
- 常量可以不用理会变量作用域的规则
- 常量一旦定义就不能被重写定义或取消定义
- 常量的值只能是标量

### 预定义量

#### 变量

主机和操作系统的许多环境变量

可以通过调用 phpinfo() 查看预定义变量列表

#### 常量

- `__FILE__` 执行中的 PHP 程序文件名
- `__LINE__` 执行中 PHP 程序行数
- `PHP_VERSION` PHP 的版本
- `PHP_OS` 执行 PHP 的操作系统名称
- `TRUE` and `FALSE`
- `E_ERROR` 指向最近的错误处

### 可变变量

动态定义一个变量的名称

```php
$a = 'hello';
$$a = 'world';
// 将 `$a` 的值 `hello` 作为变量名
// 创建新变量 `$hello`
// 然后给 `$hello` 赋值
echo "$a <br> $hello <br>";
```

### 作用域

脚本语言对作用域的限制不是很严格

```php
if(rand(0,1)==1){
	$a = 1;    
}else{
    $b = 2;
}
echo $a; // 允许, 只会警告, 不会直接报错
```

如果恰好进入分支 `b`, 那么渲染的页面会有警告信息

![image-20251223215143488](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/php/基础/Day01-控制结构/image-20251223215143488.png)

函数内无法直接对外部变量进行调用

```php
$a = 1;
function Test() {
    echo $a; // ERROR
    $b = 2;
    function Inner() {
        echo $a; // ERROR
        echo $b; // ERROR
    }
}

Test();
```

### global

如果要在函数中引用一个全局变量，可以使用global 关键字

```php
$a = 1;
function Test() {
    global $a;
    echo "a = " . $a . "<br/>";
    $b = 2;
    function Inner() {
        global $a, $b;
        echo "a = " . $a . "<br/>";
        // 不会报错, 当无法取得外围作用域中的$b值
        echo "b = " . $b . "<br/>";  // 不会渲染这个$b
        echo 'b is null ? ' . ($b == null ? 'true' : 'false') . "<br/>"; // true
    }

    Inner();
}

Test();
```

![image-20251223220138170](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/php/基础/Day01-控制结构/image-20251223220138170.png)





#### static

静态变量仅在局部函数域中存在，但当程序执行离开此作用域时，其值并不丢失。

```php
function num_generator(): int {
    static $a = 0;
    return $a++;
}

echo num_generator(); // 0
echo num_generator(); // 1
echo num_generator(); // 2
echo num_generator(); // 3
echo num_generator(); // 4
echo num_generator(); // 5
echo num_generator(); // 6
echo num_generator(); // 7
echo num_generator(); // 8
echo num_generator(); // 9
```

## 分支

### if-else elseif

```php
if ($a == '1') {
    echo '1';
} elseif ($a == '2') {
    echo '2';
} else if ($a == '3') {
    echo '3';
} else {
    echo 'otherwise';
}
```

主打一个我全都要

### switch

switch 表达式的类型必须为整数，浮点数，或是字符串

```php
switch ($variable) {
    case "a":
        echo 'a';
        break;
    case "b":
        echo 'b';
        break;
    case 13:
        echo 13;
        break;
    case 11:
        echo 11;
        break;
    case 1.1:
        echo 1.1;
        break;
    case 'aaa' . 'bbb' . 'ccc':
        echo 'aaabbbccc';
        break;
    case 1 + 2 . 'c' . 3 . 'a':
        echo '3c3a';
        break;
    case true:
        echo true;
        break;
    default:
        echo 'default';

}
```

case的比较使用 `==` 的逻辑, 使用类型转化的比较

## 循环

- while
- do-while
- for
- foreach

```php
for ($i = 0; $i < 12; $i++)
    echo $i . '&nbsp;';
```

`foreach` 的使用

```php
$arr = array(1, 2, 3, 4);
foreach ($arr as &$value) { // 这里要赋值, 应当有引用
    $value *= $value;
}
echo $value;
```

使用循环

```php+HTML
<table border = "border">
    <caption> Powers table </caption>
    <tr>
        <th> Number </th> <th> Square Root </th>  
        <th> Square </th> <th> Cube </th> <th> Quad </th>
    </tr>
    <?php
    for ($number = 1; $number <=10; $number++) {
        $root = sqrt($number);
        $square = pow($number, 2);
        $cube = pow($number, 3);
        $quad = pow($number, 4);
        print("<tr align = 'center'> <td> $number </td>");
        print("<td> $root </td> <td> $square </td>");
        print("<td> $cube </td> <td> $quad </td> </tr>");
    }
    ?>
</table>
```



## 替代语法

PHP对于流程控制, 不仅支持C风格的花括号代码块, 还只是使用shell风格的脚本代码块

```php
if($flag):
endif;

while($flag):
endwhile;
```

增加可读性

```php+HTML
<table border="border">
    <caption> Powers table</caption>
    <tr>
        <th> Number</th>
        <th> Square Root</th>
        <th> Square</th>
        <th> Cube</th>
        <th> Quad</th>
    </tr>
    <?php for ($number = 1; $number <= 10; $number++) :
        $root = sqrt($number);// PHP 代码
        $square = pow($number, 2);
        $cube = pow($number, 3);
        $quad = pow($number, 4);
        ?>
    <tr align = 'center'>
        <td> <?= $number ?> </td> <!--融入HTML-->
        <td> <?= $root ?> </td>
        <td> <?= $square ?> </td>
        <td> <?= $cube ?> </td>
        <td> <?= $quad ?> </td>
        <td> <?= $number ?> </td>
    <?php endfor; ?> <!--PHP代码, 结尾-->
</table>
```

如果是花括号, 允许, 但是可读性低