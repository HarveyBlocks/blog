# CNN

检测

识别

图像分割

检索(淘宝找商品)



传统神经网络, 对权重的占用空间太高

卷积其实就是一种特征提取方法

## POOL

-   MAX POOL
    -   表现地比AVG好得多
    -   我们认为(实际上也是)卷积层里最大的数据是最好的数据
-   AVG POOL

## 数字识别

###打开文件

```python
# 一个撮（批次）的大小，64张图片
def open_file(batch_size=64):
    # 训练集
    train_dataset = datasets.MNIST(root='./data',
                                   train=True,
                                   transform=transforms.ToTensor(),
                                   download=True)

    # 测试集
    test_dataset = datasets.MNIST(root='./data',
                                  train=False,
                                  transform=transforms.ToTensor())

    # 构建batch数据
    train_loader = torch.utils.data.DataLoader(dataset=train_dataset,
                                               batch_size=batch_size,
                                                shuffle=True) # 进行随机打乱
    test_loader = torch.utils.data.DataLoader(dataset=test_dataset,
                                              batch_size=batch_size,
                                              shuffle=True)
    return train_loader, test_loader
```

### 创建模块类

#### 创建卷积层

```python
nn.Sequential(         # 输入大小 (1, 28, 28)
    nn.Conv2d(
        in_channels=1,              # 接收 (28, 28, 1), 1表示灰度图
        out_channels=16,            # 要得到几多少个特征图
        kernel_size=5,              # 卷积核大小
        stride=1,                   # 步长
        padding=2,                  # 如果希望卷积后大小跟原来一样，需要设置
                                    # padding=(kernel_size-1)/2 if stride=1
    ),                              # 输出的特征图为 (16, 28, 28)
    nn.ReLU(),                      # relu层
    nn.MaxPool2d(kernel_size=2),    # 进行池化操作（2x2 区域）,
                                    # 输出结果为： (16, 14, 14)
)
```
#### 构建模块

```python
class CnnModule(nn.Module):
    def __init__(self, optimizer: callable, criterion: callable,
                 *, learning_rate=0.01):
        super(CnnModule, self).__init__()
        self.__conv1 = nn.Sequential(       # 接收 (28, 28, 1)
            nn.Conv2d(1, 16, 5, 1, 2),      # 不用关键字传参的写法
                                            # 输出 (16, 28, 28)
            nn.ReLU(),                      
            nn.MaxPool2d(2),                # 输出 (16, 14, 14)
        )

        self.__conv2 = nn.Sequential(       # 接收 (16, 14, 14)
            nn.Conv2d(16, 32, 5, 1, 2),     
                                            # 输出 (32, 14, 14)
            nn.ReLU(),                      
            nn.MaxPool2d(2),                # 输出 (32, 7, 7)
        )
        
        # 下注各个参数之间的关系
        # 传入数据带的条件
        out_width = 28  # 输出大小out_channels*out_width*out_width
        in_channel = 1
        # 构造第一层的与传入数据的参数关系
        out_channels = 16
        kernel_size = 5
        stride = 1
        pading = (kernel_size-1)//2
        pool_width = 2
        out_width = (
                (out_width - kernel_size+2*pading)//stride + 1
            )//pool_width
        # 构造第二层的参数与第一层关系
        in_channel = out_channels
        out_channels = 32
        out_width =\
            ((out_width - kernel_size+2*pading)//stride + 1)//pool_width
        fc_in_feature = out_width*out_width*out_channels
        
        
		# FC层
        self.out = nn.Linear(fc_in_feature, 10)   # 全连接层 FC 得到的结果
        # 优化器
        self.__optimizer = optimizer(self.parameters(), lr=learning_rate)
        # 评判器
        self.__criterion = criterion()

    def forward(self, x):
        x = self.__conv1(x)
        x = self.__conv2(x)

        # flatten操作
        x = x.view(x.size(0), -1)       # 结果为：(batch_size, 32 * 7 * 7)

        output = self.out(x)

        return output

```

#### 计算精确率

```python
@staticmethod
def __accuracy(predictions, labels):
    # 因为神经网路输出的是对数据辨别之后的概率值, 取出概率最大的, 作为辨别的结果
    pred = torch.max(predictions.data, 1)[1]
    # 辨别的结果与真实结果重叠部分的和, 也就是正确部分的和
    rights = pred.eq(labels.data.view_as(pred)).sum()
    return rights, len(labels) # 辨别正确数, 总数
```
#### 训练与测试逻辑

```python
def train_data(self, data, target):
    self.train()
    output = self(data)
    loss = self.__criterion(output, target)
    self.__optimizer.zero_grad()
    loss.backward()
    self.__optimizer.step()

    right = CnnModule.__accuracy(output, target)

    return loss, right

def test_data(self, test_loader):
    self.eval()
    val_rights = []
    for (data, target) in test_loader:
        output = self(data)
        right = CnnModule.__accuracy(output, target)
        val_rights.append(right)

    # 准确率计算
    return val_rights

```


### 测试

```python
# 一次循环结算一次准确率
def epoch_accuracy(rights):
    r = (
        sum([tup[0] for tup in rights]),
        sum([tup[1] for tup in rights])
    )
    return r


@time_advice
def cnn_test():
    train_loader, test_loader = open_file()
    # 实例化
    net = CnnModule(optim.Adam, nn.CrossEntropyLoss, learning_rate=0.01)
    # 开始训练循环
    num_epochs = 3  # 训练的总循环周期
    for epoch in range(num_epochs):
        # 当前epoch的结果保存下来
        train_rights = []

        for batch_idx, (data, target) in enumerate(train_loader):
            # 针对容器中的每一个批进行循环
            loss, right = net.train_data(data, target)
            train_rights.append(right)
            if batch_idx % 100 == 0:
                val_rights = net.test_data(test_loader)
                train_r = epoch_accuracy(train_rights)
                val_r = epoch_accuracy(val_rights)
                print_result(epoch, batch_idx, loss,
                             train_r, val_r, train_loader)

```

#### 测试输出

```python
def print_result(epoch, batch_idx, loss,
                 train_r, val_r, train_loader,
                 batch_size=64):
    print('''
          当前epoch: {} [{}/{}({:.0f}%)]\t损失: {:.6f}
          训练集准确率:{:.2f}%\t测试集正确率: {:.2f}%'''.format(
                  epoch,
                  (batch_idx+1) * batch_size,
                  len(train_loader.dataset),
                  100. * (batch_idx+1) / len(train_loader),
                  loss.data,
                  100. * train_r[0] / train_r[1],
                  100. * val_r[0] / val_r[1]))

```

### 程序清单

```python
import matplotlib.pyplot as plt
import torch
from torch import nn
from torch.utils.data import TensorDataset, DataLoader
from torchvision import datasets, transforms
import torch.optim as optim

from util.time import time_advice
from module.MyModule import CnnModule


# 根据数据画出对应的图像
def plot_error(error_list):
    ax = plt.subplot(1, 1, 1)

    plt.xlabel("X")
    plt.ylabel("Y")
    ax.scatter(range(len(error_list)), error_list,
               s=1, c="blue", marker="s")
    plt.show()


# 定义超参数
# input_size = 28  # 图像的总尺寸28*28
# num_classes = 10  # 标签的种类数


# 一个撮（批次）的大小，64张图片
def open_file(batch_size=64):
    # 训练集
    train_dataset = datasets.MNIST(root='./data',
                                   train=True,
                                   transform=transforms.ToTensor(),
                                   download=True)

    # 测试集
    test_dataset = datasets.MNIST(root='./data',
                                  train=False,
                                  transform=transforms.ToTensor())

    # 构建batch数据
    train_loader = torch.utils.data.DataLoader(dataset=train_dataset,
                                               batch_size=batch_size,
                                               shuffle=True)
    test_loader = torch.utils.data.DataLoader(dataset=test_dataset,
                                              batch_size=batch_size,
                                              shuffle=True)
    return train_loader, test_loader


def get_data(x_train, y_train, x_valid, y_valid, batch_set_size):
    train_ds = TensorDataset(x_train, y_train)
    valid_ds = TensorDataset(x_valid, y_valid)
    return (
        DataLoader(train_ds, batch_size=batch_set_size, shuffle=True),
        DataLoader(valid_ds, batch_size=batch_set_size * 2),
    )


def print_result(epoch, batch_idx, loss,
                 train_r, val_r, train_loader,
                 batch_size=64):
    print('''
          当前epoch: {} [{}/{}({:.1f}%)]\t损失: {:.6f}
          训练集准确率:{:.2f}%\t测试集正确率: {:.2f}%'''.format(
        epoch,
        (batch_idx + 1) * batch_size,
        len(train_loader.dataset),
        100. * (batch_idx + 1) / len(train_loader),
        loss.data,
        100. * train_r[0] / train_r[1],
        100. * val_r[0] / val_r[1]))


def epoch_accuracy(rights):
    r = (
        sum([tup[0] for tup in rights]),
        sum([tup[1] for tup in rights])
    )
    return r


@time_advice
def cnn_test():
    train_loader, test_loader = open_file()
    # 实例化
    net = CnnModule(optim.Adam, nn.CrossEntropyLoss, learning_rate=0.01)
    # 开始训练循环
    num_epochs = 1  # 训练的总循环周期
    for epoch in range(num_epochs):
        # 当前epoch的结果保存下来
        train_rights = []

        for batch_idx, (data, target) in enumerate(train_loader):
            # 针对容器中的每一个批进行循环
            loss, right = net.train_data(data, target)
            train_rights.append(right)
            if batch_idx % 100 == 0:
                val_rights = net.test_data(test_loader)
                train_r = epoch_accuracy(train_rights)
                val_r = epoch_accuracy(val_rights)
                print_result(epoch, batch_idx, loss,
                             train_r, val_r, train_loader)


if __name__ == "__main__":
    cnn_test()

```

```python
class CnnModule(nn.Module):
    def __init__(self, optimizer: callable, criterion: callable,
                 *, learning_rate=0.01):
        super(CnnModule, self).__init__()
        self.__conv1 = nn.Sequential(         # 输入大小 (1, 28, 28)
            nn.Conv2d(
                in_channels=1,              # 灰度图
                out_channels=16,            # 要得到几多少个特征图
                kernel_size=5,              # 卷积核大小
                stride=1,                   # 步长
                padding=2,                  # 如果希望卷积后大小跟原来一样，
                                            # 需要设置
                                            # padding=(kernel_size-1)/2
                                            # if stride=1
            ),                              # 输出的特征图为 (16, 28, 28)
            nn.ReLU(),                      # relu层
            nn.MaxPool2d(kernel_size=2),    # 进行池化操作（2x2 区域）,
                                            # 输出结果为： (16, 14, 14)
        )

        self.__conv2 = nn.Sequential(         # 下一个套餐的输入 (16, 14, 14)
            nn.Conv2d(16, 32, 5, 1, 2),     # 不用关键字传参的写法
                                            # 输出 (32, 14, 14)
            nn.ReLU(),                      # relu层
            nn.MaxPool2d(2),                # 输出 (32, 7, 7)
        )
        out_width = 28  # 输出大小out_channels*out_width*out_width
        in_channel = 1
        out_channels = 16
        kernel_size = 5
        stride = 1
        pading = (kernel_size-1)//2
        pool_width = 2
        out_width = (
                (out_width - kernel_size+2*pading)//stride + 1
            )//pool_width
        in_channel = out_channels
        out_channels = 32
        out_width =\
            ((out_width - kernel_size+2*pading)//stride + 1)//pool_width
        fc_in_feature = out_width*out_width*out_channels
        # print(out_width)
        self.out = nn.Linear(fc_in_feature, 10)   # 全连接层 FC 得到的结果
        self.__optimizer = optimizer(self.parameters(), lr=learning_rate)
        self.__criterion = criterion()

    def forward(self, x):
        x = self.__conv1(x)
        x = self.__conv2(x)

        # flatten操作
        x = x.view(x.size(0), -1)       # 结果为：(batch_size, 32 * 7 * 7)

        output = self.out(x)

        return output

    @staticmethod
    def __accuracy(predictions, labels):
        pred = torch.max(predictions.data, 1)[1]
        rights = pred.eq(labels.data.view_as(pred)).sum()
        return rights, len(labels)

    def train_data(self, data, target):
        self.train()
        output = self(data)
        loss = self.__criterion(output, target)
        self.__optimizer.zero_grad()
        loss.backward()
        self.__optimizer.step()

        right = CnnModule.__accuracy(output, target)

        return loss, right

    def test_data(self, test_loader):
        self.eval()
        val_rights = []
        for (data, target) in test_loader:
            output = self(data)
            right = CnnModule.__accuracy(output, target)
            val_rights.append(right)

        # 准确率计算
        return val_rights

```



## torchvision

https://pytorch.org/docs/stable/torchvision/index.html

[torchvision — Torchvision 0.17 documentation (pytorch.org)](https://pytorch.org/vision/stable/)

### 网络模块

#### 数据预处理部分：

- 数据增强：torchvision中transforms模块自带功能，比较实用
- 数据预处理：torchvision中transforms也帮我们实现好了，直接调用即可
- DataLoader模块直接读取batch数据

#### 网络模块设置：

- 加载预训练模型，torchvision中有很多经典网络架构，调用起来十分方便，并且可以用人家训练好的权重参数来继续训练，也就是所谓的迁移学习
- 需要注意的是别人训练好的任务跟咱们的可不是完全一样，需要把最后的head层改一改，一般也就是最后的全连接层，改成咱们自己的任务
- 训练时可以全部重头训练，也可以只训练最后咱们任务的层，因为前几层都是做特征提取的，本质任务目标是一致的

#### 网络模型保存与测试
- 模型保存的时候可以带有选择性，例如在验证集中如果当前效果好则保存
- 读取模型进行实际测试



## 花图像分类识别

102类的分类任务

### 图像增强

```python
def transformer():
    from torchvision.transforms import RandomRotation, Compose, \
        CenterCrop, RandomHorizontalFlip, RandomVerticalFlip, \
        ColorJitter, RandomGrayscale, ToTensor, Normalize, Resize
    # 数据增强转化器
    data_transformers = {
        'train': Compose([
            RandomRotation(45),  # 随机旋转，-45到45度之间随机选
            CenterCrop(224),  # 从中心开始裁剪
            RandomHorizontalFlip(p=0.5),  # 随机水平翻转 选择一个概率概率
            RandomVerticalFlip(p=0.5),  # 随机垂直翻转
            # 参数1为亮度，参数2为对比度，参数3为饱和度，参数4为色相
            ColorJitter(
                brightness=0.2, contrast=0.1, saturation=0.1, hue=0.1),
            RandomGrayscale(p=0.025),  # 概率转换成灰度率，3通道就是R=G=B
            ToTensor(),
            # 均值，标准差
            Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
        ]),
        'valid': Compose([
            Resize(256),    # 测试数据的大小比较随意, 需要Resize,变小
            CenterCrop(224),
            ToTensor(),
            Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
        ]),
    }
    return data_transformers

```



### 读取数据

```python
batch_size = 8
def open_file(transformer):
    import os
    from torchvision.datasets import ImageFolder
    from torch.utils.data import DataLoader
    data_dir = './flower_data/'
    image_datasets = {
        x: ImageFolder(os.path.join(data_dir, x), transformer[x])
        for x in ['train', 'valid']
    }
    data_loaders = {
        x: DataLoader(image_datasets[x], batch_size=batch_size, shuffle=True)
        for x in ['train', 'valid']
    }
    dataset_sizes = {x: len(image_datasets[x]) for x in ['train', 'valid']}
    class_name_ids = image_datasets['train'].classes
    return data_loaders, dataset_sizes, class_name_ids

```



### 读取类别

```python
def get_id2name():
    import json
    with open('flower_to_name.json', 'r') as f:
        flower_id2name = json.load(f)
    return flower_id2name

```



### 展示图片

```python
def show_image(class_name_ids, flower_id2name, data_loader):
    rows = 2
    columns = batch_size//rows
    data_iter = iter(data_loader)
    inputs, target = data_iter.__next__()

    def im_convert(tensor):
        """数据转图片"""
        image = tensor.to("cpu").clone().detach()

        image = image.numpy()
        # squeeze: 拉平, 挤压
        # image = image.squeeze()     # ?意义不明, 加和去效果一样
        # 数据还原一下, 因为torch的颜色通道在第一个[3,224,224], 不符合常规
        image = image.transpose(1, 2, 0)
        # 还原标准化
        image = image * np.array((0.229, 0.224, 0.225)) + \
            np.array((0.485, 0.456, 0.406))
        image = image.clip(0, 1)

        return image

    import matplotlib.pyplot as plt
    fig = plt.figure(figsize=(10, 6))
    for idx in range(columns * rows):
        ax = fig.add_subplot(rows, columns, idx + 1, xticks=[], yticks=[])
        # 获取该图片的花的种类名
        target_class_id = target[idx]
        class_name_id = class_name_ids[target_class_id]
        class_name_id = str(int(class_name_id))
        flower_name = flower_id2name[class_name_id]
        # 将花种类标注在图片上
        ax.set_title(flower_name)
        # 获取该图片
        image_data = inputs[idx]
        image = im_convert(image_data)
        # 展示图片
        plt.imshow(image)
    plt.show()

```



### 迁移学习

>   Fine-tuning(微调)

-   方案A : 把模型和他的权重拿来, 作为自己模型的初始化
-   方案B : 将别人的模型作为一个特征提取器(黑盒, 不改变参数, 冻结) , 自己的数据经过别人模型之后, 再**经过我们网络(FC)的学习**
    -   更快
-   数据量越多, 别人的模型冻结的越少(冻结前面的部分)

#### initialize_model函数

#####官方

-   可选的比较多, resnet, 残差神经网络
-   'resnet' 用的多
-   'alexnet'(老)
-   'vgg'(老)
-   'squeezenet'
-   'densenet'
-   'inception'

```python
def initialize_model(model_name, num_classes, feature_extract, use_weights=True):
    # 选择合适的模型，不同模型的初始化方法稍微有点区别
    model_ft = None
    input_size = 0

    if model_name == "resnet":
        """ Resnet152
        """
        model_ft = models.resnet152(weights=use_weights)
        set_parameter_requires_grad(model_ft, feature_extract)
        num_ftrs = model_ft.fc.in_features
        model_ft.fc = nn.Sequential(nn.Linear(num_ftrs, 102),
                                    nn.LogSoftmax(dim=1))
        input_size = 224

    elif model_name == "alexnet":
        """ Alexnet
        """
        model_ft = models.alexnet(weights=use_weights)
        set_parameter_requires_grad(model_ft, feature_extract)
        num_ftrs = model_ft.classifier[6].in_features
        model_ft.classifier[6] = nn.Linear(num_ftrs,num_classes)
        input_size = 224

    elif model_name == "vgg":
        """ VGG11_bn
        """
        model_ft = models.vgg16(weights=use_weightss)
        set_parameter_requires_grad(model_ft, feature_extract)
        num_ftrs = model_ft.classifier[6].in_features
        model_ft.classifier[6] = nn.Linear(num_ftrs,num_classes)
        input_size = 224

    elif model_name == "squeezenet":
        """ Squeezenet
        """
        model_ft = models.squeezenet1_0(weights=use_weights)
        set_parameter_requires_grad(model_ft, feature_extract)
        model_ft.classifier[1] = nn.Conv2d(512, num_classes, kernel_size=(1,1), stride=(1,1))
        model_ft.num_classes = num_classes
        input_size = 224

    elif model_name == "densenet":
        """ Densenet
        """
        model_ft = models.densenet121(weights=use_weights)
        set_parameter_requires_grad(model_ft, feature_extract)
        num_ftrs = model_ft.classifier.in_features
        model_ft.classifier = nn.Linear(num_ftrs, num_classes)
        input_size = 224

    elif model_name == "inception":
        """ Inception v3
        Be careful, expects (299,299) sized images and has auxiliary output
        """
        set_parameter_requires_grad(model_ft, feature_extract)
        # Handle the auxilary net
        num_ftrs = model_ft.AuxLogits.fc.in_features
        model_ft.AuxLogits.fc = nn.Linear(num_ftrs, num_classes)
        # Handle the primary net
        num_ftrs = model_ft.fc.in_features
        model_ft.fc = nn.Linear(num_ftrs,num_classes)
        input_size = 299

    else:
        print("Invalid model name, exiting...")
        exit()

    return model_ft, input_size
```

####resnet初始函数

本次我们的`model_name`是`'resnet'`

```python
def initialize_resnet(classes_count, feature_extract, use_weights=True):
    """
    Resnet152 残差神经网络, 152层
    """
    from torchvision import models
    # 检查是否下载了模型, 没下载自动下载, 下载了不重复下载
    use_weights = models.ResNet152_Weights\
        .IMAGENET1K_V1 if use_weights else None
    model_ft = models.resnet152(weights=use_weights)
    # [下载路径](C:\Users\27970\.cache\torch\hub\checkpoints)

    def set_parameter_requires_grad(model, feature_extracting):
        """
        冻结神经网络
        """
        if feature_extracting:
            for param in model.parameters():
                param.requires_grad = False

    set_parameter_requires_grad(model_ft, feature_extract)

    num_ftrs = model_ft.fc.in_features
    model_ft.fc = nn.Sequential(nn.Linear(num_ftrs, classes_count),
                                nn.LogSoftmax(dim=1))
    input_size = 224

    return model_ft, input_size
```


### 训练

训练

```python
def train_model(model, data_loaders, filename,
                num_epochs=1, is_inception=False, feature_extract=True):
    log = Log("train_model")

    params_to_update = update_params(model, feature_extract)
    # 优化器选择
    optimizer = optim.Adam(params_to_update, lr=1e-2)
    # 学习率每7个epoch衰减成原来的1/10
    scheduler = optim.lr_scheduler\
        .StepLR(optimizer, step_size=7, gamma=0.1)

    # 最后一层已经LogSoftmax()了，所以不能nn.CrossEntropyLoss()来计算了
    # nn.CrossEntropyLoss()相当于logSoftmax()和nn.NLLLoss()整合
    criterion = nn.NLLLoss()

    from util.time import mark

    since = mark()
    best_acc = 0    # 通过率最佳记录

    # GPU计算要放在加载模型之前, 原因未知
    train_on_gpu = torch.cuda.is_available()
    device = torch.device("cuda:0" if train_on_gpu else "cpu")
    model = model.to(device)

    try:
        # 读取上次训练的记录点
        checkpoint = torch.load(filename)   # 记录点
        best_acc = checkpoint['best_acc']   # 最佳通过率
        model.load_state_dict(checkpoint['state_dict'])     # 模型参数
        optimizer.load_state_dict(checkpoint['optimizer'])  # 优化器参数
        log.warn("将使用已存在的模型")
    except FileNotFoundError:
        log.warn("未找到上次训练的模型, 将从头训练")

    val_acc_history = []    # 测试通过率
    train_acc_history = []  # 训练通过率
    train_losses = []       # 训练损失
    valid_losses = []       # 测试损失

    LRs = [optimizer.param_groups[0]['lr']]
    import copy
    # 最好一次训练的参数
    best_model_wts = copy.deepcopy(model.state_dict())

    for epoch in range(num_epochs):
        ...

    # 输出总消耗时间和最高通过率
    time_elapsed = time.time() - since
    log.info('Training complete in {:.0f}m {:.0f}s'.format(
        time_elapsed // 60, time_elapsed % 60))
    log.info('Best val Acc: {:4f}'.format(best_acc))

    # 训练完后用最好的一次当做模型最终的结果
    model.load_state_dict(best_model_wts)
    return model, LRs,\
        val_acc_history, train_acc_history,\
        valid_losses, train_losses
```



一轮训练:

```python
# 输出进度
log.info('Epoch {}/{}'.format(epoch, num_epochs - 1))
log.info('-' * 5 + str(epoch) + '-' * 5)

# 训练和验证
for phase in ['train', 'valid']:
    log.info('=' * 5 + phase + '=' * 5)
    if phase == 'train':
        model.train()  # 打开训练模式
    else:
        model.eval()   # 验证模式

    running_loss = 0.0      # 损失
    running_corrects = 0    # 准确个数

    # 把数据都取个遍
    for inputs, labels in data_loaders[phase]:
        # 一次是一个mini batch
		...

    # 结果的计算与输出
    dataset_len = len(data_loaders[phase].dataset)
    epoch_loss = running_loss / dataset_len
    epoch_acc = running_corrects.double() / dataset_len

    # 计算时间
    time_elapsed = time.time() - since
    log.info('Time elapsed {:.0f}m {:.0f}s'.format(
        time_elapsed // 60, time_elapsed % 60))

    # 输出损失与通过率
    log.info('{} Loss: {:.4f} Acc: {:.4f}'.format(
        phase, epoch_loss, epoch_acc))

    # 得到最好那次测试的模型
    if phase == 'valid' and epoch_acc > best_acc:
        best_acc = epoch_acc
        best_model_wts = copy.deepcopy(model.state_dict())
        state = {
          'state_dict': model.state_dict(),
          'best_acc': best_acc,
          'optimizer': optimizer.state_dict(),
        }
        # 存储模型
        log.info("模型存储中...")
        torch.save(state, filename)
        log.info("模型存储完成")
    # 记录每次循环的训练结果
    if phase == 'valid':
        val_acc_history.append(epoch_acc)
        valid_losses.append(epoch_loss)
        scheduler.step()  # epoch_loss
    if phase == 'train':
        train_acc_history.append(epoch_acc)
        train_losses.append(epoch_loss)

# 输出学习率
LRs.append(optimizer.param_groups[0]['lr'])
log.debug('Optimizer learning rate : {:.7f}'.format(LRs[-1]))
log.debug()
```


一次mini batch

```python
inputs = inputs.to(device)
labels = labels.to(device)

# 清零
optimizer.zero_grad()
# 只有训练的时候计算和更新梯度
with torch.set_grad_enabled(phase == 'train'):
    if is_inception and phase == 'train':
        # aux_outputs, 辅助输出, 在网络中间输出一次
        # resnet没有这个辅助输出, 其他有些网络有(model_name=="inception")有
        outputs, aux_outputs = model(inputs)
        loss1 = criterion(outputs, labels)
        loss2 = criterion(aux_outputs, labels)
        loss = loss1 + 0.4*loss2
    else:
        # resnet执行的是这里
        outputs = model(inputs)
        loss = criterion(outputs, labels)

    # 返回张量的最大值和其索引, 1表示: 对一维方面找最大值
    # 要忽略返回值可以写`_, preds = torch.max(outputs, 1)`
    max_value, preds = torch.max(outputs, 1)
    # log.debug("最大值=", max_value)
    # log.debug("最大值索引=", preds)
    if phase == 'train':
        # 训练阶段更新权重
        loss.backward()
        optimizer.step()

# 计算损失, running_loss: 一次 Batch 的总计损失之和
running_loss += loss.item() * inputs.size(0)
running_corrects += torch.sum(preds == labels.data)
```


程序清单

```python
def train_model(model, data_loaders, filename,
                num_epochs=25, is_inception=False, feature_extract=True):
    log = Log("train_model")

    params_to_update = update_params(model, feature_extract)
    # 优化器选择
    optimizer = optim.Adam(params_to_update, lr=1e-2)
    # 学习率每7个epoch衰减成原来的1/10
    scheduler = optim.lr_scheduler\
        .StepLR(optimizer, step_size=7, gamma=0.1)

    # 最后一层已经LogSoftmax()了，所以不能nn.CrossEntropyLoss()来计算了
    # nn.CrossEntropyLoss()相当于logSoftmax()和nn.NLLLoss()整合
    criterion = nn.NLLLoss()

    from util.time import mark

    since = mark()
    best_acc = 0    # 通过率最佳记录

    train_on_gpu = torch.cuda.is_available()
    device = torch.device("cuda:0" if train_on_gpu else "cpu")
    model = model.to(device)

    try:
        # 读取上次训练的记录点
        checkpoint = torch.load(filename)   # 记录点
        best_acc = checkpoint['best_acc']   # 最佳通过率
        model.load_state_dict(checkpoint['state_dict'])     # 模型参数
        optimizer.load_state_dict(checkpoint['optimizer'])  # 优化器参数
        log.warn("将使用已存在的模型")
    except FileNotFoundError:
        log.warn("未找到上次训练的模型, 将从头训练")

    val_acc_history = []    # 测试通过率
    train_acc_history = []  # 训练通过率
    train_losses = []       # 训练损失
    valid_losses = []       # 测试损失

    LRs = [optimizer.param_groups[0]['lr']]
    import copy
    # 最好一次训练的参数
    best_model_wts = copy.deepcopy(model.state_dict())

    for epoch in range(num_epochs):
        # 输出进度
        log.info('Epoch {}/{}'.format(epoch, num_epochs - 1))
        log.info('-' * 5 + str(epoch) + '-' * 5)

        # 训练和验证
        for phase in ['train', 'valid']:
            log.info('=' * 5 + phase + '=' * 5)
            if phase == 'train':
                model.train()  # 打开训练模式
            else:
                model.eval()   # 验证模式

            running_loss = 0.0      # 损失
            running_corrects = 0    # 准确个数

            count = 64/batch_size
            # 把数据都取个遍
            for inputs, labels in data_loaders[phase]:
                count -= 1
                if count == 0:
                    #  心跳检测
                    log.debug("ping")
                    count = 64/batch_size
                inputs = inputs.cuda()
                labels = labels.cuda()

                # 清零
                optimizer.zero_grad()
                # 只有训练的时候计算和更新梯度
                with torch.set_grad_enabled(phase == 'train'):
                    if is_inception and phase == 'train':
                        # aux_outputs, 辅助输出, 在网络中间输出一次
                        outputs, aux_outputs = model(inputs)
                        loss1 = criterion(outputs, labels)
                        loss2 = criterion(aux_outputs, labels)
                        loss = loss1 + 0.4*loss2
                    else:
                        # resnet执行的是这里
                        outputs = model(inputs)
                        loss = criterion(outputs, labels)

                    # 返回张量的最大值和其索引, 1表示: 对一维方面找最大值
                    # 要忽略返回值可以写`_, preds = torch.max(outputs, 1)`
                    max_value, preds = torch.max(outputs, 1)
                    # log.debug("最大值=", max_value)
                    # log.debug("最大值索引=", preds)
                    if phase == 'train':
                        # 训练阶段更新权重
                        loss.backward()
                        optimizer.step()

                # 计算损失, running_loss: 一次 Batch 的总计损失之和
                running_loss += loss.item() * inputs.size(0)
                running_corrects += torch.sum(preds == labels.data)

            # 结果的计算与输出
            dataset_len = len(data_loaders[phase].dataset)
            epoch_loss = running_loss / dataset_len
            epoch_acc = running_corrects.double() / dataset_len

            # 计算时间
            time_elapsed = time.time() - since
            log.info('Time elapsed {:.0f}m {:.0f}s'.format(
                time_elapsed // 60, time_elapsed % 60))

            # 输出损失与通过率
            log.info('{} Loss: {:.4f} Acc: {:.4f}'.format(
                phase, epoch_loss, epoch_acc))

            # 得到最好那次测试的模型
            if phase == 'valid' and epoch_acc > best_acc:
                best_acc = epoch_acc
                best_model_wts = copy.deepcopy(model.state_dict())
                state = {
                  'state_dict': model.state_dict(),
                  'best_acc': best_acc,
                  'optimizer': optimizer.state_dict(),
                }
                # 存储模型
                log.info("模型存储中...")
                torch.save(state, filename)
                log.info("模型存储完成")
            # 记录每次循环的训练结果
            if phase == 'valid':
                val_acc_history.append(epoch_acc)
                valid_losses.append(epoch_loss)
                scheduler.step()  # epoch_loss
            if phase == 'train':
                train_acc_history.append(epoch_acc)
                train_losses.append(epoch_loss)

        # 输出学习率
        LRs.append(optimizer.param_groups[0]['lr'])
        log.debug('Optimizer learning rate : {:.7f}'.format(LRs[-1]))
        log.debug()

    # 输出总消耗时间和最高通过率
    time_elapsed = time.time() - since
    log.info('Training complete in {:.0f}m {:.0f}s'.format(
        time_elapsed // 60, time_elapsed % 60))
    log.info('Best val Acc: {:4f}'.format(best_acc))

    # 训练完后用最好的一次当做模型最终的结果
    model.load_state_dict(best_model_wts)
    return model, LRs,\
        val_acc_history, train_acc_history,\
        valid_losses, train_losses
```

### 整体

```python
@log_advice("classify_image")
def classify_image():
    log = Log("classify_image")
    # class_name_ids: 花名ID, 将花名数字化, 便于训练
    data_loaders, dataset_sizes, class_name_ids = open_file()
    log.info("dataset_sizes: "+dataset_sizes.__str__())
    # {'train': 6552, 'valid': 818}

    # flower_id2name = get_id2name() # ID和种类名的对应关系

    # 是否用人家训练好的特征来做
    feature_extract = True
    model_ft, input_size = initialize_resnet(
        len(class_name_ids), feature_extract, use_weights=True)
    log.info(input_size)   # 224

    # print(model_ft)     # out_features=102

    # 模型保存
    filename = './cnn_plus_check_point.pth'
    model, val_acc_history, train_acc_history,\
        valid_losses, train_losses, LRs =\
        train_model(model_ft, data_loaders, filename,
                    feature_extract=feature_extract)
```



模型的device

```python
def check_device(stadard_device):
    def cal_gpu(module, model_devices):
        if isinstance(module, torch.nn.DataParallel):
            module = module.module
        for submodule in module.children():
            if hasattr(submodule, "_parameters"):
                parameters = submodule._parameters
                if "weight" in parameters:
                    model_devices.append(parameters["weight"].device)
            cal_gpu(submodule, model_devices)

    model_devices = []
    cal_gpu(model, model_devices)
    for model_device in model_devices:
        if stadard_device != model_device:
            return False
    return True

print(check_device(device))    # TODO
print(inputs.device)
print(labels.device)
```




###训练所有层

自己的全连接层只有一层, 进步空间有限

所以训练所有层, 榨干所有参数

```python
for param in model_ft.parameters():
    param.requires_grad = True

# 再继续训练所有的参数，学习率调小一点
optimizer = optim.Adam(params_to_update, lr=1e-4)
scheduler = optim.lr_scheduler.StepLR(optimizer_ft, step_size=7, gamma=0.1)

# 损失函数
criterion = nn.NLLLoss()
```

```python
# Load the checkpoint

checkpoint = torch.load(filename)
best_acc = checkpoint['best_acc']
model_ft.load_state_dict(checkpoint['state_dict'])
optimizer.load_state_dict(checkpoint['optimizer'])
#model_ft.class_to_idx = checkpoint['mapping']
```

```python
model_ft, val_acc_history, train_acc_history, valid_losses, train_losses, LRs  = train_model(model_ft, dataloaders, criterion, optimizer, num_epochs=10, is_inception=(model_name=="inception"))
```







### 测试

-   针对测试的显示图片改编(兼容普通的显示图片)

    ```python
    def show_image(class_name_ids, flower_id2name, inputs, target, preds=None):
        def im_convert(tensor):
            """ 展示数据"""
            image = tensor.to("cpu").clone().detach()
    
            image = image.numpy()
            # squeeze: 拉平, 挤压
            # image = image.squeeze()     # ?意义不明, 加和去效果一样
            # 数据还原一下, 因为torch的颜色通道在第一个[3,224,224], 不符合常规
            image = image.transpose(1, 2, 0)
            # 还原标准化
            image = image * np.array((0.229, 0.224, 0.225)) + \
                np.array((0.485, 0.456, 0.406))
            image = image.clip(0, 1)
    
            return image
        rows = 2
        columns = batch_size//rows
        import matplotlib.pyplot as plt
        fig = plt.figure(figsize=(14, 5))
        for idx in range(columns * rows):
            ax = fig.add_subplot(rows, columns, idx + 1, xticks=[], yticks=[])
            # 获取该图片的花的种类名
            target_class_id = target[idx]
            class_name_id = class_name_ids[target_class_id]
            class_name_id = str(int(class_name_id))
            flower_name = flower_id2name[class_name_id]
            # 将花种类标注在图片上
            ax.set_title(flower_name)
            # 获取该图片
            image_data = inputs[idx]
            image = im_convert(image_data)
            # 展示图片
            plt.imshow(image)
            # 展示花名
            if preds is not None:
                preds_flower = flower_id2name[str(preds[idx])]
                true_flower = flower_id2name[str(target[idx].item())]
                correct = preds_flower == true_flower
                ax.set_title("{} ({})".format(preds_flower, true_flower),
                             color=("green" if correct else "red"))
    
        plt.show()
    ```

-   测试函数

    ```python
    def test_model(model, test_loader, class_name_ids, flower_id2name):
        data_iter = iter(test_loader)
        inputs, target = data_iter.__next__()
        # 强行使用CPU
        temp_model = model.cpu()
        inputs = inputs.cpu()
        temp_model.eval()
        # 测试数据
        output = temp_model(inputs)
        # 得到概率最大的那一个
        _, preds_tensor = torch.max(output, 1)
        # numpy的转化一定要用的是CPU
        # preds_tensor = preds_tensor.cpu()
        preds = np.squeeze(preds_tensor.numpy())
        show_image(class_name_ids, flower_id2name, inputs, target, preds)
    ```

-   使用方法

    ```python
    data_loaders, dataset_sizes, class_name_ids = open_file()
    test_loader = data_loaders['valid']
    flower_id2name = get_id2name()
    model_ft, input_size = initialize_resnet(
        len(class_name_ids), feature_extract=True, use_weights=True)
    
    # GPU模式
    train_on_gpu = True
    device = torch.device("cuda:0")
    model = model_ft.to(device)
    
    # 保存文件的名字
    filename = './cnn_plus_check_point.pth'
    
    # 加载模型
    checkpoint = torch.load(filename)
    best_acc = checkpoint['best_acc']
    model.load_state_dict(checkpoint['state_dict'])
    
    test_model(model, test_loader, class_name_ids, flower_id2name)
    ```
    也可以在训练之后来一波测试





### 程序清单

```python
import numpy as np
from torch import nn
import torch.nn.functional as functional
import torch
import torch.optim as optim
from util.log import Log, log_advice


batch_size = 16
image_size = (3, 224, 224)


def plot_error(error_list):
    """
    根据数据画出对应的图像

    Parameters
    ----------
    error_list : list
        输入.

    Returns
    -------
    None.

    """
    import matplotlib.pyplot as plt
    ax = plt.subplot(1, 1, 1)

    plt.xlabel("X")
    plt.ylabel("Y")
    ax.scatter(range(len(error_list)), error_list,
               s=1, c="blue", marker="s")
    plt.show()


def show_image(class_name_ids, flower_id2name, inputs, target, preds=None):
    def im_convert(tensor):
        """ 展示数据"""
        image = tensor.to("cpu").clone().detach()

        image = image.numpy()
        # squeeze: 拉平, 挤压
        # image = image.squeeze()     # ?意义不明, 加和去效果一样
        # 数据还原一下, 因为torch的颜色通道在第一个[3,224,224], 不符合常规
        image = image.transpose(1, 2, 0)
        # 还原标准化
        image = image * np.array((0.229, 0.224, 0.225)) + \
            np.array((0.485, 0.456, 0.406))
        image = image.clip(0, 1)

        return image
    rows = 2
    columns = batch_size//rows
    import matplotlib.pyplot as plt
    fig = plt.figure(figsize=(14, 5))
    for idx in range(columns * rows):
        ax = fig.add_subplot(rows, columns, idx + 1, xticks=[], yticks=[])
        # 获取该图片的花的种类名
        target_class_id = target[idx]
        class_name_id = class_name_ids[target_class_id]
        class_name_id = str(int(class_name_id))
        flower_name = flower_id2name[class_name_id]
        # 将花种类标注在图片上
        ax.set_title(flower_name)
        # 获取该图片
        image_data = inputs[idx]
        image = im_convert(image_data)
        # 展示图片
        plt.imshow(image)
        # 展示花名
        if preds is not None:
            preds_flower = flower_id2name[str(preds[idx])]
            true_flower = flower_id2name[str(target[idx].item())]
            correct = preds_flower == true_flower
            ax.set_title("{} ({})".format(preds_flower, true_flower),
                         color=("green" if correct else "red"))

    plt.show()


def open_file():

    def transformer():
        from torchvision.transforms import RandomRotation, Compose, \
            CenterCrop, RandomHorizontalFlip, RandomVerticalFlip, \
            ColorJitter, RandomGrayscale, ToTensor, Normalize, Resize
        # 数据增强转化器
        data_transformers = {
            'train': Compose([
                RandomRotation(45),  # 随机旋转，-45到45度之间随机选
                CenterCrop(image_size[1]),  # 从中心开始裁剪
                RandomHorizontalFlip(p=0.5),  # 随机水平翻转 选择一个概率概率
                RandomVerticalFlip(p=0.5),  # 随机垂直翻转
                # 参数1为亮度，参数2为对比度，参数3为饱和度，参数4为色相
                ColorJitter(
                    brightness=0.2, contrast=0.1, saturation=0.1, hue=0.1),
                RandomGrayscale(p=0.025),  # 概率转换成灰度率，3通道就是R=G=B
                ToTensor(),
                # 均值，标准差, 这些参数是别人训练出来的
                Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
            ]),
            'valid': Compose([
                Resize(256),  # 测试数据的大小比较随意, 需要Resize,变小
                CenterCrop(image_size[1]),
                ToTensor(),
                Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
            ]),
        }
        return data_transformers

    image_transformer = transformer()

    import os
    from torchvision.datasets import ImageFolder
    from torch.utils.data import DataLoader
    data_dir = './flower_data/'
    image_datasets = {
        x: ImageFolder(os.path.join(data_dir, x), image_transformer[x])
        for x in ['train', 'valid']
    }
    data_loaders = {
        x: DataLoader(image_datasets[x], batch_size=batch_size, shuffle=True)
        for x in ['train', 'valid']
    }
    dataset_sizes = {x: len(image_datasets[x]) for x in ['train', 'valid']}
    class_name_ids = image_datasets['train'].classes
    return data_loaders, dataset_sizes, class_name_ids


def get_id2name():
    import json
    with open('flower_to_name.json', 'r') as f:
        flower_id2name = json.load(f)
    return flower_id2name


@log_advice("test_cnn_plus")
def test_cnn_plus():

    # class_name_ids: 花名ID, 将花名数字化, 便于训练
    data_loaders, dataset_sizes, class_name_ids = open_file()
    flower_id2name = get_id2name()  # ID和种类名的对应关系
    print("dataset_sizes: ", dataset_sizes)    # {'train': 6552, 'valid': 818}
    train_loader = data_loaders['train']
    valid_loader = data_loaders['valid']
    input_dim = image_size[1]
    output_dim = len(class_name_ids)
    net = CnnPlusModule(optim.Adam, nn.CrossEntropyLoss,
                        input_dim, output_dim,
                        neurals=[[16, 32, 64, 128, 256], [8, 16, 8]],
                        learning_rate=0.01)
    # print(CnnPlusModule.device)
    net = net.to(CnnPlusModule.device)
    # net.__str__()
    valid_iter = iter(valid_loader)
    inputs, target = valid_iter.__next__()
    show_image(class_name_ids, flower_id2name, inputs, target)
    inputs, target = valid_iter.__next__()
    show_image(class_name_ids, flower_id2name, inputs, target)
    for batch_idx, (data, target) in enumerate(train_loader):
        data = data.to(CnnPlusModule.device)
        target = target.to(CnnPlusModule.device)
        # print(batch_idx, data[batch_idx].size(), target[batch_idx])
        break


def initialize_resnet(classes_count, feature_extract, use_weights=True):
    """
    Resnet152 残差神经网络, 152层
    """
    from torchvision import models
    # 检查是否下载了模型, 没下载自动下载, 下载了不重复下载
    use_weights = models.ResNet152_Weights\
        .IMAGENET1K_V1 if use_weights else None
    model_ft = models.resnet152(weights=use_weights)
    # [下载路径](C:\Users\27970\.cache\torch\hub\checkpoints)

    def set_parameter_requires_grad(model, feature_extracting):
        """
        冻结神经网络
        """
        if feature_extracting:
            for param in model.parameters():
                param.requires_grad = False

    set_parameter_requires_grad(model_ft, feature_extract)

    num_ftrs = model_ft.fc.in_features
    model_ft.fc = nn.Sequential(nn.Linear(num_ftrs, classes_count),
                                nn.LogSoftmax(dim=1))
    input_size = 224

    return model_ft, input_size


def update_params(model_ft, feature_extract):
    log = Log("update_params")
    # 是否训练所有层
    params_to_update = model_ft.parameters()
    log.info("需要学习, 改变的权重的:")
    if feature_extract:
        params_to_update = []
        for name, param in model_ft.named_parameters():
            if param.requires_grad:
                params_to_update.append(param)
                log.info("\t"+name)
    else:
        for name, param in model_ft.named_parameters():
            if param.requires_grad:
                params_to_update.append(param)
                log.info("\t"+name)
    return params_to_update


def check_device(model, stadard_device):
    def cal_gpu(module, model_devices):
        if isinstance(module, torch.nn.DataParallel):
            module = module.module
        for submodule in module.children():
            if hasattr(submodule, "_parameters"):
                parameters = submodule._parameters
                if "weight" in parameters:
                    model_devices.append(
                        parameters["weight"].device)
            cal_gpu(submodule, model_devices)

    model_devices = []
    cal_gpu(model, model_devices)
    for model_device in model_devices:
        if stadard_device != model_device:
            return False
    return True


def test_model(model, test_loader, class_name_ids, flower_id2name):
    data_iter = iter(test_loader)
    inputs, target = data_iter.__next__()
    # 强行使用CPU
    temp_model = model.cpu()
    inputs = inputs.cpu()
    temp_model.eval()
    # 测试数据
    output = temp_model(inputs)
    # 得到概率最大的那一个
    _, preds_tensor = torch.max(output, 1)
    # numpy的转化一定要用的是CPU
    # preds_tensor = preds_tensor.cpu()
    preds = np.squeeze(preds_tensor.numpy())
    show_image(class_name_ids, flower_id2name, inputs, target, preds)


@log_advice("classify_image")
def classify_image():
    log = Log("classify_image")
    # class_name_ids: 花名ID, 将花名数字化, 便于训练
    data_loaders, dataset_sizes, class_name_ids = open_file()
    log.info("dataset_sizes: "+dataset_sizes.__str__())
    # {'train': 6552, 'valid': 818}

    # 是否用人家训练好的特征来做
    feature_extract = True
    model_ft, input_size = initialize_resnet(
        len(class_name_ids), feature_extract, use_weights=True)
    log.info(input_size)   # 224

    # print(model_ft)     # out_features=102

    # 模型保存
    filename = './cnn_plus_check_point.pth'
    model, val_acc_history, train_acc_history,\
        valid_losses, train_losses, LRs =\
        train_model(model_ft, data_loaders, filename,
                    feature_extract=feature_extract)
    # 每次训练完, 来一波测试
    flower_id2name = get_id2name()  # ID和种类名的对应关系
    test_model(model, data_loaders['valid'], class_name_ids, flower_id2name)


def train_model(model, data_loaders, filename,
                num_epochs=1, is_inception=False, feature_extract=True):
    log = Log("train_model")

    params_to_update = update_params(model, feature_extract)
    # 优化器选择
    optimizer = optim.Adam(params_to_update, lr=1e-2)
    # 学习率每7个epoch衰减成原来的1/10
    scheduler = optim.lr_scheduler\
        .StepLR(optimizer, step_size=7, gamma=0.1)

    # 最后一层已经LogSoftmax()了，所以不能nn.CrossEntropyLoss()来计算了
    # nn.CrossEntropyLoss()相当于logSoftmax()和nn.NLLLoss()整合
    criterion = nn.NLLLoss()

    from util.time import mark

    since = mark()
    best_acc = 0    # 通过率最佳记录

    train_on_gpu = torch.cuda.is_available()
    device = torch.device("cuda:0" if train_on_gpu else "cpu")
    model = model.to(device)
    try:
        # 读取上次训练的记录点
        checkpoint = torch.load(filename)   # 记录点
        best_acc = checkpoint['best_acc']   # 最佳通过率
        model.load_state_dict(checkpoint['state_dict'])     # 模型参数
        optimizer.load_state_dict(checkpoint['optimizer'])  # 优化器参数
        log.warn("将使用已存在的模型")
    except FileNotFoundError:
        log.warn("未找到上次训练的模型, 将从头训练")

    val_acc_history = []    # 测试通过率
    train_acc_history = []  # 训练通过率
    train_losses = []       # 训练损失
    valid_losses = []       # 测试损失

    LRs = [optimizer.param_groups[0]['lr']]
    import copy
    # 最好一次训练的参数
    best_model_wts = copy.deepcopy(model.state_dict())

    for epoch in range(num_epochs):
        # 输出进度
        log.info('Epoch {}/{}'.format(epoch, num_epochs - 1))
        log.info('-' * 5 + str(epoch) + '-' * 5)

        # 训练和验证
        for phase in ['train', 'valid']:
            log.info('=' * 5 + phase + '=' * 5)
            if phase == 'train':
                model.train()  # 打开训练模式
            else:
                model.eval()   # 验证模式

            running_loss = 0.0      # 损失
            running_corrects = 0    # 准确个数

            count = 128
            # 把数据都取个遍
            for inputs, labels in data_loaders[phase]:
                count -= 1
                if count == 0:
                    #  心跳检测
                    log.debug("ping")
                    count = 64/batch_size
                inputs = inputs.cuda()
                labels = labels.cuda()
                # log.debug("inputs: ", inputs.device)  # 输出：cuda:0
                # log.debug("labels: ", labels.device)  # 输出：cuda:0
                # log.debug("model: ", next(model.parameters()).device)
                # 输出：cuda:0
                # 清零
                optimizer.zero_grad()
                # 只有训练的时候计算和更新梯度
                with torch.set_grad_enabled(phase == 'train'):
                    if is_inception and phase == 'train':
                        # aux_outputs, 辅助输出, 在网络中间输出一次
                        outputs, aux_outputs = model(inputs)
                        loss1 = criterion(outputs, labels)
                        loss2 = criterion(aux_outputs, labels)
                        loss = loss1 + 0.4*loss2
                    else:
                        # resnet执行的是这里
                        outputs = model(inputs)
                        loss = criterion(outputs, labels)

                    # 返回张量的最大值和其索引, 1表示: 对一维方面找最大值
                    # 要忽略返回值可以写`_, preds = torch.max(outputs, 1)`
                    max_value, preds = torch.max(outputs, 1)
                    # log.debug("最大值=", max_value)
                    # log.debug("最大值索引=", preds)

                    if phase == 'train':
                        # 训练阶段更新权重
                        loss.backward()
                        optimizer.step()

                # 计算损失, running_loss: 一次 Batch 的总计损失之和
                running_loss += loss.item() * inputs.size(0)
                running_corrects += torch.sum(preds == labels.data)

            # 结果的计算与输出
            dataset_len = len(data_loaders[phase].dataset)
            epoch_loss = running_loss / dataset_len
            epoch_acc = running_corrects.double() / dataset_len

            # 计算时间
            time_elapsed = mark() - since
            log.info('Time elapsed {:.0f}m {:.0f}s'.format(
                time_elapsed // 60, time_elapsed % 60))

            # 输出损失与通过率
            log.info('{} Loss: {:.4f} Acc: {:.4f}'.format(
                phase, epoch_loss, epoch_acc))

            # 得到最好那次测试的模型
            if phase == 'valid' and epoch_acc > best_acc:
                best_acc = epoch_acc
                best_model_wts = copy.deepcopy(model.state_dict())
                state = {
                  'state_dict': model.state_dict(),
                  'best_acc': best_acc,
                  'optimizer': optimizer.state_dict(),
                }
                # 存储模型
                log.info("模型存储中...")
                torch.save(state, filename)
                log.info("模型存储完成")
            # 记录每次循环的训练结果
            if phase == 'valid':
                val_acc_history.append(epoch_acc)
                valid_losses.append(epoch_loss)
                scheduler.step()  # epoch_loss
            if phase == 'train':
                train_acc_history.append(epoch_acc)
                train_losses.append(epoch_loss)

        # 输出学习率
        LRs.append(optimizer.param_groups[0]['lr'])
        log.debug('Optimizer learning rate : {:.7f}'.format(LRs[-1]))
        log.debug()

    # 输出总消耗时间和最高通过率
    time_elapsed = mark() - since
    log.info('Training complete in {:.0f}m {:.0f}s'.format(
        time_elapsed // 60, time_elapsed % 60))
    log.info('Best val Acc: {:4f}'.format(best_acc))

    # 训练完后用最好的一次当做模型最终的结果
    model.load_state_dict(best_model_wts)
    return model, LRs,\
        val_acc_history, train_acc_history,\
        valid_losses, train_losses




if __name__ == '__main__':
    classify_image()

```





-   对自己写的CNN的尝试, 未完成

```python
class CnnPlusModule(nn.Module):

    def __init__(self, optimizer: callable, criterion: callable,
                 input_dim, output_dim, *, neurals: list, learning_rate=0.01):
        super(CnnPlusModule, self).__init__()

        cnn_depth = len(neurals[0])

        cnn_neurals = [input_dim] + neurals[0]

        # 卷积神经网络部分
        out_width = input_dim
        kernel_size = 5
        stride = 1
        pading = (kernel_size - 1) // 2  # 2
        pool_width = 2
        out_channels = cnn_neurals[-1]
        self.__conv = []
        for i in range(cnn_depth):
            in_channel = cnn_neurals[i]
            out_channels = cnn_neurals[i + 1]
            # print(out_width)
            out_width = ((out_width - kernel_size + 2 * pading)
                         // stride + 1) // pool_width

            self.__conv.append(
                nn.Sequential(
                    nn.Conv2d(in_channel,
                              out_channels,
                              kernel_size,
                              stride,
                              pading),
                    nn.ReLU(),
                    nn.MaxPool2d(kernel_size=pool_width),
                ))

        # 全连接层 FC 部分
        fc_in_feature = out_width ** 2 * out_channels
        fc_depth = len(neurals[1])
        fc_neurals = [fc_in_feature] + neurals[1]
        self.__linears = []
        for i in range(fc_depth):
            self.__linears.append(
                nn.Linear(fc_neurals[i], fc_neurals[i + 1])
            )

        self.__out = nn.Linear(fc_neurals[fc_depth], output_dim)
        self.__optimizer = optimizer(self.parameters(), lr=learning_rate)
        self.__criterion = criterion()

    def forward(self, x):
        for i in range(len(self.__conv)):
            x = self.__conv[i](x)

        # flatten操作
        x = x.view(x.size(0), -1)

        for i in range(len(self.__linears)):
            print(x.size())
            x = self.__linears[i](x)
            x = functional.relu(x)

        output = self.__out(x)

        return output

    @staticmethod
    def __accuracy(predictions, labels):
        pred = torch.max(predictions.data, 1)[1]
        rights = pred.eq(labels.data.view_as(pred)).sum()
        return rights, len(labels)

    def train_data(self, data, target):
        self.train()
        output = self(data)
        loss = self.__criterion(output, target)
        self.__optimizer.zero_grad()
        loss.backward()
        self.__optimizer.step()

        right = CnnPlusModule.__accuracy(output, target)

        return loss, right

    def test_data(self, test_loader):
        self.eval()
        val_rights = []
        for (data, target) in test_loader:
            output = self(data)
            right = CnnPlusModule.__accuracy(output, target)
            val_rights.append(right)

        # 准确率计算
        return val_rights

    device = torch.device(
        "cuda:0" if torch.cuda.is_available() else "cpu"
    )

    def __str__(self):
        print(type(self), end='\n\n')
        print("device: ", CnnPlusModule.device, end="\n\n")
        for i in range(len(self.__conv)):
            print(self.__conv[i])

        print()
        # flatten操作
        print("flatten操作\n")

        for i in range(len(self.__linears)):
            print("FC: ", self.__linears[i])
            print("RELU()")
        print("\n")
        print("out: ", self.__out)


@log_advice("test_cnn_plus")
def test_cnn_plus():

    # class_name_ids: 花名ID, 将花名数字化, 便于训练
    data_loaders, dataset_sizes, class_name_ids = open_file()
    flower_id2name = get_id2name()  # ID和种类名的对应关系
    print("dataset_sizes: ", dataset_sizes)    # {'train': 6552, 'valid': 818}
    train_loader = data_loaders['train']
    valid_loader = data_loaders['valid']
    input_dim = image_size[1]
    output_dim = len(class_name_ids)
    net = CnnPlusModule(optim.Adam, nn.CrossEntropyLoss,
                        input_dim, output_dim,
                        neurals=[[16, 32, 64, 128, 256], [8, 16, 8]],
                        learning_rate=0.01)
    # print(CnnPlusModule.device)
    net = net.to(CnnPlusModule.device)
    # net.__str__()
    show_image(class_name_ids, flower_id2name, train_loader)
    show_image(class_name_ids, flower_id2name,  valid_loader)
    for batch_idx, (data, target) in enumerate(train_loader):
        data = data.to(CnnPlusModule.device)
        target = target.to(CnnPlusModule.device)
        # print(batch_idx, data[batch_idx].size(), target[batch_idx])
        break

```

