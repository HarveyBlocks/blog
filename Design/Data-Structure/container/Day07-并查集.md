# 并查集

>   Disjoint Set Union (DSU)

主要用来实现合并(merge)和查(find)的需求

需求: 依据节点获取它的root是啥, 这个root一般是整棵树的特征标识

节点有两个信息

1.  int value
2.  int root

实现: 以value作为数组的index, 如果该value在数组上对应的元素, 在逻辑上对应一棵树的root, 这个元素在数组中的值为index本身, 如果是非根的节点, 就让这个元素在数组中的值为其父节点所在的index

然而, 也可以用其他方法映射当前节点和其父亲节点, 而不是使用数组的索引-值的方式, 以实现非整形类型数据的存储



## 查找

```cpp
int find(int x) {
    return dsuArr[x] == x ? x : find(dsuArr[x]); 
}
```

### 压缩路径

考虑到每次查询时都要经过一条从叶子到根的路径, 其最终目的都是能够直接查找到其Root所在节点

那么每次查找后, 没有命中其根节点, 就优化节点的指向, 使其能直接指向根节点

```cpp
int find(int x) {
    return dsuArr[x] == x ? x :(dsuArr[x]=find(dsuArr[x])); 
}
```



## 启发式合并

合并: 一棵树的根作为另一棵树的孩子

```cpp
dsuArr[find(x)] = find(y);  // 需要先给出所在树的节点
```

启发式合并: 让更矮的树左孩子

```cpp
x = find(x), y = find(y);
if (x == y) {
    return;
}
if (size[x] < size[y]) {
    swap(x, y); 
}
```
## 删除

将其父亲设为自己(在数组中将index所在的元素的值置为本身, 让被删除节点作为root

## 移动

节点X要抛弃自己的父亲, 认Y的父亲为父

