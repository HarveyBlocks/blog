# 插入排序

>   Insertion Sort

![image-20240617183323122](../../assets/Day01-%E6%8F%92%E5%85%A5%E6%8E%92%E5%BA%8F/image-20240617183323122.png)



## 思想

想要升序, 遍历到当前元素, 检查这个元素前面的元素, 如果比当前元素大, 就将当前元素和前面的元素互换

![image-20240617144204834](../../assets/Day01-%E6%8F%92%E5%85%A5%E6%8E%92%E5%BA%8F/image-20240617144204834.png)

## 流程

 ![img](../../assets/Day01-%E6%8F%92%E5%85%A5%E6%8E%92%E5%BA%8F/insertionSort.gif)

## 实现

```cpp
for (int i = 0; i < this->len; ++i) {
    T key = arr[i];
    //  Insert arr[i] into the sorted sequence A[1 ... i —1].
    int j = i - 1;
    for (; j >= 0 && cmp(arr[j], key) > 0; j--) {
        arr[j + 1] = arr[j];
    }
    arr[j + 1] = key;
}
```

```cpp
int cmp(int a, int b) {
    return a - b; // 升序
}
```

当比较unsigned int, long long等比int大的数据的时候, 不要使用

```cpp
int cmp(unsigned int a, unsigned int b) {
    return a - b;
}
```

可能会导致溢出而不准确, 可以根据具体的数据类型微调

```cpp
int cmp(unsigned int a, unsigned int b) {
    return a==b?0:(a>b?1:-1);
}
```



## 复杂度

时间复杂度O(n^2^)

空间复杂度O(1)

## 折半插入

找到插入位置的过程, 使用折半查找

```cpp
template<class T>
void BinarySearchInsertionSort<T>::sort() {
    for (int i = 1; i < this->arr.getSize(); ++i) {
        T key = this->arr[i];
        int j = i - 1;
        int insertIndex = findInsertIndex(i - 1, key);
        for (; j >= insertIndex; j--) {
            this->arr[j + 1] = this->arr[j];
        }
        this->arr[j + 1] = key;
    }
}

template<class T>
int BinarySearchInsertionSort<T>::findInsertIndex(int index, T key) const {
    // 对已经有顺序的部分折半查找减少查找, 比较次数
    // 但插入需要挪动, 赋值交换次数不变
    // 但是每次查找都要创建一个新的数组, 进行
    int insertIndex = Arrays::binarySearch(this->arr, key, this->cmp, 0, index, 1);
    if (insertIndex < 0) {
        insertIndex = -insertIndex - 1;
    } else {
        insertIndex++;// 如果相同, 就插入后面, 保证排序的稳定性(原来在后面的还在后面)
    }
    return insertIndex;
}
```



## 希尔排序

插入排序的特点: 

1.  越有序, 越快
2.  越短, 越快

则思想为: 

1.  索引1,3,5,7,9......进行排序
2.  索引0,2,4,6,8......进行第排序
3.  此时奇数位和偶数位各自有序, 数组整体较为有序, 然后对数组整体进行排序

过程:

1.  索引之间差为d的几个子数组做排序
2.  d>>=1
3.  直到d=0

```cpp
template<class T>
int ShellSort<T>::findInsertIndex(int start, int end, T key, int step) const {
    if (end < 0) {
        return 0;
    }
    // 对已经有顺序的部分折半查找减少查找, 比较次数
    // 但插入需要挪动, 赋值交换次数不变
    // 但是每次查找都要创建一个新的数组, 进行
    int insertIndex = Arrays::binarySearch(this->arr, key, this->cmp, start, end, step);
    if (insertIndex < 0) {
        insertIndex = -insertIndex - step;
    } else {
        insertIndex++;// 如果相同, 就插入后面, 保证排序的稳定性(原来在后面的还在后面)
    }
    return insertIndex;
}


template<class T>
void ShellSort<T>::sort() {
    for (int direct = this->arr.getSize() >> 1; direct > 0; direct >>= 1) {
        for (int start = 0; start < direct; ++start) {
            sort0(start, direct);
        }
    }
}

template<class T>
void ShellSort<T>::sort0(int start, int direct) {
    int size = this->arr.getSize();
    for (int i = start; i < size; i += direct) {
        T key = this->arr[i];
        int j = i - direct;
        int insertIndex = findInsertIndex(start, j, key, direct);
        for (; j >= insertIndex
            /*j >= 0 && this->cmp(this->arr[j], key) > 0*/;
               j -= direct) {
            this->arr[j + direct] = this->arr[j];
        }
        this->arr[j + direct] = key;
    }
}
```

### 缺陷

每次, d都是前一个d的因子(n分之一), 如果前一次d子序列是有序的, 可能就做无用功

