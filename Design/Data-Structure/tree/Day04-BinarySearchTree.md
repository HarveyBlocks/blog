# 二叉搜索树

## 查找

```cpp
template<class T>
Stack<BinaryTreeNode<T> *> BinarySearchTree<T>::search(const T &value) {
    Stack<BinaryTreeNode<T> *> stack;
    if (this->root == nullptr) {
        return stack;
    }
    BinaryTreeNode<T> *node = this->root;
    while (node != nullptr && *(node->getValue()) != value) {
        int cmp = node->compareTo(value);
        stack.push(node);
        if (cmp >= 0) {
            // root 大
            node = node->getLeft();
        } else {
            node = node->getRight();
        }
    }
    return stack;
}
```

## 构建和插入

```cpp
template<class T>
void BinarySearchTree<T>::insert(const T &value) {
    if (this->root == nullptr) {
        initRoot(value);
        return;
    }
    BinaryTreeNode<T> *node = this->root;
    BinaryTreeNode<T> *nodeParent = node;
    bool goLeft = false;
    while (node != nullptr) {
        int cmp = node->compareTo(value);
        nodeParent = node;
        if (GO_LEFT_BY(cmp)) {
            // root 大
            node = node->getLeft();
            goLeft = true;
        } else {
            node = node->getRight();
            goLeft = false;
        }
    }
    insertNode(value, nodeParent, goLeft);
}

template<class T>
void BinarySearchTree<T>::initRoot(const T &value) {
    this->root = new BinaryTreeNode(value);
    this->size = 1;
}

template<class T>
void BinarySearchTree<T>::insertNode(const T &value, BinaryTreeNode<T> *nodeParent, bool goLeft) {
    if (goLeft) {
        nodeParent->setLeft(new BinaryTreeNode(value));
    } else {
        nodeParent->setRight(new BinaryTreeNode(value));
    }
    this->size++;
}
```

## 删除

-   删除叶子
    1.  直接删除
-   删除只有右子树的节点
    1.  右子树代替本节点
-   删除只有左子树的节点
    1.  左子树代替本节点
-   删除有左右子树的节点
    1.  找到本节点左子树中最大的节点X
    2.  节点X的值覆盖本接待你
    3.  删除节点X

```cpp
template<class T>
int BinarySearchTree<T>::remove(const T &value) {
    if (this->root == nullptr || this->empty()) {
        return 0;
    }

    int count = 0;
    while (value == *(this->root->getValue())) {
        count++;
        if (this->size == 1) {
            remove0(this->root);
            this->root = nullptr;
            return count;
        }
        removeNode(this->root, nullptr, true);
    }
    while (true) {
        BinaryTreeNode<T> *node = this->root;
        BinaryTreeNode<T> *nodeParent = node;
        bool goLeft = true;
        while (node != nullptr) {
            int cmp = node->compareTo(value);
            if (cmp > 0) {
                // root 大
                nodeParent = node;
                node = node->getLeft();
                goLeft = true;
            } else if (cmp < 0) {
                nodeParent = node;
                node = node->getRight();
                goLeft = false;
            } else {
                break;
            }
        }
        if (node == nullptr) {// 没找到
            break;
        }
        count++;
        removeNode(node, nodeParent, goLeft);
    }
    return count;
}

template<class T>
void BinarySearchTree<T>::removeNode(BinaryTreeNode<T> *node, BinaryTreeNode<T> *nodeParent, bool goLeft) {
    if (node == nullptr) {
        return;
    }
    BinaryTreeNode<T> *left = node->getLeft();
    BinaryTreeNode<T> *right = node->getRight();
    if (left == nullptr && right == nullptr) {
        // 1
        if (nodeParent == nullptr) {
            // root;
            remove0(node);
            this->root = nullptr;
            return;
        }
        if (goLeft) {
            nodeParent->setLeft(nullptr);
        } else {
            nodeParent->setRight(nullptr);
        }
        remove0(node);
        return;
    }
    if (right == nullptr) {
        // 2
        if (nodeParent == nullptr) {
            // root;
            remove0(node);
            this->root = left;
            return;
        }
        if (goLeft) {
            nodeParent->setLeft(left);
        } else {
            nodeParent->setRight(left);
        }
        remove0(node);
        return;
    }
    if (left == nullptr) {
        // 3
        if (nodeParent == nullptr) {
            // root;
            remove0(node);
            this->root = right;
            return;
        }
        if (goLeft) {
            nodeParent->setLeft(right);
        } else {
            nodeParent->setRight(right);
        }
        remove0(node);
        return;
    }
    // 4
    BinaryTreeNode<T> *substitute = left; // 替补
    BinaryTreeNode<T> *substituteParent = node; // 替补
    goLeft = true;
    while (substitute->getRight() != nullptr) {
        goLeft = false;
        substituteParent = substitute;
        substitute = substitute->getRight();
    }
    node->setValue(substitute->getValue());
    removeNode(substitute, substituteParent, goLeft);
}

template<class T>
void BinarySearchTree<T>::remove0(const BinaryTreeNode<T> *node) {
    delete node;
    this->size--;
}
```

## 判断左右

判断左孩子还是右孩子的函数, 返回布尔值, 但是不具备继承关系就抛出异常



```cpp
template<class T>
bool BinaryTree<T>::isLeftChild(const BinaryTreeNode<T> *node, const BinaryTreeNode<T> *nodeParent) {
    if (nodeParent == nullptr) {
        throw NullPointException();
    }
    BinaryTreeNode<T> *parentLeft = nodeParent->getLeft();
    BinaryTreeNode<T> *parentRight = nodeParent->getRight();
    if (parentLeft == nullptr && parentRight == nullptr) {
        throw IllegalArgumentException();
    }

    bool same2Left = parentLeft == node;
    bool same2Right = parentRight == node;

    if (parentLeft == nullptr && parentRight != nullptr) {
        if (!same2Right) {
            throw IllegalArgumentException();
        }
        return false;
    }
    if (parentLeft != nullptr && parentRight == nullptr) {
        if (!same2Left) {
            throw IllegalArgumentException();
        }
        return true;
    }

    if (same2Left && !same2Right) {
        return true;
    }
    if (!same2Left && same2Right) {
        return false;
    }
    throw IllegalArgumentException();
}
```

## 持久动态集合

需求: 每次树有变化的时候, 都保存这个状态, 用于追溯历史状态

实现: 如果每次都拷贝一个树, 要消耗事件, 一些节点上的值总是被重复

为每一个版本提供一个根节点, 新版本只存储和新节点有关的树信息, 其他数据和节点复用老版本的

<img src="../../assetss/Day04-BinarySearchTree/image-20240629235929849.png" alt="image-20240629235929849" style="zoom:67%;" />

该策略下: 

1.  考虑增删涉及的节点
2.  若节点保存父节点指针, 考虑需要更改的信息(3的父亲到底指向谁啊)

## 连接   *Join*

集合S~1~和S~2~, max{S~1~}<a~n~<min{S~2~}, 集合以红黑树存储, 思考如何获取S, S = S~1~∪{a~n~}∪S~2~ (S~1~和S~2~可销毁)

a~n~一定是适合作为初始根节点的

