# 欠拟合和过拟合

## 出现原因

-   过拟合现象
    -   学到了过多的特征(嘈杂的特征)
    -   例如: 天鹅一定是白色的(黑天鹅)
    -   在训练集上表现得很好, 在测试集上表现差(模型过于复杂)
-   欠拟合现象
    -   学到的特征不够
    -   例如: 会飞的就是天鹅
    -   在训练集上表现得不好, 在测试集上表现差(模型过于简单)

![image-20240307205603212](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Python/机器学习/Day06-欠拟合和过拟合/image-20240307205603212.png)

## 解决

-   欠拟合
    -   增加数据特征
-   过拟合
    -   正则化(使高次项的参数减小, 使模型偏向简单,减小嘈杂特征的影响)

## 正则化

### L1正则化

>LASSO回归

损失函数+惩罚系数(`\lambda`)\*惩罚项
$$
\eta (w) = \frac{1}{2m}\sum_{i=1}^m(\space 

h_w(x_i)-y_i

\space)^2 

+\lambda \sum_{j=1}^n{|w_j|} \\
$$
会导致某一特征的权重系数直接为零(在接近0时依旧是线性, 会降得很快)

删除了这个特征的影响

### L2正则化

>   Rifdge 岭回归

更常用

损失函数 + 惩罚系数(`\lambda`)\*惩罚项

$$
\eta (w) = \frac{1}{2m}\sum_{i=1}^m(\space 

h_w(x_i)-y_i

\space)^2 

+\lambda \sum_{j=1}^n{w^2_j} \\
$$

-   `m`样本数
-   `n` 特征数
-   `w`权重值
-   对权重的削弱更平滑, 更柔和(平方在接近0时更平缓)

#### 正则化力度对最终结果的影响

![image-20240307220106607](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Python/机器学习/Day06-欠拟合和过拟合/image-20240307220106607.png)

## 岭回归

>   带L2正则化的线性回归

#### API

```python
print("---------------------------岭回归---------------------------")
from sklearn.linear_model import Ridge
regresssor = Ridge(alpha=0.5)
regresssor.fit(data_train,target_train)
print(regresssor.coef_)
print(regresssor.intercept_)
# 结果比正规方程调优还要好...牛逼啊...看来方程确实是过拟合了
```

-   `alpha` 正则化粒度 0~1 1~10

-   `fit_intercept:bool`, default=True 是否计算偏置

-   `solver` 会根据数据自动选择优化方法

    -   {'auto', 'svd', 'cholesky', 

        'lsqr', 'sparse_cg', 'sag', 

        'saga', 'lbfgs'}

    -   default='auto'

    -   'sag' 如果数据集和特征值都比较大, 选择该随机梯度下降优化

-   `normalize: bool, default=False` 数据时候进行标准化

    -   在`fit`之前调用`StandardScaler`标准化数据

`SGDRegressor`也由参数`penalty='l2'`实现l2正则化,**但是Redge实现了SAG**, Bigger, Better,Stronger

