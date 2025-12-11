# Treap

>   Tree + Heap

Treap（树堆）是一种 **弱平衡** 的 **二叉搜索树**

-   左子节点的值比父节点小
-   右子节点的值比父节点大(自定)
-   子节点值 *priority* 比父节点大或小（取决于是小根堆还是大根堆）

其中 *priority* 随机给出,用于维护堆的性质结构, 而节点值用于维护树的性质结构, 防止出现数据值有序增长的情况

<img src="../../assetss/Day07-Treap/1920px-Treap.svg.png" alt="img" style="zoom:30%;" />

-   纵轴是 *priority* 
-   横轴是 *value*
-   横轴的 *value* 优先度比纵轴的 *priority* 高

## 旋转

用旋转实现Treap优先级, 堆的性质的维护

### 增加

如果当前节点的优先度不符合Treap的结构, 通过旋转调整

```cpp
template<class T>
bool Treap<T>::treapInsertAdjustByRotation(Stack<BinaryTreeNode<T> *> &trace) {
    BinaryTreeNode<T> *targetNode = trace.pop();
    // 调整, 小根堆, 根的优先级Priority是最小的
    if (trace.empty()) {
        // 只有一个节点
        return false;
    }
    BinaryTreeNode<T> *parentNode = trace.pop();
    bool leftChild = this->BinaryTree<T>::isLeftChild(targetNode, parentNode);
    // 使用旋转调整树的结构, 旋转不会改变中序遍历时的有序性
    if (!leftChild && TreapNode<T>::comparePriority(parentNode, parentNode->getRight()) > 0) {
        // 右子树更小, 让右子树旋转上来, 使用左旋
        this->BinaryBalanceSearchTree<T>::leftRotate(parentNode, trace.empty() ? nullptr : trace.top());
        trace.push(targetNode);
        return true;
    }
    if (leftChild && TreapNode<T>::comparePriority(parentNode, parentNode->getLeft()) > 0) {
        // 左子树更小, 让左子树旋转上来, 使用右旋
        this->BinaryBalanceSearchTree<T>::rightRotate(parentNode, trace.empty() ? nullptr : trace.top());
        trace.push(targetNode);
        return true;
    }
    // 旋转之后往更上一级
    return false;
}
```

### 删除



删除用二叉搜索树本来的删除没有问题, 两个子树都存在的情况, 用选装将一个子树转上来, 反复直到需要被删除的节点降到了最低

## 排名-值

用排名(范围)查询值和用值(范围)查询排名

如果节点里存一个字段, 存储当前节点包含自己的所有子树个数, 这样就好实现了

因为可以根据左右子树的个数来判断自己处于哪个位置

## 无旋 treap

通过分裂(splite)和合并(merge)实现对Treap性质的维护

将查找这一步也融合入splite和merge中了

### splite

```cpp
void split(BinaryTreeNode<T> *root, BinaryTreeNode<T> *targetNode) {
    if (targetNode == nullptr) {
        throw IllegalArgumentException();
    }
    if (root == nullptr) {
        return;
    }
    if (root->compareTo(targetNode) > 0) {
        // root大, 向右走
        split(root->getRight(), targetNode);
    } else {
        // root小, 向左走
        split(root->getLeft(), targetNode);
    }
}
```

### merge

```cpp
/**
 * @param smallerTreeRoot Value更小的树, 其最大value小于biggerTree的最小value
 * @param biggerTreeRoot Value更小的树, 其最大value小于biggerTree的最小value
 * @return 不是nullptr的那个节点, 如果都是nullptr了, 就返回nullptr;
 */
BinaryTreeNode<T> *merge(BinaryTreeNode<T> *smallerTreeRoot, BinaryTreeNode<T> *biggerTreeRoot) {
    if (biggerTreeRoot == nullptr && smallerTreeRoot == nullptr) {
        return nullptr;
    }
    if (biggerTreeRoot == nullptr) {
        return smallerTreeRoot;
    }
    if (smallerTreeRoot == nullptr) {
        return biggerTreeRoot;
    }
    // 小根树
    if (TreapNode<T>::getPriority(smallerTreeRoot) < TreapNode<T>::getPriority(biggerTreeRoot)) {
        // biggerTreeRoot在smallerTreeRoot的上方
        biggerTreeRoot->setLeft(merge(smallerTreeRoot, biggerTreeRoot->getLeft()));
        return biggerTreeRoot;
    } else {
        smallerTreeRoot->setRight(merge(smallerTreeRoot->getRight(), biggerTreeRoot));
        return smallerTreeRoot;
    }
}
```

