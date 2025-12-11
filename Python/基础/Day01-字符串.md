# 字符串

## 定义

1.  单引号定义
2.  双引号定义
3.  三引号定义
    -   和多行注释写法一样
    -   同样支持多行

```python
print("你好,'666'")
print('你好,"666"')
```

## 拼接

```python
string = "你好" + "mysql" + str(12)
print(string)
```

## 格式化

```python
value = 1.1
message1 = "%10.3e" % value
print(message1)  # ` 1.100e+00`

var_name = "num" 
var_value = 2
message2 = "%s = %02d" % (var_name, var_value)
print(message2)  # num = 02
```

字符串`f"xxxx{var}xxx"`带入变量

-   不会理会类型
-   没有精度控制

```java
var_name = "num"
var_value = 2
message2 = f"{var_name} = {var_value}" 
print(message2)  # num = 02
```

## 比较

依据时ASCII码, 但不能直接和数字比

