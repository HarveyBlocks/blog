# Lua

## 简介

Lua是轻量小巧的脚本语言, 由标准C语言编写

设计目的是为了嵌入应用程序

Centos内自带了Lua的运行缓解

### 运行Lua文件

1.  创建`hello.lua`文件

2.  编写Lua脚本文件

    ```lua
    print("Hello World")
    ```

3.  执行命令运行脚本

    ```bash
    lua hello.lua
    ```


### 打开Lua控制台

>   类似于Python的控制台

```bash
lua
```



```bash
[root@harvey-centos lua]# lua
Lua 5.1.4  Copyright (C) 1994-2008 Lua.org, PUC-Rio
> print("Hello")
Hello
> 
```

### 注释

```lua
--这是Lua的单行注释
--[[
 多行注释
 多行注释
 --]]
```



### 标识符

**Lua 是一个区分大小写的语言**

 标识符以字母"A 到 Z"或"a 到 z"或下划线"_"开头，后跟零个或多个字母、下划线和数字（0 到 9）。

### 关键词

以下列表显示了 Lua 中的一些保留关键词。 这些保留关键词不能用作常量或变量或任何其他标识符名称。

| and      | break | do    | else   |
| -------- | ----- | ----- | ------ |
| elseif   | end   | false | for    |
| function | if    | in    | local  |
| nil      | not   | or    | repeat |
| return   | then  | true  | until  |
| while    |       |       |        |



## 变量与类型

### 数据类型

| 数据类型 | 描述                                                         |
| -------- | ------------------------------------------------------------ |
| nil      | 空, 在条件表达式中表示false,一切变量的默认值                 |
| boolean  | false和true                                                  |
| number   | 双进度类型的实浮点数, 任何数, 包括0都为真(true)              |
| string   | 由一对双引号或单引号包围                                     |
| function | 由C或lua编写的函数                                           |
| table    | Lua中的表(table)其实是一个"关联数组(associative arrays)", 数组的索引可以是数字, 字符串或表类型. 在Lua里, table的创建时通过"构造表达式"来完成的, 最简单的构造表达式时`{}`,用来创建一个空表 |

### type函数



```lua
print("Hello World")
print("12 is "..type(12))
print("1.2 is "..type(1.2))
print("'Hello' is "..type('Hello'))
print("nil is "..type(nil))
print("true is ".. type(true))
print("print() is ".. type(print))
print("type() is ".. type(type))
print("{} is ".. type({}))
```



```text
Hello World
12 is number
1.2 is number
'Hello' is string
nil is nil
true is boolean
print() is function
type() is function
{} is table
```





### 变量声明

### 变量的作用域

`local`表示局部变量,缺省表示全局变量

局部变量的作用域为从声明位置开始到所在语句块结束。

不同文件可以读到全局变量

```lua
local num = 1.2
local str = "Hello"
PI = 3.1415926
```



#### 表的声明



```lua
-- 索引为数字,从1开始
local arr = {1,2,5,1,5}
-- 索引为字符串, 字符串不用加引号
local map = {name = 'Jack',age = 21}
```



#### 表的访问

```lua
local arr = {1,2,5,1,5}
local map = {name = 'Jack',age = 21}
print(arr[1])
print(arr[3])
print(arr[4])
print(map['name'])
print(map.name)
```



```text
1
5
1
Jack
Jack
```



## 流程控制

### 顺序

#### 算数运算符

下表列出了 Lua 语言中的常用算术运算符，设定 A 的值为10，B 的值为 20：

| 操作符 | 描述                 | 实例                |
| :----- | :------------------- | :------------------ |
| +      | 加法                 | A + B 输出结果 30   |
| -      | 减法                 | A - B 输出结果 -10  |
| *      | 乘法                 | A * B 输出结果 200  |
| /      | 除法                 | B / A 输出结果 2    |
| %      | 取余                 | B % A 输出结果 0    |
| ^      | 乘幂                 | A^2 输出结果 100    |
| -      | 负号                 | -A 输出结果 -10     |
| //     | 整除运算符(>=lua5.3) | **5//2** 输出结果 2 |

#### 字符串运算符

下表列出了 Lua 语言中的连接运算符与计算表或字符串长度的运算符：

| 操作符 | 描述                               | 实例                                                         |
| :----- | :--------------------------------- | :----------------------------------------------------------- |
| ..     | 连接两个字符串                     | a..b ，其中 a 为 "Hello " ， b 为 "World", 输出结果为 "Hello World"。 |
| #      | 一元运算符，返回字符串或表的长度。 | #"Hello" 返回 5                                              |

### 条件



#### 关系运算符

下表列出了 Lua 语言中的常用关系运算符，设定 A 的值为10，B 的值为 20：

| 操作符 | 描述                                                         | 实例                  |
| :----- | :----------------------------------------------------------- | :-------------------- |
| ==     | 等于，检测两个值是否相等，相等返回 true，否则返回 false      | (A == B) 为 false。   |
| ~=     | 不等于，检测两个值是否相等，不相等返回 true，否则返回 false  | (A ~= B) 为 true。    |
| >      | 大于，如果左边的值大于右边的值，返回 true，否则返回 false    | (A > B) 为 false。    |
| <      | 小于，如果左边的值大于右边的值，返回 false，否则返回 true    | (A < B) 为 true。     |
| >=     | 大于等于，如果左边的值大于等于右边的值，返回 true，否则返回 false | (A >= B) 返回 false。 |
| <=     | 小于等于， 如果左边的值小于等于右边的值，返回 true，否则返回 false | (A <= B) 返回 true。  |

#### 逻辑运算符

下表列出了 Lua 语言中的常用逻辑运算符，设定 A 的值为 true，B 的值为 false：

| 操作符 | 描述                                                         | 实例                   |
| :----- | :----------------------------------------------------------- | :--------------------- |
| and    | 逻辑与操作符。 若 A 为 false，则返回 A，否则返回 B。         | (A and B) 为 false。   |
| or     | 逻辑或操作符。 若 A 为 true，则返回 A，否则返回 B。          | (A or B) 为 true。     |
| not    | 逻辑非操作符。与逻辑运算结果相反，如果条件为 true，逻辑非为 false。 | not(A and B) 为 true。 |

#### 分支语句

```lua
if 1 == 1 then
	print('true')
end
if (not nil) then
    -- 括弧随意
	print("!nil")
elseif true or false then
	-- 条件运算符 and or not
	print("true")
else
	print('default')
end
```







### 循环

#### 条件循环



```lua
local i = 5;
while i>0 do
	print(i)
	i=i-1
end
```



```lua
5
4
3
2
1
```

#### 增强循环

```lua
print('-------arr-------')

local arr = {1,1,4,5,1,4,1,9,1,9,8,1,0}

for index,value in ipairs(arr) do
	print(type(index),index,type(value),value)
end

print('-------map-------')

local map = {
	ok="114",
	come="514",
	go="1919",
	monster="810"
}

for key,value in pairs(map) do
	print(type(key),key,type(value),value)
end
```



```text
-------arr-------
number  1       number  1
number  2       number  1
number  3       number  4
number  4       number  5
number  5       number  1
number  6       number  4
number  7       number  1
number  8       number  9
number  9       number  1
number  10      number  9
number  11      number  8
number  12      number  1
number  13      number  0
-------map-------
string  ok      string  114
string  monster string  810
string  go      string  1919
string  come    string  514
```



## 函数

```lua
function 函数名(arg1,arg2...)
    return 返回值 -- 可有可无
end
```

```bash
local arr = {1,1,4,5,1,4}
local function printArr(array)
	if (not array) then
		print(nil)
		return 0
	end
	local i = 0;
	for index,value in ipairs(array) do
		print(type(index),index,type(value),value)
		i = index
	end
	return i
end
local length = printArr(arr);

print("len = "..length);
```

```bash
number  1       number  1
number  2       number  1
number  3       number  4
number  4       number  5
number  5       number  1
number  6       number  4
len = 6
```

