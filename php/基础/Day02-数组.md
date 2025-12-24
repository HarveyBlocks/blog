# 数组

PHP的数组是个一一映射的键值对

可以使用数字作为索引

也可以用字符串作为索引



## 创建

如果指定的索引已经有了 value，则该值会被覆盖

如果给出的 value 没有指定索引，则取当前最大的整数索引值加一

```php+HTML
<?php
$fruits = array(
    0 => "apples",
    1 => "oranges",
    2 => "grapes",
    '0' => '覆盖索引0',
    '索引是3', 6 => '索引是6', '索引是7',
    3 => '覆盖索引3'
);
?>
<?php foreach ($fruits as &$fruit): ?>
    <div><?= $fruit ?></div>
<?php endforeach; ?>
```

数组中可以混合各种类型元素

![image-20251224030942414](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/php/基础/Day02-数组/image-20251224030942414.png)



### 短数组定义

不再使用 `array()` 来定义, 使用`[]`代替

```php
$fruits = [
    0 => "apples",
    1 => "oranges",
    2 => "grapes",
    '0' => '覆盖索引0',
    '索引是3', 6 => '索引是6', '索引是7',
    3 => '覆盖索引3'
];
```

## 赋值元素/添值

```php
$arr[4] = 7;
$arr["day"] = "Tuesday";
$arr[] = 17;
var_dump($arr);
```

![image-20251224030844184](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/php/基础/Day02-数组/image-20251224030844184.png)

## 比较

```php
$a = [
    '0' => 1,
    1 => '2',
];
$b = [
    0 => '1',
    '1' => 2,
];
$c = [
    0 => 1,
    '1' => '2',
];
echo $a == $b ? 'true' : 'false'; // true
echo $a === $b ? 'true' : 'false'; // false
echo $a === $c ? 'true' : 'false'; // true
```

## Union

重复的键, 保留左侧的值

```php
$a = ["a" => "apple", "b" => "banana"];
$b = ["a" => "pear", "b" => "strawberry", "c" => "cherry"];
$c = $a + $b; // Union of $a and $b
var_dump($c);
$c = $b + $a; // Union of $b and $a
var_dump($c);
```



![image-20251224102426955](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/php/基础/Day02-数组/image-20251224102426955.png)

## 提取

获取 keys 和 values 

```php
$a = ["0" => "apple", 1 => "banana",'a'=>'peach' ];
var_dump(array_keys($a));
var_dump(array_values($a));
```

![image-20251224103213177](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/php/基础/Day02-数组/image-20251224103213177.png)

## 删除

可以使用`unset`

```php
unset($list[4]); // 删除数组元素
unset($list); // 删除整个数组
```

## 排序

| 函数名 | 排序依据 | 顺序 | 索引关系                             |
| ------ | -------- | ---- | ------------------------------------ |
| sort   | 值       | 升序 | 只移动值, 键不移动, 索引关系可能改变 |
| asort  | 值       | 升序 | 移动值和键, 保持索引关系             |
| rsort  | 值       | 降序 | 只移动值, 键不移动, 索引关系可能改变 |
| ksort  | 键       | 升序 | 移动值和键, 保持索引关系             |
| krsort | 键       | 降序 | 移动值和键, 保持索引关系             |

## 其他函数

- `is_array($list)` 
- `in_array($element,$list)`
- `explode(",",$str)` 字符串分割成数组
- `implode(", ",$list)` 数组元素值转换成字符串

## 多维数组

```php
$arr_books['0-51-85595-5']['pages'];
```