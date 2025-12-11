# 闭包

变量很容易被访问, 更改

## 闭包

写一个闭包, 

就是在函数里定义一个函数, 再返回这个函数, 这样, 就防止了变量被外界访问

```python
def outer(label: str):
    """
    返回了函数, 内部变量(pre_label和post_label)不会被外界访问到
    :return: 函数
    """
    pre_label: str = f"<{label}>"
    post_label: str = f"</{label}>"

    def inner(msg):
        return f"{pre_label}{msg}{post_label}"

    return inner
```

无论怎么导报, 都不能访问到内部的参数

使用函数

```python
from util import outer

if __name__ == '__main__':
    inner = outer("br")
    result:str = inner("Hello world")
    print(result)
```

## nonlocal

内部函数可以作用外部函数,但是在更改其值的时候会出现报错, 但是

```python
num1 = 100

def single(num2: int):
    global num1 # 指的是最顶层的num1
    num1 += num2
    return num1

def outer(num: int):
    num1: int = num*2
    def inner(num2):
        nonlocal num1 # 指的是外部函数的num1
        num1 -= num2
        return num1

    return inner
```

引入global和nonlocal的原因, 都是**Python无法使用`类型 变量名;`的形式声明变量**

为了解决这个问题, **python认为赋值语句附带了创建变量的功能**

如果在当前函数作用域有一个赋值语句, 且本函数作用域之前都没有这个变量的声明, 那么Python将认为这是一个**新的局部变量**并创建这个变量

但是, 照理来说, 内部函数也是外部函数作用域里的一部分, 内部函数还是有权访问到外部函数的变量的

在使用的过程中, 我们不希望仅仅是一个赋值语句就让函数内部无法访问到外部变量,反而创建了一个意想不到的内部变量

于是, 我们用`global`关键字标注这个变量指的是最顶层的变量, `nonlocal`变量指的是外层函数里变量

## 装饰器

>   面向切面增强

```python
def time_advice(fun):
    """
    增强fun函数的函数 , 进行了时间增强
    :param fun: 像被增强的函数
    :return: 增强后的函数
    """

    def inner(arg):
        """
        :param args: 被增强函数的参数
        :return: 函数返回的结构
        """
        from time import struct_time, localtime
        this_time: struct_time = localtime()
        year = this_time.tm_year
        mon = this_time.tm_mon
        mday = this_time.tm_mday
        hour = this_time.tm_hour
        min = this_time.tm_min
        sec = this_time.tm_sec
        print(f"{year % 100}-{mon}-{mday} {hour}:{min}:{sec} [{fun.__name__}]: 开始执行")
        result = fun(arg)
        print(f"{year % 100}-{mon}-{mday} {hour}:{min}:{sec} [{fun.__name__}]: 执行结束")
        return result

    return inner

def sleep(sec):
    import time
    # 导包在方法内也是可以做哒!,也是有作用域哒!
    time.sleep(sec)

if __name__ == '__main__':
    time_advice_sleep = time_advice(sleep)
    time_advice_sleep(1)
```

### 语法糖

装饰器的快捷写法

```python
# 如上
def time_advice(fun):
	pass

@time_advice
def sleep(sec):
    import time
    time.sleep(sec)

if __name__ == '__main__':
    sleep(1) # 直接走了增强
```

这么想来, 之前的`getter`和`setter`也算是python自带的一些增强了

-   工厂类

    就像Executor里面会创建各种类型的线程处理器, 统一了入口

### 时间增强工具

```python
# -*- coding: utf-8 -*-

# 时间增强工具
def mark() -> float:
    import time
    return time.time()

def formated_through_time(marked_time: float):
    import time
    through = time.time() - marked_time
    million = int(through * 1000 % 1000)
    sec = int(through % 60)
    minute = int(through // 60 % 60)
    hour = int(through // 3600) - 16
    return "{:02d}:{:02d}:{:02d}.{:03d}".format(
        hour % 24, minute, sec, million
    )

def formated_now_time():
    return formated_through_time(0)

def time_advice(fun):
    def advice():
        print(formated_now_time(), "\tmain", "开始")
        start = mark()

        fun()

        print(formated_now_time(), "\tmain", "结束")
        print("共耗时: {:.3f} s".format(mark()-start))
    return advice

```

### 日志增强工具

```python
from util.time import formated_now_time
import threading
import time
import functools

class Log():
    __lever_color_pre = {
        "DEBUG": "\033[30m",
        "INFO": "\033[32m",
        "WARN": "\033[33m",
        "ERROR": "\033[31m",
        }

    __color_post = "\033[0m"

    def __init__(self, name: str = "unknow"):
        self.__name = name
        self.__thread = threading.currentThread().getName()

    def __log_formatter(self, level: str, *msg: str):
        all_msg = ""
        for i in msg:
            for j in i:
                all_msg = all_msg + j.__str__()

        color_pre = Log.__lever_color_pre[level]
        all_msg = color_pre + all_msg + Log.__color_post
        return formated_now_time() +\
            " "+color_pre+"{:5s}\033[0m ---[{:15s}] \033[36m{:15s}\033[0m : {}"\
            .format(level, self.__thread, self.__name, all_msg)

    def error(self, *msg):
        print(self.__log_formatter("ERROR", msg))

    def debug(self,  *msg):
        print(self.__log_formatter("DEBUG", msg))

    def info(self,  *msg):
        print(self.__log_formatter("INFO", msg))

    def warn(self, *msg):
        print(self.__log_formatter("WARN", msg))

    def __str__(self):
        return "Log{ thread="+self.__thread+", name="+self.__name+" }"

def log_advice(log_name: str = "unknow"):
    def log_adviced_inner(fun):
        @functools.wraps(fun)
        def advice():
            log = Log(log_name)
            log.debug("开始")
            start = time.time()
            try:
                fun()
            except Warning as w:
                log.warn(w)
            except Exception as e:
                log.error(e)
            log.debug("结束")
            log.info("共耗时: {:.3f} s".format(time.time()-start))
        return advice
    return log_adviced_inner

```

