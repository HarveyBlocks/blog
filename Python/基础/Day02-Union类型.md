# Union

一个变量里的类型不受限制

我们也可以用类型注解去标注一个变量达到规范和可读性

但是如果想要一个变量存储的内容, 既可以是str也可以是int, 也需要被类型标注出来 ,怎么办呢?

使用Union

## 对变量标注Union



```python
my_var: Union[str,int] = "2sdal"
print(my_var)
my_var = "asdssfas"
print(my_var)
my_var = 21
print(my_var)
my_var = 21.3 # 涉黄
print(my_var)
```





## 对容器标注Union

```python
my_list: list[Union[str, int]] = ["a", "b", 96, 94]
print(my_list)
my_list = [3.1, 2.1] # 涉黄
```

说实话, 这个标注有时候不太管用真的

```python
def my_fun(var: Union[int, str]) -> Union[int, str]:
    return var
```