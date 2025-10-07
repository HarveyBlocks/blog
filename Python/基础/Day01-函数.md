# 函数





```python
def print_hi(name):
    print(len(name))
	return len(name) # 可省略

if __name__ == '__main__':
    print_hi('PyCharm')
    exit(0)
```





```python
def my_function():
    return "Hello"


def print_hi():
    def my_fun(): # 可以
        return "你好"

    print(my_fun())
    print(my_function())


# print(my_fun())  保错


if __name__ == '__main__':
    print_hi()
    exit(0)
```



## 传参

```python
def a(x):
    print(x)
if __name__ == '__main__':
    a(12)
    a(x = 12) # 可以把参数名写出来
    # 报错 a(l = 12)
```

```python
def a(x, y):
    print(x - y)


if __name__ == '__main__':
    a(12, 11)
    a(y=11, x=12) # 参数列表是无序的
```

### 值传递or引用传递

```python
from pojo.Student import Student


def change(stu: Student):
    stu.name = "李四"


if __name__ == '__main__':
    s1: Student = Student("张三", 18, 100)
    print(s1)
    change(s1) 
    print(s1) # 变成张三了
```

-   传递的是指针
-   Python没有什么包装类之类的, 所有的类型都是引用类型, int,str都是引用类型



### 位置参数



传入实际参数和接收的形式参数一一对应

```python
def xyz_2str(x: int, y: int, z: int):
    return str(x), str(y), str(z)


if __name__ == '__main__':
    a: int
    b: int
    c: int
    a, b, c = 1, 2, 3

    x, y, z = xyz_2str(a, b, c)
```

使用`/`强制**此之前的参数**必须使用位置传参, 不得使用关键字传参

```python
def xyz_2str(x: int, y: int, /, z: int):
    return str(x), str(y), str(z)


if __name__ == '__main__':
    a: int
    b: int
    c: int
    a, b, c = 1, 2, 3

    x, y, z = xyz_2str(a, b, z = c) # 可
    print(x, y, z)
    x, y, z = xyz_2str(a, y=b, z=c) # 意外实参, y未填
    print(x, y, z)
```



### 关键字参数

写出形式参数的变量名, 写出后位置可以变动

```python
def xyz_2str(x: int, y: int, z: int):
    return str(x), str(y), str(z)


if __name__ == '__main__':
    a: int
    b: int
    c: int
    a, b, c = 1, 2, 3

    x, y, z = xyz_2str(y=a, z=b, x=c)
```



-   写关键字和不写的可以混合
-   位置参数应该在所有关键字参数之前
-   第一个写关键字的参数之后的所有参数都应加上关键字
-   被写了关键字的参数可以调换在参数列表种的位置

```python
fun(a, b, z=c)
fun(a, y=b, z=c)
fun(a, z=b, y=c)
# xyz_2str(x=a, b, z=c)不可以
# xyz_2str(x=a, b, c)不可以
# xyz_2str(a,y=b,c) 不可以
```

使用`*`强制**此之后的参数**必须使用关键词传参, 不得使用位置传参

```python
def xyz_2str(x: int, y: int, *, z: int):
    return str(x), str(y), str(z)


if __name__ == '__main__':
    a: int
    b: int
    c: int
    a, b, c = 1, 2, 3

    x, y, z = xyz_2str(a, b, z = c) # 可
    print(x, y, z)
    x, y, z = xyz_2str(a, b, c) # 意外实参, z未填
    print(x, y, z)
```









### 缺省参数

在参数列表为参数声明默认值, 有了默认值, 传参时可以缺省参数,然后使用默认值

```python
def fun(x: int, y: int, z: int = 0):
    print(x,y,z)


if __name__ == '__main__':
    a, b, c = 1, 2, 3
    fun(a, b)

```

**默认值必须在最后, 因为缺省的话会从前往后配**

如果默认值的参数写在中间, 传参的时候串两个,就不知道你是参数没传全还是使用了默认值



### 不定长参数

-   不确定参数的数量, 例如`print()`里输出的内容可以有无限多个
-   不定长参数就是可以给0个, 给1个,给无穷个
-   在调用函数时, 若不定长参数在缺省参数前面, 应使用关键字参数传参







-   位置传递的不定长

    -   指在传递参数时没有使用关键字(如果使用了关键字反而会报错)
    -   使用`*`标注参数
    -   **函数内部会将不定长参数看作元组**
    -   **位置传递的不定长参数**和缺省参数**在声明函数时的参数列表种顺序随意**

    ```python
    def fun(*argv):
        print(type(argv))
        print(argv)
    
    
    if __name__ == '__main__':
        a, b, c = 1, 2, 3
    
        fun()
        """
        <class 'tuple'>
        ()
        """
    
        fun(a)
        """
        <class 'tuple'>
        (1,)
        """
    
        fun(a, b,c)
        """
        <class 'tuple'>
        (1, 2, 3)
        """
    ```

-   关键字传递的不定长

    -   用**两个`*`**标注参数, **即`**argc`**
    -   使用键值对形式的关键字传递参数
    -   函数内部会将不定长参数看作**字典**
    -   使用关键字传递的不定长参数, 缺省参数在声明函数时!!!一定要写在使用关键字传递的不定长参数之前!!!!!
    -   如果要传递缺省参数, **缺省参数也需要写关键字**, 当然也可以不传递缺省参数
    -   后面不定长参数的关键字里不能有缺省参数的关键字
    -   其余的不是定长的也不是缺省的, 写不写关键字都无所谓
    -   如果全部写了关键字, 位置全都可以乱换

    ```python
    def fun(x, end='\n', **argv):
        print(type(argv), end)
        print(argv, end=end)
    
    
    if __name__ == '__main__':
        fun(1, a=1, b=2, c=3)
        fun(x=1, a=1, b=2, c=3)
        fun(1, end='\n', a=1, b=2, c=3)
        fun(x =1, end='\n', a=1, b=2, c=3)
        fun(  a=1, x =1,b=2,end='\n', c=3)
        # 关于写了关键字,传递参数的顺序, 这样也可以:
        fun(1,  a=1, b=2,end='\n', c=3)
        
    ```

-   不管是关键字传递的不定长参数还是位置传递的不定长参数, 凡是不定长参数的, 都不能在同一个函数中出现两个即以上

-   别管什么`args`, 取有意义的名字更为优先



##作用域



函数内变量, 如果在外界有定义而其内部无定义, 会采用外部的定义

```python
a = "a"  # 全局变量


def fun():
    print(a)
    print(b)
    print(c)
    print(d)


b = "b"  # 全局变量, 后面也能取得

if __name__ == '__main__':
    c = "c"  # 可以取得
    fun()
    d = "d" # 不能取得
```



```python
a = "a"  # 全局变量


def fun():
    print(a)
    print(b)
    # print(c)
    # print(d)


b = "b"  # 全局变量, 后面也能取得


def fun2():
    print(e)
    # print(f)
    c = "c"  # fun()不能取得
    fun()
    d = "d"  # fun中不能取得不能取得


if __name__ == '__main__':
    e = "e"  # 可以取得
    fun2()
    f = "f"  # 不能取得
```





```python
a = "a"  # 全局变量


def fun():
    # 如果函数中存在和外界同名的变量
    # 若存在赋值操作, 则认为是一个新定义的变量
    # 会优先取函数内部的变量作为局部变量
    # 也就是说下面这个 a 是取不到全局变量的
    # print(a)
    a = "c"  # 也就是说这个a是一个局部变量
    print(a)


if __name__ == '__main__':
    print(a)
    fun()
    print(a)    # 依旧是全局变量, "a"
    a = "e"     # 这改的是全局变量
    print(a)
```







```python
for i in range(10):
    print(a)  # 全局变量
    a = i
    print(a)  # 全局变量

print(a)  # 经过for改动后的全局变量
```



### 声明全局变量

```python
def fun():
    global a # 声明全局变量需要在使用全局变量之前
    print(a)
    a = "c"  # 也就是说这个a是一个局部变量
    print(a)


if __name__ == '__main__':
    print(a)
    fun()
    print(a) # 已改动
```



## 函数返回值

```python
def p():
    print("hi")


if __name__ == '__main__':
    a = p()
    print(a)  # None
    if(a): # 非None等同于True
        print(True)
    else: # None等同于False
        print(False)

```



### 多返回值

-   Python遇到一个return就不会执行后面的语句

-   那要怎么做呢?

    ```python
    def xy(x, y):
        return x,y
    a = 2, b = 3
    a,b = xy(b,a)
    ```

-   返回值的类型可以不一样









```python
def xyz_2str(x: int, y: int, z: int):
    return str(x), str(y), str(z)


if __name__ == '__main__':
    a: int
    b: int
    c: int
    a, b, c = 1, 2, 3

    x: str
    y: str
    z: str

    x, y, z = xyz_2str(x=a, y=b, z=c)
    print(type(c), type(b), type(a))
    print(a, b, c)
    """
    <class 'int'> <class 'int'> <class 'int'>
    1 2 3
    """
    print(type(x), type(y), type(z))
    print(x, y, z)
    """
    <class 'str'> <class 'str'> <class 'str'>
    1 2 3
    """
```



## 函数说明文档

```python
def fun(a, b):
    """
    输出两个参数的值并找出最大值
    :param a: 参数一
    :param b: 参数二
    :return: 两数较大值
    """
    print(a, b)
    return max(a, b)
```

![image-20240302130556065](../../assets/Day01-%E5%87%BD%E6%95%B0/image-20240302130556065.png)

## 匿名函数

### 函数作为参数传递

```python
def fun() -> int: ...


if __name__ == '__main__':
    # 将函数作为print()的参数
    print(fun)  # <function fun at 0x0000022098EBF0D0>
    # 将函数作为type()的参数
    print(type(fun))  # <class 'function'>
    # 将函数作为str()的参数
    print(str(fun))  # <function fun at 0x0000022098EBF0D0>
```





```python
import random

def do_fun(fun: callable([[str, str], [bool, bool]])) -> int:
    """
    这是一个测试函数, 用来测试函数作为参数时\
    参数列表怎么写可以体现类型
    :param fun: fun(int,int)->[bool,bool]\
                callable表示一切可以调用的对象,\
                包括类,函数,方法. 与function的\
                区别在于, 可以指定返回值类型和参数\
                列表
    :return:
    """
    a: int = random.randint(0, 10)
    b: int = random.randint(0, 10)
    print(fun(a, b))
    return a - b


def compute(a: int, b: int) -> [bool, bool]:
    return a + b == 0, a * b == 0


def compute2(a: str, b: str) -> [bool, bool]:
    return a + b == "", a == b


if __name__ == '__main__':
    do_fun(compute)
    do_fun(compute2)
```

### lambda关键字定义匿名函数

匿名函数只能使用一次

```python
a = lambda 参数:只能有一行代码的函数体
```

不用写return语句, 默认就是return一行函数体的结果, 没有就返回None

```python
if __name__ == '__main__':
    a = lambda x, y: random.randint(x,y)
    print(type(a)) # <class 'function'>

    # 可以用来做一些简单的逻辑
    do_fun(lambda x,y:random.randint(x,y))
```

