#控制结构

## 顺序结构

```python
print("Hello World",end="")
```



##分支结构



-   if-elif-else
-   换行
-   冒号

```python
if True:
    print(True)
else:
    print(False)
```





-   可

```python
if True:
    a =1
print(a)
```

-   不可

```python
s = input()
if s == "1":
    a =1
print(a) # 报错
```



##循环结构



###while

作用域

```python
while input() != "exit":
    print("输入 `exit` 退出循环")
    a = 10
print(a) # 报错
```

没有do-while



###for-in

```java
for i in "可以是一个序列, 包括列表等":
    print(i)
```

for中定义的循环只在for中有效

```python
for i in range(10):
    a = 10

print(i) # 报错
print(a) # 报错
```



### break-continue

### range

```python
print([i for i in range(2)])            # [0, 1]
print([i for i in range(1, 2)])         # [1]
print([i for i in range(1, 11, 2)])     # [1, 3, 5, 7, 9]
```



