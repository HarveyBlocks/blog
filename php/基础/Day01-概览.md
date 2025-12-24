#   概览

> **P**ersonal **H**ome **P**age

服务器端脚本语言, 代码可以嵌入HTML文档中

弱类型解释型语言

会编译成中间代码 **Opcode**, 然后再虚拟机上解释执行

## 获取PHP配置信息

```php
phpinfo();
```

输出PHP当前状态的大量信息

- PHP编译选项
- 启用的扩展
- PHP版本
- 服务器信息和环境变量
- PHP环境变量
- 操作系统版本信息
- path变量
- 配置选项的本地值和主值
- HTTP头
- PHP授权信息License

## 基本与研发

### 使用

```html
<?php
...
?>
```

嵌入`(X)HTML?XML`文档中

当PHP解析一个文件时, 会寻找开始和结束的标记, 并执行其中的代码

凡是在一对开始和结束标记之外的内容会被PHP解析器当成文本直接输出给客户端

**每个语句后用分号结束指令**

### 输出

有三种输出`echo`, `print`, `printf`

一般是输出到浏览器

```php
echo 12, 13, 14 , '<br>'; // 一个或多个参数
print "Hello World<br>"; # 一个参数
```





**Short open tag**

使用短标记`<?=` 作为 `<?php echo` 简写

```php+HTML
<?php
$username = "Geek";
?>
<h1>
    <?= $username ?>
</h1>
```



### 注释

C++ 风格单行注释

```php
// 此乃单行注释
```

C 风格多行注释

```php
/* 此乃 
多行
注释 */
```

UNIX Shell 风格

```php
# 此亦单行注释
```

## 基本调试函数

`var_dump()` 打印变量的相关信息

`print_r()` 打印关于变量的易于理解的信息

```php
$a = array(1, 2, array("a", "b", "c"));
echo '----------------------------------------', '<br>';
var_dump($a);
echo '----------------------------------------', '<br>';
print_r($a);
```


<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/php/基础/Day01-概览/image-20251224010638213.png" alt="image-20251224010638213" style="zoom: 67%;" />

