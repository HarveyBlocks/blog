# 模块

其实质, 就是一个Python文件

`.py`结尾

模块能定义函数, 类, 变量

模块里也可以包含可执行代码

## 导入模块

```python
[from 模块名] import [模块 | 类 | 变量 | 函数 | * [模块2....]] [as 别名]
```

```python
from astroid.brain.brain_io import TextIOWrapper as io
```

## 使用模块

```python
import time
```

使用函数时

```python
time.sleep(20)
```

```python
from time import sleep
```

使用函数时

```python
sleep(20)
```

```python
from time import *
```

使用函数时

```python
sleep(20)
```

## 自定义模块

![image-20240303124236111](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Python/基础/Day02-模块/image-20240303124236111.png)

当读到import的时候, 就会把模块文件执行一遍

![image-20240303125524568](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Python/基础/Day02-模块/image-20240303125524568.png)

调用了函数, 就执行了函数里的内容

不想要自己写的代码在被导入的时候运行, 怎么办

![image-20240303125652825](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Python/基础/Day02-模块/image-20240303125652825.png)

**当执行文件的是当前文件时, 执行以下代码**

`__name__`是python文件自带的一个参数, 是指执行的文件

在导入该模块时, `__name__`变成了模块名

```python
def test_model():
    print("test model")

print(__name__) # model
print(type(__name__)) # <class 'str'>

if __name__ == '__main__':
    test_model()
```

### 模块内的封装

`__all__`参数, 在使用`import *`的时候起作用, 到时候只会把指定的**参数和函数**和暴露处理

![image-20240303130444700](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Python/基础/Day02-模块/image-20240303130444700.png)

```python
from model import *
from model import test_model

if __name__ == '__main__':
    test_model()
    print(PI)
```

但是依旧可以特别地import一个函数来绕过`__all__`调用它

