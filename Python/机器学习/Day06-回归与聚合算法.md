# 回归和聚类

## 线性回归

-   欠拟合
-   过拟合

改进=>岭回归

逻辑回归(一种分类算法)

模型保存与加载

无监督学习 K-means

### 原理

回归问题, 目标值是连续性数值的问题

### 应用场景

>   目标值是连续性数值的问题

房价预测

金融

### 定义

多个自变量(特征值) 和因变量(目标值)之间的关系进行建模的一种分析算法

-   只有一个自变量的情况称为单变量回归, 多用于一个自变量情况的回归
-   目的是寻找一种函数关系(线性模型)

### 目标

获取回归模型, 实现对目标值的预测

看作获取对参数的获取

通过迭代更新, 让特征不断接近目标值, 以此获取参数的值

### 损失函数

(预测值-真实值)的平方累和

$$
\eta (\theta) = \sum_{i=1}^m(\space 

h_w(x_i)-y_i

\space)^2
$$

#### 优化

最小二乘法

如何求出模型参数的最小值

-   正规方程

    $$
    w = (X^{T}X)^{-1}X^{T}y
    $$

    -   X为特征值矩阵, y为目标值矩阵, 直接求得最佳值
    -   求逆的时间复杂度是O(n^3^), 耗时长
    -   

-   梯度下降

    $$
    w'_1:= w_1 - \alpha\frac{\partial cost(w_0+w_1x_1)}{\partial w_1}\\
    w'_0:= w_0 - \alpha\frac{\partial cost(w_0+w_1x_1)}{\partial w_1}\\
    $$

    `\partial`是求偏导

    `\alpha`是学习率, 沿着坡度最陡方向下降的步长

    `\frac`所指向的偏导分式, 代表坡度方向

    -   试错
-   改进

    -   几何意义是沿着切线的方向向下降
        -   当然有可能陷入局部最小点, 而不是全局最小点
    -   数据量很大的话有优势

-   模型评估: 均方误差

    $$
    MSE = \frac{1}{m}\sum_{i=1}^m(y_i-\overline{y})2
    $$

    -	预测值 - 真实值	 

### API

导入

```python
from sklearn.linear_model import LinearRegression,SGDRegressor
```

-   `LinearRegression()`

    -   参数:

        `fit_intercept:bool, default=True`是否计算偏置 , 不加就会变成只能过原点的了

-   `SGDRegressor()`

    -   参数:

        `loss:str default='squared_error'` 损失类型, 普通最小二乘法

        `fit_intercept:bool, default=True` 是否计算偏置

        `learning_rate: str, default='invscaling' `学习率填充

        -   `'constant': eta = eta0`
        -   `'optimal': eta = 1.0 / (alpha * (t + t0))`
        -   `'invscaling': eta = eta0 / pow(t, power_t) power_t = 0.25`越接近, 步长越小
        -   对于一个常数值的学习率来说, 可以使用**`'constant'`**

        `penalty{'l2', 'l1', 'elasticnet'}, default='l2'` 'l2'是一种应对过拟合的方式, 相当于岭回归

-   通用属性

    -   `coef_` 回归系数
    -   `intercept_`偏置

-   模型评估

    均方误差`from sklearn.metrics import mean_squared_error`

    -   参数

        `y_true`真实值

        `y_ored`预测值

        返回浮点数结果, 越小越好

#### 使用

数据导入, 划分 越标准化

```python
def pre():
    # 导入
    from sklearn.datasets import load_boston
    boston = load_boston()

    # 划分
    from sklearn.model_selection import train_test_split
    data_train,data_test,target_train, target_test = \
        train_test_split(
            boston.data,boston.target,
            test_size = 0.2 # 可选, 默认0.25
        )

    # 标准化
    from sklearn.preprocessing import StandardScaler
    scaler = StandardScaler()
    scaler.fit(data_train)
    data_train = scaler.transform(data_train)
    data_test = scaler.transform(data_test)

    # 预估器
    return data_train,data_test,target_train, target_test

```

预估器的使用

```python
data_train,data_test,target_train, target_test = pre()

from sklearn.linear_model import LinearRegression,SGDRegressor

# 正规方程调优
regresssor = LinearRegression(fit_intercept = True)
regresssor.fit(data_train,target_train)
print(regresssor.coef_)
print(regresssor.intercept_)

# 模型评估
post(regresssor,data_test,target_test)

# 梯度下降调优
regresssor = SGDRegressor()
regresssor.fit(data_train,target_train)
print(regresssor.coef_)
print(regresssor.intercept_)

# 模型评估
post(regresssor,data_test,target_test)
```

模型评估

```python
def post(regresssor,data_test,target_test):
    from sklearn.metrics import mean_squared_error
    y_pred = regresssor.predict(data_test)
    mse = mean_squared_error(target_test,y_pred)
    print(mse)
```

测试结果

```
---------------------------正规方程调优---------------------------
[-1.1791657   1.02808499 -0.09391596  0.85710118 -1.62491562  2.83730232
 -0.22286357 -3.14737367  2.83916075 -1.91233441 -1.92721546  0.77364736
 -3.78034918]
22.83440594059409
23.106773767569152
---------------------------梯度下降调优---------------------------
[-1.09778643  0.81052533 -0.36109374  0.93337608 -1.3501466   3.02138602
 -0.27414724 -2.83226148  1.80561715 -0.92488151 -1.87785952  0.79071181
 -3.73245358]
[22.84935662]
23.35512013558203
```

#### 梯度下降优化器

-   GD
    -   原始的梯度(Gradient)下降(Descent)算法
    -   每次迭代都需要算一遍所有样本
-   SGD
    -   随机(Stochastic)梯度下降
    -   在一次迭代时, 只考虑一个训练样本
    -   高效, 容易实现
    -   需要许多超参数, 入正则项参数, 迭代参数
    -   对于特征标准化是敏感的
-   SAGD
    -   随机平均(Average)梯度法
    -   收敛速度块
    -   岭回归, 逻辑回归都会使用到SAG优化

