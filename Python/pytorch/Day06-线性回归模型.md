# PyTorch

导入

```python
import torch
```

## 自动求导机制

### 启动自动求导机制

```python
x = torch.rand((5,3))
# 默认关闭自动求导机制
print(x.requires_grad) # False
# 使用更改属性的方式启动自动求导机制
x.requires_grad = True

# 通过传递参数的方式开启自动求导机制
y = torch.rand((5,3),requires_grad = True)


print(x.requires_grad,y.requires_grad) # True True
```





#### 自动求导启动的传播

```python
print(x.requires_grad,y.requires_grad) # True True
z = x + y
print(z.requires_grad) # True
```




```python
x = torch.rand((5,3),requires_grad = True)
y = torch.rand((5,3))

# 运算
z = torch.sum(x+y)
print(x.requires_grad,y.requires_grad,z.requires_grad)    
#       True            False           True
```
-   y没有被开启自动求导



## 反向传播



### 开启反向传播

```python
# 创建数据
x = torch.rand((5,3),requires_grad = True)
y = torch.rand((5,3),requires_grad = True)

# 运算
z = torch.sum(114*x + 514*y)
# 执行反向传播
z.backward()
# 查看反向传播结果
print(x.grad) # 一推114
print(y.grad) # 一推514
```


不开启自动求导但是执意要反向传播:

```python
z = x + y
print(x.requires_grad,y.requires_grad,z.requires_grad)    
#       True            False           True
z.backward() # 报错
# RuntimeError: grad can be implicitly created only for scalar outputs
```
### 是否是反向传播过程中的叶子

```python
# 运算
z = torch.sum(114*x + 514*y)
# 执行反向传播
z.backward()
# 查看反向传播结果
print(x.is_leaf) # True
print(y.is_leaf) # True
print(z.is_leaf) # False
```
### 积累

```python
z = torch.sum(x + y)
# 执行反向传播
z.backward(retain_graph=True)
print(x.grad) # 1
z.backward(retain_graph=True)
print(x.grad) # 2
z.backward(retain_graph=True)
print(x.grad) # 3 
z.backward(retain_graph=True)
print(x.grad) # 4 
z.backward(retain_graph=True)
print(x.grad) # 5 
```
-   `retain_graph`缺省为`False`
-   当`retain_graph`为`False`时, 重复做`z.backward()`, 会报错, 除非再进行如`z = torch.sum(x + y)`的计算
-   当`retain_graph`为`True`时, 反向传播的结果会累加, 即使没有进行计算

## 线性回归

```python
from torch import nn
class MyModule(nn.Module):
    
    def __init__(self,input_dim,output_dim):
        super(MyModule,self).__init__()
        self.linear = nn.Linear(
            input_dim, # 输入数据维度
            output_dim # 输出数据维度
        ) # 全连接层
    
    def forward(self,x):
        out = self.linear(x)
        return out
    
    
    def iterate(self,inputs,labels,optimizer,criterion)->float:
        """
        forward和__init__()按照上面这么写在类里
        本方法不需要强制写在类里, 单纯是我觉得这么设计更合理
        Parameters
        ----------
        inputs : Tensor
            训练数据.
        labels : Tensor
            预期值.
        optimizer : optim
            优化器.
        criterion : loss
            损失函数.

        Returns
        -------
        float
            损失.

        """
        

        # 清空上一次的迭代
        optimizer.zero_grad() 
        
        # 前向传播
        outputs = self.forward(inputs)
        
        # 计算损失
        loss = criterion(outputs,labels)
        
        # 反向传播
        loss.backward()
        
        # 更新权重函数
        optimizer.step()
        
        # 返回损失
        return loss.item()


def create_data():
    import torch
    import numpy as np
    # 准备数据
    inputs = torch.rand((100,1))
    labels = 6*inputs + 2 + torch.from_numpy(
        np.random.normal(0,0.01,size = (100,1))
    )
    labels = labels.float()
    return inputs,labels




def tensor_test():
    import torch
    inputs,labels = create_data()
    
    module = MyModule(1,1)
    print(module) # 输出模型情况
    
    # 指定优化器参数, 优化器: 优化参数/权重值做线性回归
    optimizer = torch.optim.SGD(
        module.parameters(),
        lr = 0.01 # learning rate
    ) # SGD 优化器 , 随机梯度下降
    
    # 损失函数
    criterion = nn.MSELoss()
    
    # 遍历迭代
    for i in range(5000):
        loss = module.iterate(inputs,labels,optimizer,criterion)
        if(i%100==0):
            print(loss)


if __name__ == "__main__":
    tensor_test()
```

## 模型持久化

### 保存

```python
module_file = "module.pkl"
torch.save(module.state_dict(),module_file)
```
### 读取

```python
module_file = "module.pkl"
module.load_state_dict(torch.load(),module_file)
```

只读取, 不保存, 文件就会被删除

## 使用GPU训练

>   将数据传入Cuda

```python
device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")
module.to(device)
```
还有

```python
inputs = torch.rand((100,1)).to(device)
```

-   此时由于张量太小，难以并行化计算，测试起来会比CPU慢





## 程序清单

```python
from torch import nn


class MyModule(nn.Module):

    def __init__(self, input_dim, output_dim, inputs, labels):
        super(MyModule, self).__init__()

        self.linear = nn.Linear(
            input_dim,  # 输入数据维度
            output_dim  # 输出数据维度
        )  # 全连接层

        self.__inputs = inputs
        self.__labels = labels

        import torch
        self.__device = torch.device(
            "cuda:0" if torch.cuda.is_available() else "cpu"
        )

    def forward(self, x):
        out = self.linear(x)
        return out

    def open_cuda(self):
        self.to(self.__device)
        self.__inputs = self.__inputs.to(self.__device)
        self.__labels = self.__labels.to(self.__device)
        return self

    def iterate(self, optimizer, criterion) -> float:
        """
        forward和__init__()按照上面这么写在类里
        本方法不需要强制写在类里, 单纯是我觉得这么设计更合理
        Parameters
        ----------
        optimizer : optim
            优化器.
        criterion : loss
            损失函数.

        Returns
        -------
        float
            损失.

        """

        # 清空上一次的迭代
        optimizer.zero_grad()

        # 前向传播
        outputs = self.forward(self.__inputs)

        # 计算损失
        loss = criterion(outputs, self.__labels)

        # 反向传播
        loss.backward()

        # 更新权重函数
        optimizer.step()

        # 返回损失
        return loss.item()


def create_data():
    import torch
    import numpy as np
    # 准备数据
    inputs = torch.rand((100, 1))
    labels = 6 * inputs + 2 + torch.from_numpy(
        np.random.normal(0, 0.01, size=(100, 1))
    )
    labels = labels.float()
    return inputs, labels


def tensor_test():
    import torch
    inputs, labels = create_data()

    module = MyModule(1, 1, inputs, labels)  # .open_cuda()

    print(module)  # 输出模型情况

    # 指定优化器参数, 优化器: 优化参数/权重值做线性回归
    optimizer = torch.optim.SGD(
        module.parameters(),
        lr=0.01  # learning rate
    )  # SGD 优化器 , 随机梯度下降

    # 损失函数
    criterion = nn.MSELoss()

    # 遍历迭代
    for i in range(5000):
        loss = module.iterate(optimizer, criterion)
        if (i % 100 == 0):
            print(loss)
    module_file = "module.pkl"
    torch.save(module.state_dict(), module_file)


if __name__ == "__main__":
    tensor_test()

```

