# 概述

帮助我们解决复杂计算, 完成复杂计算, 我们设计计算架构

```python
torch.__version__
```

```shell
pip install -i https://pypi.tuna.tsinghua.edu.cn/simple  --target=D:\IT_study\anaconda3\Lib jupyter
```

```
pip3 install  torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu118
```

```
tensorboard
```

```python
pip3 install -i https://pypi.tuna.tsinghua.edu.cn/simple  torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu118
```

```cmd
python -m venv myprojectenv
source myprojectenv/bin/activate  # Linux/macOS
myprojectenv\Scripts\activate.bat  # Windows
```

pip install Cython

Loss = sum((wx+b-y)^2^)/N

## 梯度下降

```python
# 伪造数据
def create_data(length = 4000):
    from numpy import random
    X = random.random(size=(length,1))
    Y = 6*X+3+random.normal(0,0.08,size = (length,1))
    return X,Y

# 梯度下降迭代
def gradient_descent( X,Y, *, learning_rate,times ):
    from numpy import mean
    # 代价函数对应的梯度函数，
    def gradient_function(w,b,X,Y):
        diff = ((w*X+b).reshape(-1,1)-Y).reshape(-1,1)
        w_gradient = mean(X*diff)
        b_gradient = mean(diff)
        return w_gradient,b_gradient
    w = b = 0
    for i in range(times):
        w_gradient,b_gradient = gradient_function(w,b, X,Y)
        w = w - learning_rate * w_gradient
        b = b - learning_rate * b_gradient
    return w,b

# 根据数据画出对应的图像
def plot(X, Y, w, b):
    import matplotlib.pyplot as plt
    ax = plt.subplot(1,1,1)
    ax.scatter(X, Y, s=1, c="pink", marker="s")
    plt.xlabel("X")
    plt.ylabel("Y")
    x = arange(0, 2)  # x的范围
    y = w * x + b #假设函数
    ax.plot(x, y)
    plt.show()

def test():
    X,Y = create_data()
    # 梯度下降,线性回归
    w,b = gradient_descent(X, Y, learning_rate = 0.1,times = 1000)
    # 打印theta值
    print('w:',w,'b:',b)
    plot(X, Y, w , b)

if __name__=="__main__":
    test()
else:
    print(__name__)
```

