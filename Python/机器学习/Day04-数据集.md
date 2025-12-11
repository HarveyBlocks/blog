# 数据集

## 可用数据集

数据哪里来? 企业通过用户发送的信息做整理来获取数据, 我们没有啊? 

-   scikit-learn
-   kaggle
-   UCI

### scikit-learn

实现大多数机器学习算法, 需要前置的`Numpy`和`Scipy`.....`threadpoolctl, numpy, joblib, scipy, scikit-learn`

```shell
pip3 install Scikit-learn==0.19.1
```

```shell
pip3 install -i https://pypi.tuna.tsinghua.edu.cn/simple  --target=\ProgramData\Anaconda3\Lib Scikit-learn==0.19.1
```

```shell
C:\Users\27970\AppData\Local\Programs\Python\Python311\python.exe -m pip install -i https://pypi.tuna.tsinghua.edu.cn/simple  --target=\ProgramData\Anaconda3\Lib  --upgrade numpy
```

## 获取数据集

fetch_XXX获取大数据集, load_XXX获取小数据集

数据集的返回值, 是继承自字典的Bunch, 有键值对

-   data

    特征值 , ndarry, 多维

-   target

    目标值, ndarry. 一维

-   DESCR

    数据描述

-   feature_names

    特证名, 新闻数据, 手写数字, 回归数据集没有

-   target_names

    标签名

```python
import torch
import numpy as np
from sklearn.datasets import load_iris

def datasets_demo():
    iris = load_iris()

    for string in str(iris.get("DESCR")).split("\n"):
        # print(string) # 数据输出有点问题
        pass

    data = iris.get("data")
    print(data.shape) # (150, 4)
    target_name = iris.get("target_names")
    print(target_name)  
    target = iris.get("target")
    print(target.shape) # (150,)

if __name__=="__main__":
    datasets_demo()
```

## 数据集的使用

是否需要将所有的数据集都用于训练? 

否, 一部分用来检验训练之后的效果

训练集占七到八成

-   数据集的划分
    -   `sklearn.model_selection.train_test_split(arrays, *options)`
    -   参数
        -   x 数据集的特征值
        -   y 数据集的标签值
        -   test_size , 可选, 测试集的大小, 一般为float
        -   random_state 随机数种子
    -   返回值, 次序如下
        -   训练集特征值 
        -   测试集特征值
        -   训练集目标值
        -   测试集目标值

```python
from sklearn.model_selection import train_test_split
from sklearn.utils import Bunch

def split_dataset(iris:Bunch):
    data_train,data_test,target_train, target_test = \
        train_test_split(
            iris.data,iris.target,
            test_size = 0.2 # 可选, 默认0.25
        )
    print(data_train.shape) # (120, 4)
    print(data_test.shape) # (120,)
    print(target_train.shape) # (30, 4)
    print(target_test.shape) # (30,)
    return 
```

