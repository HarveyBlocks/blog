# AVL-Tree

是一棵二叉搜索树

是平衡二叉搜索树

## 平衡和平衡因子

>   Balance Factor

一颗树的任意节点的平衡因子的绝对值小于等于一, 它就是平衡树

平衡因子=节点左子树的深度(最大深度)减右子树的最大深度

平衡因子大于0, 说明左子树比右子树要有更大深度的节点, 重心在左

```cpp
static int balanceFactor(BinaryTreeNode<T> *node) {
    if (node == nullptr) {
        return 0;
    }
    return BinaryTree<T>::maxDepth(node->getLeft()) - BinaryTree<T>::maxDepth(node->getRight());
}
```



## 旋转

平衡因子大于1时, 通过旋转树的节点, 使树恢复平衡

注意, 旋转是特指对某一节点的操作, 如果直到一个节点含有父节点的话, 一个节点完全能完成该操作

### 左旋

当右子树比较重, 使用左旋

1.  节点X的右子树R从节点上脱落
2.  如果右子树R有左子树, 左子树成为节点X的右子树
3.  节点X成为R的左子树, 节点X的父节点指向R(R替代节点X的位置)
4.  此时树的高度下降1

```cpp
template<class T>
void AvlTree<T>::leftRotate(BinaryTreeNode<T> *node, BinaryTreeNode<T> *parentNode) {
    if (node == nullptr) {
        throw NullPointException();
    }
    BinaryTreeNode<T> *right = node->getRight();
    if (right == nullptr) {
        throw NullPointException();
    }
    node->setRight(right->getLeft());
    // 调整parent, 将right替换node的位置
    if (parentNode == nullptr) {
        if (node != this->root) {
            throw IllegalArgumentException();
        }
        // node = root;
        this->root = right;
    } else if (parentNode->getRight() == node && parentNode->getLeft() != node) {
        // node = left
        parentNode->setRight(right);
    } else if (parentNode->getLeft() == node && parentNode->getRight() != node) {
        // node = right
        parentNode->setLeft(right);
    } else {
        throw IllegalArgumentException();
    }
    right->setLeft(node);
}
```



### 右旋

当左子树比较重, 使用右旋

1.  节点X的左子树L从节点上脱落
2.  如果左子树L有右子树, 右子树成为节点X的左子树
3.  节点X成为L的右子树, 节点X的父节点指向L(L替代节点X的位置)
4.  此时树的高度下降1

```cpp
template<class T>
void AvlTree<T>::rightRotate(BinaryTreeNode<T> *node, BinaryTreeNode<T> *parentNode) {
    if (node == nullptr) {
        throw NullPointException();
    }
    BinaryTreeNode<T> *left = node->getLeft();
    if (left == nullptr) {
        throw NullPointException();
    }
    node->setLeft(left->getRight());
    // 调整parent, 将left替换node的位置
    if (parentNode == nullptr) {
        if (node != this->root) {
            throw IllegalArgumentException();
        }
        // node = root;
        this->root = left;
    } else if (parentNode->getRight() == node && parentNode->getLeft() != node) {
        // node = left
        parentNode->setRight(left);
    } else if (parentNode->getLeft() == node && parentNode->getRight() != node) {
        // node = right
        parentNode->setLeft(left);
    } else {
        throw IllegalArgumentException();
    }
    left->setRight(node);
}

```



### 旋转的使用

-   LL型
    -   节点的平衡因子大于等于2
    -   节点的左节点的平衡因子大于等于0
    -   对节点右旋, 实现平衡
-   LR型
    -   节点的平衡因子大于等于2
    -   节点的左节点的平衡因子小于0
    -   对节点左子树左旋, 再对节点右旋, 实现平衡
-   RL型
    -   节点的平衡因子小于等于-2
    -   节点的左节点的平衡因子大于0
    -   对节点右子树右旋, 再对节点左旋, 实现平衡
-   RR型
    -   节点的平衡因子小于等于-2
    -   节点的左节点的平衡因子小于等于0
    -   对节点左旋, 实现平衡

```cpp
if (node == nullptr) {
    return;
}
int factor = BinaryBalanceSearchTree<T>::balanceFactor(node);
if (factor >= 2) {
    factor = BinaryBalanceSearchTree<T>::balanceFactor(node->getLeft());
    if (factor >= 0) {
        // LL型
        this->rightRotate(node, nodeParent);
    } else {
        // LR型
        this->leftRotate(node->getLeft(), node);
        this->rightRotate(node, nodeParent);
    }
} else if (factor <= -2) {
    factor = BinaryBalanceSearchTree<T>::balanceFactor(node->getRight());
    if (factor <= 0) {
        // RR型
        this->leftRotate(node, nodeParent);
    } else {
        // RL型
        this->rightRotate(node->getRight(), node);
        this->leftRotate(node, nodeParent);
    }
}
return ;
```
## AVL树的增删

由于节点没有指向父节点的指针的字段, 于是修改二叉搜索树的增删代码, 使其能够有一个栈用于存储查找父亲的记录trace

```cpp
#define GO_LEFT_BY(cmp) ((cmp)>=0)

template<class T>
class BinarySearchTree : public BinaryTree<T> {
private:

    /**
     * @return 如果需要左右子树都存在, 需要递归/循环删除, 则返回true
     */
    bool removeNode0(Stack<BinaryTreeNode<T> *> &trace);

    /**
     * 使用循环删除都有左右子树的情况
     */
    void removeNodeByLoop(Stack<BinaryTreeNode<T> *> &trace);

    /**
     * 使用递归删除都有左右子树的情况
     */
    void removeNodeByRecursive(Stack<BinaryTreeNode<T> *> &trace);

public:
    BinarySearchTree() : BinaryTree<T>() {}

    BinarySearchTree(const BinarySearchTree<T> &tree);

    Stack<BinaryTreeNode<T> *> search(const T &value);

    void insert(const T &value);

    int remove(const T &value);

    /**
     * @param trace 该函数保证, 不会更改原trace, trace的栈顶是要新增加的节点
     * 次数trace.empty() 说明是根节点需要被删除
     */
    virtual void insertNode(Stack<BinaryTreeNode<T> *> &trace);

    /**
     * 不会被递归(其内部含递归)
     * @param trace 不会拷贝一份, 因为如果在两个叶子都存在的情况下的删除, 可能涉及删除节点的变化<p>
     *      本函数将会对trace进行进一步的加工<p>
     *      在如果在两个叶子都存在的情况下, 替换的节点在值被替换后不被释放内存, 依然出现在栈中<p>
     *      trace的栈顶不包含已经删除的节点(指已经被真正释放内存的节点), 栈顶是被删除节点的父节点<p>
     */
    virtual void removeNode(Stack<BinaryTreeNode<T> *> &trace);


    void resetRoot(BinaryTreeNode<T> *newRoot);

    static void traceFamily(Stack<BinaryTreeNode<T> *> trace, BinaryTreeNode<T> *root);
};
```



```cpp
template<class T>
int BinarySearchTree<T>::remove(const T &value) {
    if (this->root == nullptr && this->empty()) {
        return 0;
    } else if (this->root == nullptr || this->empty()) {
        throw IllegalStatementException();
    }

    int count = 0;
    Stack<BinaryTreeNode<T> *> trace;
    while (value == *(this->root->getValue())) {
        count++;
        trace.clear();
        if (this->size == 1) {
            resetRoot(nullptr);
            this->size--;
            return count;
        }
        trace.push(this->root);
        removeNode(trace);
    }
    while (true) {
        BinaryTreeNode<T> *node = this->root;
        trace.clear();
        while (node != nullptr) {
            trace.push(node);
            int cmp = node->compareTo(value);
            if (cmp > 0) {
                // root 大
                node = node->getLeft();
            } else if (cmp < 0) {
                node = node->getRight();
            } else {
                break;
            }
        }
        if (node == nullptr) {// 没找到
            break;
        }
        count++;
        removeNode(trace);
    }
    return count;
}

template<class T>
bool BinarySearchTree<T>::removeNode0(Stack<BinaryTreeNode<T> *> &trace) {
    BinaryTreeNode<T> *node = trace.pop();
    //  次数trace.empty() 说明是根节点
    BinaryTreeNode<T> *nodeParent = trace.empty() ? nullptr : trace.top();

    bool leftChild = (nodeParent == nullptr ? true : BinaryTree<T>::isLeftChild(node, nodeParent));

    BinaryTreeNode<T> *left = node->getLeft();
    BinaryTreeNode<T> *right = node->getRight();
    if (left == nullptr && right == nullptr) {
        // 1
        if (nodeParent == nullptr) {
            resetRoot(nullptr);
        } else {
            if (leftChild) {
                nodeParent->setLeftRemoveOld(nullptr);
            } else {
                nodeParent->setRightRemoveOld(nullptr);
            }
        }
    } else if (right == nullptr) {
        // 2
        if (nodeParent == nullptr) {
            resetRoot(left);
        } else {
            if (leftChild) {
                nodeParent->setLeftRemoveOld(left);
            } else {
                nodeParent->setRightRemoveOld(left);
            }
        }
    } else if (left == nullptr) {
        // 3
        if (nodeParent == nullptr) {
            resetRoot(right);
        } else {
            if (leftChild) {
                nodeParent->setLeftRemoveOld(right);
            } else {
                nodeParent->setRightRemoveOld(right);
            }
        }
    } else {
        // 只是修改, 返回true, 表示确实要等待删除
        trace.push(node);
        trace.push(node->getLeft());
        while (trace.top()->getRight() != nullptr) {
            trace.push(trace.top()->getRight());
        }
        node->setValue(trace.top()->getValue());
        return true;
    }
    this->size--;
    return false;
}


template<class T>
void BinarySearchTree<T>::resetRoot(BinaryTreeNode<T> *newRoot) {
    delete this->root;
    this->root = newRoot;
}


template<class T>
void BinarySearchTree<T>::removeNode(Stack<BinaryTreeNode<T> *> &trace) {
    removeNodeByLoop(trace);
}


template<class T>
void BinarySearchTree<T>::removeNodeByRecursive(Stack<BinaryTreeNode<T> *> &trace) {
    if (!removeNode0(trace)) {
        return;
    }
    removeNodeByRecursive(trace);
}

template<class T>
void BinarySearchTree<T>::removeNodeByLoop(Stack<BinaryTreeNode<T> *> &trace) {
    while (removeNode0(trace));
}


template<class T>
void BinarySearchTree<T>::insert(const T &value) {
    Stack<BinaryTreeNode<T> *> trace;
    BinaryTreeNode<T> *node = this->root;
    while (node != nullptr) {
        trace.push(node);
        node = GO_LEFT_BY(node->compareTo(value)) ? node->getLeft() : node->getRight();
    }
    BinaryTreeNode<T> *newNode = new BinaryTreeNode(value);
    trace.push(newNode);
    insertNode(trace);
}


template<class T>
void BinarySearchTree<T>::insertNode(Stack<BinaryTreeNode<T> *> &trace) {
    // 如果没有俩, 说明状态一定错了, pop会抛出异常
    BinaryTreeNode<T> *newNode = trace.pop();
    if (this->root == nullptr && this->size == 0) {
        this->root = newNode;
        this->size++;
        trace.push(newNode);
        return;
    } else if (this->root == nullptr || this->size == 0) {
        throw IllegalStatementException();
    }
    BinaryTreeNode<T> *nodeParent = trace.top();
    trace.push(newNode);
    if (nodeParent == nullptr) {
        throw IllegalArgumentException();
    }
    if (GO_LEFT_BY(nodeParent->compareTo(newNode))) {
        nodeParent->setLeftRemoveOld(newNode);
    } else {
        nodeParent->setRightRemoveOld(newNode);
    }
    this->size++;
}
```



用于检查trace栈里的节点是否都有继承关系的函数

```cpp
template<class T>
void BinarySearchTree<T>::traceFamily(Stack<BinaryTreeNode<T> *> trace, BinaryTreeNode<T> *root) {
    if (trace.empty()) {
        return;
    }
    BinaryTreeNode<T> *node = trace.pop();
    if (trace.empty()) {
        return;
    }
    BinaryTreeNode<T> *nodeParent = trace.pop();
    while (true) {
        BinaryTree<T>::isLeftChild(node, nodeParent);
        if (trace.empty()) {
            break;
        }
        node = nodeParent;
        nodeParent = trace.pop();
    }
    if (nodeParent != root) {
        throw IllegalArgumentException();
    }
}

```



### 增

新增加一个节点之后, 开始调整这棵树, 使其平衡

从这个节点开始依次遍历父节点, 如果访问到的节点不平衡了, 就调整这棵树

如果访问到的所有节点都是平衡的, 就不用调整

如果调整了一个节点之后, 这个节点的父和祖辈节点就不需要再访问, 再调整了

如果插入始终是依照AVL树的规范来了, 更祖辈的节点保证已经平衡

```cpp
void insertNode(Stack<BinaryTreeNode<T> *> &trace) override {
    this->BinarySearchTree<T>::insertNode(trace);
    BinaryTreeNode<T> *node = trace.pop();
    BinaryTreeNode<T> *nodeParent = trace.empty() ? nullptr : trace.pop();
    while (rotate2Adjust(node, nodeParent)) {// 更改函数, 使其返回是否经过调整
        node = nodeParent;
        nodeParent = trace.empty() ? nullptr : trace.pop();
    }

}
```

### 删

删除了一个节点之后, 开始调整这棵树, 使其平衡

从这个节点开始依次遍历父节点, 如果访问到的节点不平衡了, 就调整这棵树

如果访问到的所有节点都是平衡的, 就不用调整

如果调整了一个节点之后, 这个节点的父和祖辈节点仍然需要再访问, 再调整

```cpp
void removeNode(Stack<BinaryTreeNode<T> *> &trace) override {
    this->BinarySearchTree<T>::removeNode(trace);
    if (trace.empty()) {
        // 树唯有根节点
        return;
    }
    BinaryTreeNode<T> *node = trace.pop();
    if (trace.empty()) {
        // 树唯有一个根节点
        rotate2Adjust(node, nullptr);
        return;
    }
    BinaryTreeNode<T> *nodeParent = trace.pop();
    while (true) {
        rotate2Adjust(node, nodeParent);
        if (nodeParent == nullptr) {
            break;
        }
        node = nodeParent;
        nodeParent = trace.empty() ? nullptr : trace.pop();
    }
}
```

