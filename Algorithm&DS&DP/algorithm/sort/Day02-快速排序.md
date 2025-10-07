#快速排序

最坏情况O(n^2^), 在原数据递增或递减或完全相等时成为最坏情况, 但是期望是O(log(n))

而且不需要开辟新的内存空间(原址排序)

## 思想

分治思想

1.  分解
    1.  数组Array划分两个子数组(可为空)A[start:mid], A[mid+1:end]
    2.  使A[start:mid]小于等于A[mid], A[mid]小于等于A[mid+1:end]
    3.  先选择一个基准(一般是第一个或最后一个元素)
    4.  然后遍历, 所有小于基准的放在左边, 所有大于基准的放在右边
    5.  基准就放在中间
2.  解决
    1.  用递归, 对子数组A[start:mid], A[mid+1:end]排序
3.  合并
    1.  原址排序, 物理上已经拍好了

## 实现

递归

```cpp
void quickSort(int start, int end) {
    if (end <= start) {
        return;
    }
    int mid = partition(start, end);
    quickSort(start, mid);
    quickSort(mid + 1, end);
}
```



分解

```cpp
int partition(int start, int end) {
    T element = this->arr[end - 1]; // 选择基准
    int slow = start;
    for (int fast = start; fast < end - 1; ++fast) {
        if (this->cmp(element, this->arr[fast]) > 0) {
            this->arr.elementSwap(slow++, fast);// 移到左边
        }
    }
    this->arr.elementSwap(slow, end - 1);
    return slow;
}
```

栈+循环的版本

```cpp
struct IndexBound {
    int start;
    int end;

    IndexBound(int start, int end) : start(start), end(end) {}
};
```



```cpp
void quickSort() {
    Stack<IndexBound> st;
    st.push(IndexBound(0, this->arr.length()));
    while (!st.empty()) {
        IndexBound top = st.pop();

        int start = top.start;
        int end = top.end;
        if (end <= start) {
            continue;
        }
        int mid = this->partition(top.start, top.end);
        if (start < mid) {
            st.push(IndexBound(start, mid));
        }
        if (mid + 1 < end) {
            // 可以不判断, 不影响结果正确性
           	// 判断一下, 占用CPU
            // 不判断, 存进去, 占用内存
            st.push(IndexBound(mid + 1, end));
        }
    }
}
```

## 随机化

输入数据都是等概率的时候, 能让快速排序更快

```cpp
int randomPartition(int start, int end) {
    this->arr.elementSwap(end - 1,Random::randint(start,end));
    return partition(start, end);
}
```

随机数生成比较消耗事件所以很不愿意使用这个随机化

## HoarePartition

另一版本的算法

```cpp
int hoarePartition(int start, int end) {
    T element = this->arr[start]; // 选择基准
    int i = start + 1;
    int j = end - 1;
    while (true) {
        for (; i < end && this->arr[i] < element; ++i);
        for (; j > start && this->arr[j] > element; --j);
        if (i < j) {
            this->arr.elementSwap(i, j);
        } else {
            this->arr.elementSwap(start, j);
            return j;
        }
    }
}
```

