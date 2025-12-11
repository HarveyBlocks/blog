# 分类算法

## KNN算法

-   两点之间的距离
-   已知图上所有点的类, 新来了一个点, 这个点的类可以看作离他最近的一个点所在的类
-   但是, 一个点可能是异常点, 所以可以用**K个最近(Nearest)点(neigbro)**, 其中大部分属于什么类, 新点就是什么类
-   **k点太多**, 如果样本不均衡, 可能导致取得非一类的点(尽管距离很远, 但要求太低了, 也被包含了进去, 同时离得近的很少, 离得远的恰好的很多) 

忽略由版本和sklearn内部使用Scipy不当引起的警告

```python
def ignore_worning(ignore_all = False):
    import warnings
    if ignore_all:
        warnings.filterwarnings("ignore")
    else:
        # 忽略特定类型的警告
        warnings.filterwarnings("ignore", category=FutureWarning)
```



```python
import numpy as np
from numpy import ndarray
from sklearn.neighbors import KNeighborsClassifier

def knn_train(data_train:ndarray,target_train:ndarray)->KNeighborsClassifier:
    """
    将数据用k近邻算法分类

    Parameters
    ----------
    data_train : ndarray
        经过标准化的待分类的数据特征值.
    target_train : ndarray
        待分类的数据目标值.

    Returns
    -------
    ndarray
        分类后的数据.

    """
    from sklearn.neighbors import KNeighborsClassifier
    
    classifier = KNeighborsClassifier(
        n_neighbors = 5 ,# 默认5
        algorithm = 'auto' # auto就会根据特征值和目标值选择最好的算法
    )
    return classifier.fit(data_train,target_train)

def standard_scaler(data:ndarray,show:bool=False)->ndarray:
    from sklearn.preprocessing import StandardScaler
    scaler = StandardScaler()
    scaler.fit(data)
    result = scaler.transform(data)
    if show:
        ...
    return result

def get_iris(show:bool=False):
    from sklearn.datasets import load_iris

    iris = load_iris()
    if show: 
        ...
    return iris

from sklearn.utils import Bunch 
def split_dataset(iris:Bunch,show:bool=False):
    from sklearn.model_selection import train_test_split
    data_train,data_test,target_train, target_test = \
        train_test_split(
            iris.data,iris.target,
            test_size = 0.2 # 可选, 默认0.25
        )
    if show:
        ...
    return data_train,data_test,target_train, target_test


if __name__ == '__main__':
    ignore_worning()
    iris = get_iris()
    max:float = 0
    min:float = 1
    for i in range(10000):
        data_train,data_test,target_train, target_test = split_dataset(iris)
        standarded_data_train = standard_scaler(data_train)
        standarded_data_test = standard_scaler(data_test)
        classifier = knn_train(standarded_data_train,target_train)
        # 直接比对真实值和预测值
        y_predict = classifier.predict(standarded_data_train)
        # print(y_predict)
        # 计算准确率
        score = classifier.score(standarded_data_test,target_test ) 
        
        # print(score) # 每次分数还不一样? 我猜它训练集和测试集在分的时候是随机的
        if(score<min):
            min = score
        if(score>max):
            max = score
    print("-----------------------score--------------------------")
    print("min score:",min) # 0.43333333333333335
    print("max score:",max) # 1.0
    print("-----------------------score--------------------------")

```

缺点: 

-   必须选择合适的k值
-   懒惰算法, 不管三七二十一 ,就是算, 内存开销哒, 时间开销大
-   适合小规模数据

### 模型选择与调优

#### 交叉验证

1.  将**训练数据**再分成多组, 选用其中一组为验证集, 其他训练集训练后用验证集验证
2.  每次跟换验证集, 直到所有训练集都做过验证集
3.  全部轮完后的准确率取平均值

![image-20240307100716924](../assets/Day05-%E5%88%86%E7%B1%BB%E7%AE%97%E6%B3%95/image-20240307100716924.png)

-   四份称为四则交叉验证

每组训练集和验证集

#### 超参数-网格搜索

>   Grid Search

选择合适的k取值

-   遍历k的值, 找到最合适的

#### 调优API

>   sklearn.model_selection.GrodSearchCV(estimator,param_grid=None,cv=None)

```python
ignore_worning()
iris = get_iris()
data_train,data_test,target_train, target_test = split_dataset(iris)
standarded_data_train = standard_scaler(data_train)
standarded_data_test = standard_scaler(data_test)
# classifier = knn_train(standarded_data_train,target_train)
# score = knn_test(classifier,standarded_data_train,target_test)

from sklearn.model_selection import GridSearchCV
from sklearn.neighbors import KNeighborsClassifier

classifier = GridSearchCV(
    KNeighborsClassifier(), # 需要被优化的预估器
    param_grid={"n_neighbors":[i for i in range(10)]}, 
    # 以字典或列表的形式传入Classifiy的参数
    cv=4 # 指定几折交叉验证, 默认十折
)
knn_test(classifier.fit(standarded_data_train,target_train),
         standarded_data_test,target_test,show=True)
# score = 0.9666666666666667
print("-------------------------------------------")
print("最佳参数: ",classifier.best_params_)
print("最佳准确率: ",classifier.best_score_)
print("最佳估计器: ",classifier.best_estimator_)
print("交叉验证结果: ",classifier.cv_results_)
print("-------------------------------------------")
```
测试结果

```text
-------------------------------------------
最佳参数:  {'n_neighbors': 9}
最佳准确率:  0.95
最佳估计器:  KNeighborsClassifier(n_neighbors=9)
交叉验证结果: 
-------------------------------------------
```





## 朴素贝叶斯算法

一个东西, 它的成分哪个最高, 就认为它属于什么类

朴素在哪里呢? 假设每个特征值之间是互相独立的

如果样本数量太少, 就可能出现概率为0 ,就不好

为此引入拉普拉斯平滑系数, 分子分母都加上拉普拉斯平滑系数

$$
P(F_1|C) = \frac{Ni+\alpha}{N+\alpha m}
$$



-   `alpha`为指定的系数, 一般为1
-   m为训练文档中统计处的特征词个数





### 优缺点

-   缺点
    -   不一定独立
    -   要求数据量足够大



### 应用场景

文本分类, 关键词作为特诊, 假设词与词之间相互独立



### 在文本分类的应用



```python
from sklearn.naive_bayes import MultinomialNB
classifier = MultinomialNB(alpha=1.0)
```


测试`featch_dataset`都失败了, 403:forbidden ,使用VPN解决

```python
from sklearn.utils import Bunch 
def get_news_groups(show:bool = False)->Bunch:
    from sklearn.datasets import fetch_20newsgroups
    news_groups =fetch_20newsgroups(
        data_home="C:\\Users\\27970\\Desktop\\it\\py\\data_analysis" 
        # 文件下载的路径
        , subset = "all"
    )
    return news_groups
    
def split_dataset(dataset:Bunch):
    from sklearn.model_selection import train_test_split
    data_train,data_test,target_train, target_test = \
        train_test_split(
            dataset.data,dataset.target,
            test_size = 0.2 # 可选, 默认0.25
        )
    return data_train,data_test,target_train, target_test



def text_vectorizer(data):
    """
    文本特征抽取
    """
    from sklearn.feature_extraction.text import TfidfVectorizer
    transfer = TfidfVectorizer()
    fit_data = transfer.fit_transform(data)
    return fit_data

def test_bayes(data,target):
    from sklearn.naive_bayes import MultinomialNB
    classifier = MultinomialNB(alpha=1.0)
    classifier.fit(data, target)
    return classifier
    
def predict_test(classifier,data,target):
    # 计算准确率
    predict = classifier.predict(data)
    print(type(predict))
    print("测试数据预测值:",predict.shape[0])
    for i in range(predict.shape[0]//20):
        print(predict[i*20:(i+1)*20])
    score = classifier.score(data,target) 
    print("测试数据准确率:",score) # 0.85上下
    return score
    
    
if __name__ == "__main__" :
    news_groups = get_news_groups()
    data_train,data_test,target_train, target_test = split_dataset(news_groups)
    from sklearn.feature_extraction.text import TfidfVectorizer
    transfer = TfidfVectorizer()
    vectored_data_train = transfer.fit_transform(data_train)
    vectored_data_test = transfer.transform(data_test)
    classifier = test_bayes(vectored_data_train,target_train)
    predict_test(classifier,vectored_data_test,target_test)
```











## 决策树

if-else结构

怎么尽可能做少的选择, 就可以锁定目标?

-   把能排除更多的选项放在尽可能前面

-   信息熵(单位Bit)

    $$
    \\
    H(X) = -\sum_{i=1}^{n}{[P(x_i)\times{log_b{P(x_i)]}}}\\
    $$

    -   x~i~  i 事件
    -   P(X) 事件概率
    -   b 基数(二进制就是2)

-   条件熵

    $$
    \begin{aligned}
    H(Y|X) &= -\sum_{x\in X}{[ \space p(x)\times{H(Y|X=x)} \space ]}\\
    	   &= -\sum_{x\in X}{[ \space p(x)\times 
    		\sum_{y\in Y}{[ \space p(y|x)\times{\log_{b}{p(y|x)}}\space ]}
    		\space ]}\\ 
    		&= -\sum_{x\in X}{
    		\sum_{y\in Y}{[ \space p(x,y)\times{\log_{b}{p(y|x)}}\space ]}
    		}\\ 
    \end{aligned}
    $$

-   信息增益
    $$
    \\
    g(D,A) = H(D) - H(D|A) \\ 
    \\
    $$





### API

```python
from sklearn.tree import DecisionTreeClassifier
classifier = DecisionTreeClassifier()
```
-   `criterion` 默认是`gini`, 信息熵可用`entropy`
-   `max_depth`, 默认会尽可能地拟合一切训练数据, 但太过精细泛化能力较差, 可能导致对测试数据的预估变差
-   `random_state` 种子

```python
from sklearn.utils import Bunch 
from numpy import ndarray


def ignore_worning(*ignore_warnings, ignore_all = False):
    import warnings
    if ignore_all:
        warnings.filterwarnings("ignore")
    else:
        # 忽略特定类型的警告
        for warn in ignore_warnings:
            warnings.filterwarnings("ignore", category=warn)


from sklearn.utils import Bunch
def get_iris(show:bool=False) -> Bunch:
    from sklearn.datasets import load_iris

    iris = load_iris()
    if show: 
        for string in str(iris.get("DESCR")).split("\n"):
            print(string) # 数据输出有点问题
        data = iris.get("data")
        print("data.shape:",data.shape) # (150, 4)
        target_name = iris.get("target_names")
        print("target_name",target_name)  
        target = iris.get("target")
        print("target.shape",target.shape) # (150,)
    
    return iris


def split_dataset(iris:Bunch,show:bool=False):
    from sklearn.model_selection import train_test_split
    data_train,data_test,target_train, target_test = \
        train_test_split(
            iris.data,iris.target,
            test_size = 0.2 # 可选, 默认0.25
        )
    if show:
        print("data_train.shape:",data_train.shape) # (120, 4)
        print("target_train.shape:",target_train.shape) # (120,)
        print("data_test.shape:",data_test.shape) # (30, 4)
        print("target_test.shape:",target_test.shape) # (30,)
    return data_train,data_test,target_train, target_test

def standard_scaler(data:ndarray,show:bool=False)->ndarray:
    """
    标准化无量纲化

    Parameters
    ----------
    data : ndarray
        未无量纲化的数据.

    Returns
    -------
    ndarray
        无量纲化后的数据.

    """
    from sklearn.preprocessing import StandardScaler
    scaler = StandardScaler()
    scaler.fit(data)
    result = scaler.transform(data)
    if show:
        print("--------------------标准化已完成----------------------")
        for i in result:
            for j in i:
                print("%7.3f"%j, end = ",")
            print()
    return result




def decision_tree_train(data_train:ndarray,target_train:ndarray):
    """
    将数据用决策树算法分类

    Parameters
    ----------
    data_train : ndarray
        经过标准化的待分类的数据特征值.
    target_train : ndarray
        待分类的数据目标值.

    Returns
    -------
    ndarray
        分类后的数据.

    """
    from sklearn.tree import DecisionTreeClassifier
    classifier = DecisionTreeClassifier(max_depth = 2)
    return classifier.fit(data_train,target_train)


def decision_tree_test(classifier,data_test:ndarray,target_test:ndarray,show:bool=False):
    # 直接比对真实值和预测值
    y_predict = classifier.predict(data_test)
    # 计算准确率
    score = classifier.score(data_test,target_test) 
    if show:
        print("predict:",y_predict)
        print("测试数据准确率:",score) # 每次分数还不一样? 我猜它训练集和测试集在分的时候是随机的
    return score


def decision_tree_grip_search(data_train,data_test,target_train, target_test,show=False):
    from sklearn.model_selection import GridSearchCV
    from sklearn.tree import DecisionTreeClassifier
    
    classifier = GridSearchCV(
        DecisionTreeClassifier(), 
        param_grid={"max_depth":[i for i in range(20)]},
        cv=4 # 指定几折交叉验证, 默认十折
    )
    classifier.fit(data_train,target_train)
    if show:
        decision_tree_test(classifier,
                 data_test,target_test,show=show)
        print("-------------------------------------------")
        print("最佳参数: ",classifier.best_params_)
        print("最佳准确率: ",classifier.best_score_)
        print("最佳估计器: ",classifier.best_estimator_)
        # print("交叉验证结果: ",classifier.cv_results_)
        print("-------------------------------------------")
   



if __name__ == '__main__':
    ignore_worning(FutureWarning)
    iris = get_iris()
    data_train,data_test,target_train, target_test = split_dataset(iris)
    # 决策树不需要标准化, 因为决策树的特征之间不会相互影响, 特征只和自身比较
    # standarded_data_train = standard_scaler(data_train)
    # standarded_data_test = standard_scaler(data_test)
    # classifier = decision_tree_train(standarded_data_train,target_train)
    # score = decision_tree_test(classifier,standarded_data_test,target_test,True) 
    # max_depth = 2 时, score = 1.0 
    decision_tree_grip_search(standarded_data_train,standarded_data_test,
                              target_train, target_test,True)
    
"""
最佳参数:  {'max_depth': 4}
最佳准确率:  0.9666666666666667
最佳估计器:  DecisionTreeClassifier(max_depth=4)
交叉验证结果: 
One or more of the test scores are non-finite: [     
 nan 0.68333333 0.93333333 0.95833333 0.96666667 0.96666667
 0.95833333 0.95833333 0.96666667 0.96666667 0.95833333 0.95833333
 0.96666667 0.95833333 0.95833333 0.95833333 0.95833333 0.96666667
 0.96666667 0.95833333]

"""
    
```



决策树的可视化

```python
classifier = decision_tree_train(standarded_data_train,target_train)
from sklearn.tree import export_graphviz
export_graphviz(
    classifier,out_file="C:\\Users\\27970\\Desktop\\IT\py\\data_analysis\\tree.dot"
    , feature_names = iris.feature_names
)
```

[dot文件转树](http://webgraphviz.com/)

![image-20240307163131403](../assets/Day05-%E5%88%86%E7%B1%BB%E7%AE%97%E6%B3%95/image-20240307163131403.png)



### 优缺点

可视化, 可解释能力强

容易产生过拟合(如果不设置max_depth)

-   采用cart剪枝算法(决策树API中已实现)
-   随机森林







### 随机森林



#### 集成学习方法

通过建立几个模型组合的来解决单一预测问题

1.  生成多个分类器/模型
2.  各自独立地学习和做出预测
3.  这些预测最后结合成组合预测



#### 随机森林的原理

-   多个**决策树**构成, 采用少数服从多数
-   **特征值随机(选取部分特征学习)** + **训练集随机**  让每棵树生成得都不一样
    -   `bootstrap` 随机有放回抽样
    -   取得的特征数都**远小于**总特征集
-   为什么结果的正确率能提高? 
    -   因为聪明是千篇一律的
    -   蠢是千姿百态的
    -   蠢蠢的树在决策时犯蠢了, 各自的犯蠢会抵消
    -   最终只有聪明的树的决策会被保留

#### API



```python
sklearn.ensemble.RandomForestClassifier(n_estimators=10,criterion='gini',max_depth=None,bootstrap=True,random_state=None,min_samples_split=2)
```



-   随机森林分类器
-   n＿estimators： integer，optional（default＝10）森林里的树木数量 
-   criteria：string，可选（default＝"gini"）分割特征的测量方法
-   `max＿depth`：integer或None，可选（默认＝None）树的最大深度
-   `max＿features`＝"auto"，每个决策树的最大特征数量
    -   If "auto",then **max_features=sqrt(n_features)**
    -   If "sqrt", then **max_features=sqrt(n_features)** (*same as "auto"*). 
    -   If "log2",then **max_features=log2(n_features).**
    -   If None,then **max_features=n_features**.
-   `bootstrap`：boolean， optional （default＝True）是否在构建树时**使用放回抽样** 
-   `min_samples_split`：节点划分最少样本数
-   `min_samples_leaf`：叶子节点的最小样本数
-   超参数：`n_estimator`，`max_depth`，`min_samples_split`，`min_samples_leaf` 

测试结果

```
predict: [1 1 1 2 2 2 2 1 2 1 1 0 0 2 0 2 2 2 1 1 1 2 1 1 0 0 0 0 2 0]
测试数据准确率: 0.9
-------------------------------------------
最佳参数:  {'max_depth': 2, 'n_estimators': 10}
最佳准确率:  0.975
最佳估计器:  RandomForestClassifier(max_depth=2, n_estimators=10)
-------------------------------------------
```

#### 优缺点

-   极好的准确率
-   能有效运行在大数据集上, 处理具有高维特征的输入样本, 而不需要降维?????????????????????????????
-   能够评估各个特征在分类问题上的重要性

