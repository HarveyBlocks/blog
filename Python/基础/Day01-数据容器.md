# 数据容器





## 概述

列表, 集合, 字典, 元组, 字符串

表中的元素类型不限

长度任意

创建容器时, 最后一个逗号可以存在, 例如 : `[1,1,2,]`

-   获取容器元素个数

    ```python
    len(容器名)
    ```

-   获取容器中的最大值

    ```python
    max(容器名)
    ```

-   获取容器中的最小值

    ```python
    min(容器名)
    ```

-   容器内元素类型可以任意, 但如果类型不同导致无法比较时, `max()`和`min()`方法就无法使用

    报错: 

    ```python
    not supported between instances of 'tuple' and 'str'
    ```
    
-   排序

    所以有的容器都可以用`sort(容器名[,reverse = True缺省False])`排序

    结果返回总是**列表**, 字符串拆解成字符后存入列表

    字典丢失Value

    ```python
    my_string = "12a"
    my_list = ['1', '2', 'a']
    my_tuple = ('1', '2', 'a')
    my_set = {'1', '2', 'a'}
    my_dict = {'1': 100, '2': 99, 'a': 98}
    print("=============original=============")
    print(my_string)
    print(my_list)
    print(my_tuple)
    print(my_set)
    print(my_dict)
    """
    12a
    ['1', '2', 'a']
    ('1', '2', 'a')
    {'a', '2', '1'}
    {'1': 100, '2': 99, 'a': 98}
    """
    print("=============default=============")
    print(sorted(my_string))
    print(sorted(my_list))
    print(sorted(my_tuple))
    print(sorted(my_set))
    print(sorted(my_dict))
    """
    ['1', '2', 'a']
    默认升序
    """
    ```

    ```python
    print("=============not reverse=============")
    print(sorted(my_string, reverse=False))
    """
    ['1', '2', 'a']
    默认reverse=False
    """
    print("=============reverse=============")
    print(sorted(my_string, reverse=True))
    """
    ['a', '2', '1']
    """
    ```



## 切片

>   slice

序列都是有序的

凡是**连续**, **有序**的序列都可以切片

##五种容器

### 列表

-   有序
-   可重复
-   内容类型不限
-   上线在2^64^-1

####定义

```python
list = [1, "你好", [1.3, True, 2]]
```

#### 下标

```python
if __name__ == '__main__':
    ls = [1, "你好", [1.3, True, 2]]
    print(ls[2])  # [1, 1, 2]
    print(ls[-1])  # [1, 1, 2]
    print(ls[2][1])  # True
```

#### 切片

```python
print(ls[::-1])  # 倒置, [[1.3, True, 2], '你好', 1]
print(ls[0:2:1])  # [0->起始索引,2->结尾索引), 1->步长(可负,不可0)
```
#### 列表的方法

![image-20240302143632281](../assets/Day01-%E6%95%B0%E6%8D%AE%E5%AE%B9%E5%99%A8/image-20240302143632281.png)

函数在类里就称为方法

-   查询指定元素的下标

    ```python
    if __name__ == '__main__':
        ls = [1, 1,"你好", [1.3, True, 2],4]
        # print(ls.index(2)) # 找不到就报错
        print(ls.index(1)) # 0
        print(ls.index(4)) # 3
    ```

    

-   增加元素

    ```python
    if __name__ == '__main__':
        ls = [1, 1, "你好", [1.3, True, 2], 4]
        ls[4] = 1
        print(ls) # 成功加入[1, 1, '你好', [1.3, True, 2], 1]
        ls[6] = 1
        print(ls)  # Index out of Bound
    ```

    增加多个

    ```python
    if __name__ == '__main__':
        ls = [1, 1, "你好", [1.3, True, 2], 4]
        print(ls.append([1, 1]))  # None
        print(ls)  # [1, 1, '你好', [1.3, True, 2], 4, [1, 1]]
        ls = ls + [1, 1]
        print(ls)  # [1, 1, '你好', [1.3, True, 2], 4, [1, 1], 1, 1]
        # 注意区分
    ```

    增加一批

    ```python
    if __name__ == '__main__':
        ls = [1, 1, "你好", [1.3, True, 2], 4]
        ls.extend([1, 1])
        print(ls)  # [1, 1, '你好', [1.3, True, 2], 4, 1, 1]
        ls.extend("sd") # 可以是各种序列做参数
        print(ls)  # [1, 1, '你好', [1.3, True, 2], 4, 1, 1, 's', 'd']
    ```

-   插入元素

    ```python
    if __name__ == '__main__':
        ls = [1, 1, "你好", [1.3, True, 2], 4]
        # index: 需要插入的元素插入后所在的索引
        print(ls.insert(2,'value')) # None
        print(ls) # [1, 1, 'value', '你好', [1.3, True, 2], 4]
    ```

-   删除元素

    -   依据下标删除

        ```python
        if __name__ == '__main__':
            ls = [1, 1,"你好", [1.3, True, 2],4]
            index = 1
            print(ls.pop(index)) # 返回删除的值
            print(ls)  # [1, '你好', [1.3, True, 2], 4]
        ```

    -   依据值删除

        ```python
        if __name__ == '__main__':
            ls = [1, 1,"你好", [1.3, True, 2],4]
            value = "你好"
            print(ls.remove(value)) # None
            print(ls) # [1, 1, [1.3, True, 2], 4]
            # print(ls.remove(value)) 找不到, 报错
        ```

        多个重复的值就只删除第一个

        ```python
        if __name__ == '__main__':
            ls = [1, 1, "你好", [1.3, True, 2], 4]
            value = 1
            print(ls.remove(value))  # None
            print(ls)  # [1, '你好', [1.3, True, 2], 4]
        ```

    -   **`del`关键字**删除

        ```python
        if __name__ == '__main__':
            ls = [1, 1, "你好", [1.3, True, 2], 4]
            del ls[1] 
            print(ls) # [1, '你好', [1.3, True, 2], 4]
        ```

-   清空列表

    ```python
    if __name__ == '__main__':
        ls = [1, 1,"你好", [1.3, True, 2],4]
        print(ls) # [1, 'x', '你好', [1.3, True, 2], 4]
        print(ls.clear()) # None
        print(ls) # []
        print([]) # []
    ```

    删除列表变量

    ```python
    if __name__ == '__main__':
        ls = [1, 1, "你好", [1.3, True, 2], 4]
        print(ls)
        del ls # 删除对变量的定义, 释放变量的空间
        print(ls) # ls is not defined
    ```

-   修改元素

    ```python
    if __name__ == '__main__':
        ls = [1, 1,"你好", [1.3, True, 2],4]
        ls[1] = "x"
        print(ls) # [1, 'x', '你好', [1.3, True, 2], 4]
    ```

    修改多个

    ```python
    if __name__ == '__main__':
        ls = [1, 1, "你好", [1.3, True, 2], 4]
        ls[0:2] = [2,3]
        print(ls) # [2, 3, '你好', [1.3, True, 2], 4]
    ```

-   倒置

    ```python
    if __name__ == '__main__':
        ls = [1, 1, "你好", [1.3, True, 2], 4]
        print(ls.reverse())
        print(ls)  # 倒置
    ```

-   拷贝

    ```python
    if __name__ == '__main__':
        ls = [1, 1, "你好", [1.3, True, 2], 4]
        ls2 = ls.copy()
        print(ls)
        print(ls2)
    
        """深拷贝"""
        ls2[0] = 10
        print(ls)
        print(ls2)
    ```

-   统计元素个数

    ```python
    if __name__ == '__main__':
        print(len([1,2,3])) # 3
    ```

    ```python
    if __name__ == '__main__':
        ls = [1, 1,"你好", [1.3, True, 2],4]
        print(ls.count(1)) # 2
    ```

-   所占字节大小(分配的大小)

    ```python
    print([1,1].__sizeof__()) # 56 字节数
    print([1,1].__sizeof__() - [1].__sizeof__()) # 8 字节数
    ```

    真实毫不吝啬内存啊

### 元组

-   不可修改
-   类型不限
-   有序

```python
if __name__ == '__main__':
    a = ()  # 空元组声明
    print(a)  # ()
    print(type(a))  # <class 'tuple'>

    b = (1,)  # 单元组声明加逗号
    print(b)
    print(type(b))
```

```python
if __name__ == '__main__':
    a = (1,2,3,4,5,6,7,8,9,10,11,12,13)
    print(a)
    print(a[0]) # 1
    print(a[3]) # 4
    print(a[0:4:2]) # (1, 3)
    value = 5
    print(a.count(value)) # 1
    print(a.index(value)) # 4
```







==元组里的list的元素可以修改==

```python
if __name__ == '__main__':
    a = (1,[1])
    a[1][0] = 2
    print(a) # (1, [2])
```





```python
def print_list(ls):
    try:
        for i in ls:
            print_list_dep(i, 0)
    except TypeError:
        print(ls)


def print_list_dep(ls, dep):
    try:
        for i in ls:
            print_list_dep(i, dep + 1)
    except TypeError:
        print(dep * "\t" + f"{ls}")


if __name__ == '__main__':
    a = (1, 2, (3, 4), (4, (5, 6), 7, 8), 9, (0))
    print_list(a)
```



### 字符串

-   字符的容器
-   可重复
-   有序
-   不可变

```python
if __name__ == '__main__':
    string = "0123456789abcdef"
    string[1] = '2' # 报错
    print(string)
```

```python
def insert_str(string, index, new_value):
    return string[0:index] + new_value + string[index:]


def update_str(string, index, new_value):
    return string[0:index] + new_value + string[index + 1:]


def pop_str(string, index):
    return string[0:index] + string[index + 1:]


if __name__ == '__main__':
    msg = "01234567890"
    msg = update_str(msg, 1, "x")
    print(msg)

```

#### 方法

![image-20240302161213588](../assets/Day01-%E6%95%B0%E6%8D%AE%E5%AE%B9%E5%99%A8/image-20240302161213588.png)

-   替换, 将所有指定字符串换成新字符串

    ```python
    if __name__ == '__main__':
        msg = "0202002002200020"
        # msg.replace(__old,__new,__count)
        print(msg.replace("2", "xx", 2))
        # 0xx0xx002002200020
    
        print(msg)
        # 0202002002200020
    ```

-   分割

    ```python
    if __name__ == '__main__':
        split = "a b ,c".split()  # 默认以空格分割
        print(split)
        split = "a b ,c".split(",")
        print(split)
        split = "a b ,c".split("[,\\s]+") # 不支持正则表达式(不屑)
        print(split)
    ```

-   掐头去尾

    ```python
    if __name__ == '__main__':
        msg = "   Hell o "
        print(f"`{msg.strip()}`")  # `Hell o`
        msg = "111Hell o11"
        print(f"`{msg.strip('1')}`")  # `Hell o`
        msg = "1212Hell o21"
        print(f"`{msg.strip('12')}`")  # `Hell o`
        msg = "1221Hell o21"
        print(f"`{msg.strip('12')}`")  # `Hell o`
        msg = "1222Hell o1121"
        print(f"`{msg.strip('12')}`")  # `Hell o`
    ```

    起始参数的作用不是字符串, 而是一个字符集, 顺序不重要, 前后有这个字符集里的字符都会被删除

    原理:

    ```python
    def str_strip(string, strip_by=" "):
        i = 0
        result = string
    
        # 消除头
        for i in range(len(result)):
            if not (result[i] in strip_by):
                break
        result = result[-1:-len(result) + i - 1:-1]
    
        # 消除尾
        for i in range(len(result)):
            if not (result[i] in strip_by):
                break
        result = result[-1:-len(result) + i - 1:-1]
    
        # 返回结果
        return result
    
    ```

-   统计小字符串出现次数

    ```python
    if __name__ == '__main__':
        msg = "***H**ell*o*"
        print(msg.count("**"))  # 2
        msg = "dadadadada"
        print(msg.count("dad"))  # 2
    ```

    获取到复合的之后, 直接跳过统计字符串的长度, 再往下数

    原理:

    ```python
    def str_count(string, count_by):
        count = 0
        i = 0
        while i < len(string):
            if string[i:i + len(count_by)] == count_by:
                i += len(count_by)
                count += 1
            else:
                i += 1
        return count
    ```

### 集合

-   不可重复
-   无序
-   可改

#### 定义集合

```python
if __name__ == '__main__':
    my_dir = {}  # 不能用来定义空集合, 这个是空字典
    print(my_dir)
    print(type(my_dir)) # <class 'dict'>
    my_set = set()  # 空集合
    print(my_set) # set()
    print(type(my_set))  # <class 'set'>
    my_set = {2, 2, 1, 3}
    print(my_set) # {1, 2, 3} 无序, 去重
    my_set = set(range(10))
    print(my_set) # {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}
```

#### 方法

不支持下标, 不支持切片

![image-20240302165529563](../assets/Day01-%E6%95%B0%E6%8D%AE%E5%AE%B9%E5%99%A8/image-20240302165529563.png)

-   添加`add()`

-   删除指定元素`remove()`和`discard`

    ```python
    # 尝试移除指定元素, 不存在不会抛异常
    print(my_set.discard(1))  # None
    print(my_set)  # {0, 2, 3, 4, 5, 6, 7, 8, 9}
    ```

-   随机**取出**元素, 不放回

    ```python
    my_set = set(range(10))
    print(my_set.pop()) # 0
    print(my_set.pop()) # 1 
    print(my_set.pop()) # 2
    print(my_set.pop()) # 3
    ```

    底层应该是给每个元素对应有一个`score`, 整形的话, score刚好递增, 

    其实就是取得最前面的元素然后删除

    只是不知道底层的`score`是怎么决定的罢了

-   清空集合`clear()`

-   统计集合元素

    

-   是否是成员

    ```python
    print(my_set.__contains__(6)) # True
    ```

-   集合关系的判断

    ```python
    my_set = set(range(10))
    # 是子集
    print({2, 3, 5}.issubset(my_set))  # True
    # 没有交集
    print(my_set.isdisjoint({-1, -2}))  # True
    # 是超集
    print(my_set.issuperset({2, 3, 5}))  # True
    ```

-   集合之间的运算(原集合不变)

    ```python
    my_set = set(range(10))
    your_set = set(range(4, 19))
    # 求交集
    print(my_set.intersection(your_set))  # {4, 5, 6, 7, 8, 9}
    # 求并集
    print(my_set.union(your_set))  # {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18}
    # 求差集 (我有它没有)
    print(my_set.difference(your_set))  # {0, 1, 2, 3}
    ```

-   更新集合(原集合改变 `inplase =  True`)

    ```python
    your_set = set(range(4, 19))
    
    # 并集式更新
    my_set = set(range(10))
    print(my_set.update(your_set))  # None
    print(my_set)  # {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18}
    
    # 交集式更新
    my_set = set(range(10))
    print(my_set.intersection_update(your_set))  # None
    print(my_set)  # {4, 5, 6, 7, 8, 9}
    
    # 差集式更新
    my_set = set(range(10))
    print(my_set.difference_update(your_set))  # None
    print(my_set)  # {0, 1, 2, 3}
    ```



#### 迭代器

```python
if __name__ == '__main__':
    my_set = set(range(10))
    it = my_set.__iter__()
    while True:
        try:
            ele = it.__next__()
            print(ele)
        except StopIteration:
            break
    print("完成遍历")
```

### 字典

#### 定义

```python
my_dir = {}
print(my_dir)
print(type(my_dir))
my_dir = dict()
print(my_dir)
print(type(my_dir))
# 不一样的,dict才是正统
# my_dir = dir()
# print(my_dir)
# print(type(my_dir))
```

##### key不得重复

```python
my_dir = {'a': 97, 'a': 98, 'c': "99", 'd': "100"}
print(my_dir) # {'a': 98, 'c': '99', 'd': '100'}
my_dir = {'a': 98, 'a': 97, 'c': "99", 'd': "100"}
print(my_dir) # {'a': 97, 'c': '99', 'd': '100'}
```

似乎是后来居上的





##### 键的类型

键有类型

```python
my_dict = {'1':10,1:20}
print(my_dict) # {'1': 10, 1: 20}
```

部分容器不能做键

```python
# my_dict = {{"hi": 1}: 100}    不能用字典做键
# my_dict = {{"hi", 1}: 100}    不能用集合做键
# my_dict = {["hi", 1]: 100}    不能用列表做键
my_dict = {("hi", 1): 100}  # 可以用元组做键
print(my_dict[("hi", 1)])  # 100
my_dict = {
    "Harvey": {
        "age": 19,
        "name": "Harvey",
        "score": 76
    }
}  # 值就为所欲为吧
print(my_dict["Harvey"])
# {'age': 19, 'name': 'Harvey', 'score': 76}
```

静态方法创建字典

```python
# 创建字典
keys = ['a', 'b', 'c']

my_dir = dict.fromkeys(keys)    # 静态方法
print(my_dir)  # {'a': None, 'b': None, 'c': None}

default_value = 0
my_dir = dict.fromkeys(keys, default_value)
print(my_dir)  # {'a': 0, 'b': 0, 'c': 0}

default_value = [97, 98, 99]
my_dir = dict.fromkeys(keys, default_value)
print(my_dir)
# {'a': [97, 98, 99], 'b': [97, 98, 99], 'c': [97, 98, 99]}
```



#### 方法

-   常见的

    ```python
    print(my_dir.copy())
    print(my_dir.clear())
    ```

-   分别获取, 键, 值, 键值对的容器

    ```python
    my_dir = {'a': 97, 'b': 98, 'c': "99", 'd': "100"}
    print(my_dir)
    # {'a': 97, 'b': 98, 'c': 'as', 'd': 'as'}
    
    print(my_dir.keys())
    # dict_keys(['a', 'b', 'c', 'd'])
    print(type(my_dir.keys()))
    # <class 'dict_keys'>
    
    print(my_dir.values())
    # dict_values([97, 98, '99', '100'])
    print(type(my_dir.values()))
    # <class 'dict_values'>
    
    print(my_dir.items())
    # dict_items([('a', 97), ('b', 98), ('c', '99'), ('d', '100')])
    print(type(my_dir.items()))
    # <class 'dict_items'>
    ```

-   获取单个值

    ```python
    print(my_dir['c'])
    print(my_dir.get('c'))
    ```

-   pop

    ```python
    # 依据key
    print(my_dir.pop('d'))  # 返回值,value
    print(my_dir)  # 删除了
    print(my_dir.popitem())  # 随机键值对取出并删除
    print(my_dir)
    ```

-   组合字典

    ```python
    # 组合字典
    my_dir = {'a': 97, 'b': 98, 'c': "99", 'd': "100"}
    your_dir = {'a': '101', 'b': '102', 'g': 103, 'h': 104}
    
    print(my_dir.update(your_dir))  # None
    print(my_dir)
    # {'a': '101', 'b': '102', 'c': '99', 'd': '100', 'g': 103, 'h': 104}
    ```

    **若参数字典和原字典存在键重叠且值不一致, 采用参数字典的值**

-   增加键值对

    `字典名[不存在的键] = 值`

    ```python
    my_dir['A'] = 65
    print(my_dir)
    ```

    ```python
    my_dir = {'a': 97, 'b': 98, 'c': "99", 'd': "100"}
    
    """
    如果键不存在，
    则插入指定的键值对并返回设置的默认值。
    如果键存在，
    返回字典中的键对应的值, 不加入, 不修改
    """
    print(my_dir.setdefault('a', 10))  # 97
    print(my_dir)  # 不变
    print(my_dir.setdefault('e', 101))  # 101
    print(my_dir)  # 新增
    ```

-   修改字典

    `字典名[存在的键] = 新值`

    ```python
    my_dir['a'] = -97
    print(my_dir)
    ```

    





#### 遍历

```python
my_dir = {'a': 97, 'b': 98, 'c': "99", 'd': "100", 100:100}
for key in my_dir:
    print(key) 
    print(type(key)) 
for value in my_dir.values():
    print(value)
    print(type(value))
```

##容器间的类型转化

```python
my_string = "12a"
my_list = [1, "2", 'a']
my_tuple = (1, "2", 'a')
my_set = {1, "2", 'a'}
my_dict = {1: 100, "2": 99, 'a': 98}
print("=============original=============")
print(my_string)
print(my_list)
print(my_tuple)
print(my_set)
print(my_dict)
"""
12a
[1, '2', 'a']
(1, '2', 'a')
{'2', 1, 'a'}
{1: 100, '2': 99, 'a': 98}
"""
```



```python
print("=============string=============")
print(str(my_string))
print(str(my_list))
print(str(my_tuple))
print(str(my_set))
print(str(my_dict))
"""
12a
[1, '2', 'a']
(1, '2', 'a')
{'2', 1, 'a'}
{1: 100, '2': 99, 'a': 98}
"""
```

```python
print("=============list=============")
print(list(my_string))
print(list(my_list))
print(list(my_tuple))
print(list(my_set))
print(list(my_dict))
"""
['1', '2', 'a']
[1, '2', 'a']
[1, '2', 'a']
['2', 1, 'a']
[1, '2', 'a']
"""
```

```python
print("=============tuple=============")
print(tuple(my_string))
print(tuple(my_list))
print(tuple(my_tuple))
print(tuple(my_set))
print(tuple(my_dict))
"""
('1', '2', 'a')
(1, '2', 'a')
(1, '2', 'a')
('2', 1, 'a')
(1, '2', 'a')
"""
```

```python
print("=============set=============")
print(set(my_string))
print(set(my_list))
print(set(my_tuple))
print(set(my_set))
print(set(my_dict))
"""
{'2', 'a', '1'}
{'2', 1, 'a'}
{'2', 1, 'a'}
{'2', 1, 'a'}
{'2', 1, 'a'}
"""
```







```python
print("=============容器不能转成dict=============")
print("=============dict转成其他类型的容器(除字符串)会丢失Value=============")
```

