# with

无论异常与否, 都会关闭对象



```python
with open(file=file_name, mode="r", encoding=encoding) as file_input:
    while file_input.readable():
        print(file_input.read(1), end="")
```


## 上下文管理器

with的关闭基于上下文管理器

一句代码在执行之前的操作和执行之后的操作称为上下文

管理上下文的就是上下文管理器





一个类只要实现了`__enter()__`上文方法和`__exit__()`下文方法, 其创建的对象就是上下文管理器

`__enter__()`方法需要**返回**一个**操作文件对象**

`__exit()__`方法会在`with`执行完成之后自动执行

### 自定义上下文管理器

```python
class Context(object):
    def __init__(self, name: str):
        self.__name = name

    # 上文
    def __enter__(self):
        print('entering', self.__name)
        return self.__name

    def __exit__(self, exc_type, exc_value, traceback):
        print('exiting', self.__name)
        self.__name = None
        print(f'{self.__name} is exit')
        return


if __name__ == '__main__':
    with Context('a') as a:
        print(a)
        print(type(a))
    print("end")
    """
    entering a
    a
    <class 'str'>
    exiting a
    None is exit
    end   
    """
```





