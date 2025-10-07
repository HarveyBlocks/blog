# 异常

## 捕获异常

```python
try:
    with open(file=file_name, mode="r", encoding=encoding) as file_input:
        while file_input.readable():
            print(file_input.read(1), end="")
except FileNotFoundError:
    print("文件不存在")
except UnicodeDecodeError as ud:
    print("文件格式不正确",ud)
except Exception as e:
    print("未知异常",e)
else:
    print("没有异常")
finally:
    print("无论如果都会做")
```



一般异常都是自动抛出到上级的, 直到捕获它



## 主动抛出异常

```python
if not file_name:  # 字符串在为None和空时, bool(str)都会是False
    raise ValueError
```







## 异常的继承关系

```
BaseException
|-- SystemExit
|-- KeyboardInterrupt
|-- GeneratorExit
|-- Exception
    |-- StopIteration
    |-- AssertionError
    |-- AttributeError
    |-- ...

```

-   `BaseException` 是所有异常类的根类，是所有异常类的基类。
-   `SystemExit` 当 Python 程序退出时引发的异常。
-   `KeyboardInterrupt` 用户通过键盘(Ctrl+C)中断程序执行时引发的异常。
-   `GeneratorExit` 生成器被关闭时引发的异常。
-   `Exception` 是大多数常见异常类的基类，是用户自定义异常的推荐基类。
    -   `StopIteration` 是迭代器对象遍历结束时引发的异常。当一个迭代器没有更多的元素可以返回时，会引发 `StopIteration` 异常。
    -   `AssertionError` 当 `assert` 语句失败时引发的异常。`assert` 语句用于测试程序的断言，并且如果断言为假，则会引发 `AssertionError` 异常。
    -   `AttributeError` 当尝试访问不存在的属性或方法时引发的异常。通常用于指示对象没有特定的属性或方法。