# 模型的保存和加载

-   其实是保存预估器
-   得出模型之后就可以保存模型

```python
import joblib
joblib.dump(
    classifier,  # any Python object
    "C:\\Users\\27970\\Desktop\\IT\\py\\data_analysis\\decision_tree_grip_search.pkl"
)
```

```python
import joblib
classifier = joblib.load(
    "C:\\Users\\27970\\Desktop\\IT\\py\\data_analysis\\decision_tree_grip_search.pkl")
```

测试结果:

```python
runcell(0, 'C:/Users/27970/Desktop/IT/py/data_analysis/decision_tree.py')
predict: [2 2 1 1 1 2 2 0 0 1 2 2 2 2 0 1 2 0 0 2 0 2 0 1 0 2 0 0 1 1]
测试数据准确率: 0.9333333333333333
-------------------------------------------
最佳参数:  {'max_depth': 10, 'n_estimators': 20}
最佳准确率:  0.95
最佳估计器:  RandomForestClassifier(max_depth=10, n_estimators=20)
-------------------------------------------

runcell(0, 'C:/Users/27970/Desktop/IT/py/data_analysis/decision_tree.py')
predict: [2 2 2 2 1 2 2 1 0 2 1 1 0 2 2 1 1 1 0 1 0 1 1 1 2 2 2 2 0 1]
测试数据准确率: 1.0
-------------------------------------------
最佳参数:  {'max_depth': 10, 'n_estimators': 20}
最佳准确率:  0.95
最佳估计器:  RandomForestClassifier(max_depth=10, n_estimators=20)
-------------------------------------------
```

两次测试结果一致

