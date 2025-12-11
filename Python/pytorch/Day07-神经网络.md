# 神经网络

## 构建神经网络模型

```python
from util.time import time_advice

def create_data():
    import torch
    import numpy as np
    # 准备数据
    data_amount = 100000
    inputs = torch.rand((data_amount, 4))
    labels = 2 * inputs[:, 0]**2 - \
        4 * inputs[:, 1]**1.3 + \
        2 * inputs[:, 2] - \
        3 * inputs[:, 3]**0.5 + \
        6 + \
        torch.from_numpy(
                 np.random.normal(0, 0.01, size=(1, data_amount))
        )
    labels = labels.view(data_amount, 1).float()
    return inputs, labels

# 标准化, 使学习更快
def standardize(data):
    return (data - data.mean()) / data.std()

@time_advice
def my_module_test():
    import torch
    inputs, labels = create_data()
    from module.MyModule import MyModule
    module = MyModule(
         inputs.shape[1], labels.shape[1], [8]
    )  # .open_cuda()
    module.set_data(standardize(inputs), standardize(labels))
    print(module)  # 输出模型情况

    # 损失函数
    from torch import nn
    criterion = nn.MSELoss()

    # 遍历迭代
    for i in range(20):
        loss = None
        for j in range(50):
            loss = module.iterate(criterion)
        print("train loss:", float(1 - loss / labels.var())**0.5 * 100, "%")
        inputs, labels = create_data()
        module.set_data(standardize(inputs), standardize(labels))
    inputs, labels = create_data()
    loss = module.test_data(
        standardize(inputs), standardize(labels), criterion
    )
    print("test loss:", float(1 - loss / labels.var())**0.5 * 100, "%")
    module_file = "module.pkl"
    torch.save(module.state_dict(), module_file)

if __name__ == "__main__":
    tensor_test()

```

```python
from torch import nn

class MyModule(nn.Module):

    def __init__(self, inputs_dim, labels_dim, neurals: list):
        """
        创建自己的神经网络

        Parameters
        ----------
        inputs : Tensor
            训练集.
        labels : Tensor
            结果集.
        neurals : list
            神经网络的构造, 是一个一维列表.
            例如[3,4,2]
            表示隐藏层第一层有3个神经元,第二层有4个神经元,第三层有2个神经元

        Returns
        -------
        MyModule.

        """
        super(MyModule, self).__init__()

        self.__inputs_dim = inputs_dim
        self.__labels_dim = labels_dim

        # 检查GPU是否可用
        import torch
        self.__device = torch.device(
            "cuda:0" if torch.cuda.is_available() else "cpu"
        )

        # 初始化权重和偏差
        self.__weights = [
            torch.randn((inputs_dim, neurals[0]), requires_grad=True)
        ]
        self.__biases = [
            torch.randn(neurals[0], requires_grad=True)
        ]
        neurals.append(labels_dim)
        self.__depth = len(neurals)
        for i in range(1, self.__depth):
            self.__weights.append(
                torch.randn((neurals[i-1], neurals[i]), requires_grad=True)
            )
            self.__biases.append(
                torch.randn(neurals[i], requires_grad=True)
            )

    def forward(self, x):
        import torch
        hidden = x
        for i in range(self.__depth - 1):
            z = hidden.mm(self.__weights[i]) + self.__biases[i]
            hidden = torch.relu(z)
        out = hidden.mm(self.__weights[-1]) + self.__biases[-1]
        return out

    def update_weights(self, leaning_rate=0.01):
        for i in range(self.__depth):
            if self.__weights[i].grad.data.max() > 1e5:
                print(i, "weight:", self.__weights[i].grad.data.max())
                leaning_rate /= 1e7
            if self.__biases[i].grad.data.max() > 1e5:
                print(i, "biases:", self.__biases[i].grad.data.max())
                leaning_rate /= 1e7
            self.__weights[i].data -= \
                leaning_rate * self.__weights[i].grad.data
            self.__biases[i].data -= \
                leaning_rate * self.__biases[i].grad.data

    def set_data(self, inputs, labels):
        if self.__inputs_dim != inputs.shape[1]\
                or self.__labels_dim != labels.shape[1]:
            return
        self.__inputs = inputs
        self.__labels = labels

    def open_cuda(self):
        self.to(self.__device)
        self.__inputs = self.__inputs.to(self.__device)
        self.__labels = self.__labels.to(self.__device)
        return self

    def clean_grad(self):
        import torch
        for i in range(self.__depth):
            self.__weights[i].grad.data = \
                torch.zeros_like(self.__weights[i])
            self.__biases[i].grad.data = \
                torch.zeros_like(self.__biases[i])

    def iterate(self, criterion) -> float:
        """
        forward和__init__()按照上面这么写在类里
        本方法不需要强制写在类里, 单纯是我觉得这么设计更合理
        Parameters
        ----------
        criterion : loss
            损失函数.

        Returns
        -------
        float
            损失.

        """

        # 前向传播
        outputs = self.forward(self.__inputs)

        # 计算损失
        loss = criterion(outputs, self.__labels)

        # 反向传播
        loss.backward()

        # 更新权重函数
        self.update_weights()

        # 清空上一次的迭代
        self.clean_grad()

        # 返回损失
        return loss.item()

    def test_data(self, inputs, labels, criterion):

        # 前向传播
        outputs = self.forward(self.__inputs)

        # 计算损失
        loss = criterion(outputs, self.__labels)

        # 返回损失
        return loss.item()

```

## 随机Learning-rate下降

$$
x_n = init\times\frac{q^{n-1}+l}{1+l},\\
q = 1 - random\_value\\
设:\\
x_n = ax_{n-1}+b ,\\
x_n = mq^{n-1}+p\\
则: \\
\begin{cases}
m = \frac{init}{1+l}\\
p = init\times \frac{l}{1+l}
\end{cases}\\
有:\\
mq^{n}+p = a(mq^{n-1}+p)+b\\
解得:\\
\begin{cases}
a = q\\
b = p-pq
\end{cases}\\
又有:\\
\frac{l}{1+l} = \frac{last}{init} \\
解得:\\
l = \frac{last}{init - last}
$$

## 简化Module

```python
def mini_batch_test():
    inputs, labels = create_data()

    from module.MyModule import MySequentialModule
    my_sequential = MySequentialModule(inputs.shape[1], labels.shape[1], [8])
    print(my_sequential)
    my_sequential.set_data(inputs, labels)
    from torch import nn
    cost = nn.MSELoss(reduction='mean')
    losses = []
    for i in range(50):
        loss_avg = my_sequential.iterate(cost)
        losses.append(loss_avg)
        print(i, " train:", float(1 - loss_avg / labels.var())**0.5 * 100, "%")
        inputs, labels = create_data()
        my_sequential.set_data(inputs, labels)

    inputs, labels = create_data()
    loss_avg = my_sequential.test_data(inputs, labels, cost)
    print("test:", float(1 - loss_avg / labels.var())**0.5 * 100, "%")

if __name__ == "__main__":
    mini_batch_test()

```

```python
class MySequentialModule(nn.Sequential):
    def __init(self):
        # python意义不明的继承
        self._modules = {}
        self._parameters = {}
        self._backward_hooks = {}
        self._backward_pre_hooks = {}
        self._forward_hooks = {}
        self._forward_pre_hooks = {}

    def __init__(self, inputs_dim, labels_dim, neurals: list):
        super()
        self.__init()
        self.__inputs_dim = inputs_dim
        self.__labels_dim = labels_dim

        # 检查GPU是否可用
        import torch
        self.__device = torch.device(
            "cuda:0" if torch.cuda.is_available() else "cpu"
        )

        # 往模型中添加组件
        super().add_module("1", nn.Linear(inputs_dim, neurals[0]))
        super().add_module("sigmoid1", nn.Sigmoid())
        depth = len(neurals)
        for i in range(1, depth):
            super().add_module(str(i+1), nn.Linear(neurals[i-1], neurals[i]))
            super().add_module("sigmoid"+str(i+1), nn.Sigmoid())
        super().add_module(str(depth+1), nn.Linear(neurals[-1], labels_dim))

        # 添加优化器
        import torch
        self.__optimizer = torch.optim.Adam(self.parameters(), lr=0.01)

    def set_data(self, inputs, labels):
        if self.__inputs_dim != inputs.shape[1]\
                or self.__labels_dim != labels.shape[1]:
            return
        self.__inputs = inputs
        self.__labels = labels

    def open_cuda(self):
        self.to(self.__device)
        self.__inputs = self.__inputs.to(self.__device)
        self.__labels = self.__labels.to(self.__device)
        return self

    def iterate(self, criterion):
        optimizer = self.__optimizer

        batch_size = 16
        inputs = self.__inputs
        labels = self.__labels

        batch_loss = []
        # MINI-Batch方法进行训练
        for start in range(0, len(inputs), batch_size):
            end = min(len(inputs), start + batch_size)
            xx = inputs[start:end].clone().detach().requires_grad_(True)
            yy = labels[start:end].clone().detach().requires_grad_(True)

            prdiction = self(xx)
            loss = criterion(prdiction, yy)
            optimizer.zero_grad()
            loss.backward(retain_graph=True)
            optimizer.step()
            batch_loss.append(loss.data.numpy())

        import numpy as np
        return np.mean(batch_loss)

    def test_data(self, inputs, labels, criterion):
        import numpy as np
        prdiction = self(inputs)
        loss = criterion(prdiction, labels)
        self.__optimizer.zero_grad()
        return np.mean(loss.data.numpy())

```

## 分类算法

识别图片

结果是一个列表, 0-9. 看是该数字的概率

### 下载文件

```python
from pathlib import Path
import requests

DATA_PATH = Path("data")
PATH = DATA_PATH / "mnist"

PATH.mkdir(parents=True, exist_ok=True)

URL = "http://deeplearning.net/data/mnist/"
FILENAME = "mnist.pkl.gz"

if not (PATH / FILENAME).exists():
        content = requests.get(URL + FILENAME).content
        (PATH / FILENAME).open("wb").write(content)
```

### 解压文件

```pytorch
x_train = []
y_train = []
x_valid = []
y_valid = []

import pickle
import gzip

with gzip.open((PATH / FILENAME).as_posix(), "rb") as f:
        ((x_train, y_train), (x_valid, y_valid), _) = pickle.load(f, encoding="latin-1")
```

### 显示图片

```python
x_train, y_train, x_valid, y_valid = open_file()
from matplotlib import pyplot
import torch
x_train, y_train, x_valid, y_valid = map(
        torch.tensor, (x_train, y_train, x_valid, y_valid)
)
pyplot.imshow(x_train[1].view((28, 28)), cmap="gray")
print(x_train.shape)
```
### 训练

-   法一

```python
def model(x_batch_set, weights, bias):
    return x_batch_set.mm(weights) + bias
@time_advice
def classify_test():
    x_train, y_train, x_valid, y_valid = open_file()
    import torch.nn.functional as functional
    import torch
    loss_fun = functional.cross_entropy
    batch_set_size = 64
    x_batch_set = x_train[0:batch_set_size]  # a mini-batch from x
    y_batch_set = y_train[0:batch_set_size]
    weights = torch.randn(
        [x_train.shape[1], 10],
        dtype=torch.float,  requires_grad=True
    )
    bias = torch.zeros(10, requires_grad=True)
	# 训练一次
    print(loss_fun(model(x_batch_set, weights, bias), y_batch_set))

```

#### 准备数据

```python
x_train, y_train, x_valid, y_valid = open_file()
batch_set_size = 64
from torch.utils.data import TensorDataset
from torch.utils.data import DataLoader

# 组合data和target, 构建数据源
train_ds = TensorDataset(x_train, y_train)
# 将数据集分成一份一份的Mini Batch, shuffle是否洗牌
train_dl = DataLoader(train_ds, batch_size=batch_set_size, shuffle=True)

valid_ds = TensorDataset(x_valid, y_valid)
valid_dl = DataLoader(valid_ds, batch_size=batch_set_size * 2)python
```
整合成函数

```python
x_train, y_train, x_valid, y_valid = open_file()

def get_data(x_train, y_train, x_valid, y_valid, batch_set_size):
    from torch.utils.data import TensorDataset
    from torch.utils.data import DataLoader
    train_ds = TensorDataset(x_train, y_train)
    valid_ds = TensorDataset(x_valid, y_valid)
    return (
        DataLoader(train_ds, batch_size=batch_set_size, shuffle=True),
        DataLoader(valid_ds, batch_size=batch_set_size * 2),
    )

train_loader, valid_loader =\
        get_data(x_train, y_train, x_valid, y_valid, 64)
```
#### 构建模型

```python
# 构建神经网络
from torch import nn

class MnistModule(nn.Module):
    def __init__(self):
        super().__init__()
        self.hidden1 = nn.Linear(784, 128)
        self.hidden2 = nn.Linear(128, 256)
        self.out = nn.Linear(256, 10)

    def forward(self, x):
        import torch.nn.functional as functional
        x = functional.relu(self.hidden1(x))
        x = functional.relu(self.hidden2(x))
        x = self.out(x)
        return x
```
-   实例化模型

    ```python
    # 创建网络实例, 打印
    import torch.nn.functional as functional
    loss_fun = functional.cross_entropy
    net = MnistModule(functional)
    print(net)

    # 打印参数
    for name, parameter in net.named_parameters():
        print(name, parameter, parameter.size())
    opt = torch.optim.SGD(model.parameters(), lr=0.001)
    ```

#### 正向与反向传播

```python
def loss_batch(self, loss_fun, x_batch, y_batch, opt=None):
    loss = loss_fun(self(x_batch), y_batch)

    if opt is not None:
        loss.backward()
        opt.step()
        opt.zero_grad()

    return loss.item(), len(x_batch)python

```
#### 训练与测试

```python
def fit(self, steps, loss_fun, opt, train_loader, valid_loader):
    import torch
    import numpy as np
    for step in range(steps):
        self.train()   # 训练, 会使用 Normalization 和 Dropout
        for x_batch, y_batch in train_loader:
            self.loss_batch(loss_fun, x_batch, y_batch, opt)
        self.eval()    # 测试, 不会使用 Normalization 和 Dropout
        with torch.no_grad():
            losses, nums = zip(*[
                    self.loss_batch(loss_fun, x_batch, y_batch)
                    for x_batch, y_batch in valid_loader
            ])

        val_loss = np.sum(np.multiply(losses, nums)) / np.sum(nums)
        print('当前step:'+str(step), '验证集损失：'+str(val_loss))
```

#### 测试代码

```python
@time_advice
def classify_test():
    # 准备数据
    x_train, y_train, x_valid, y_valid = open_file()
    train_loader, valid_loader =\
        get_data(x_train, y_train, x_valid, y_valid, 64)
    # 创建网络实例, 打印
    net = MnistModule()
    print(net)
    loss_fun = functional.cross_entropy
    opt = torch.optim.SGD(net.parameters(), lr=0.001)

    # 训练
    net.fit(25, loss_fun, opt, train_loader, valid_loader)
```

