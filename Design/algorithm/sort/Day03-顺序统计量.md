# 顺序统计量

## 最值

要同时获取一个数组中的最大值和最小值

将**一对**输入元素相互进行比较，然后把较小的与当前最小值比较，把较大的与当前最大值进行比 较。这样，对每两个元素共需3次比较。

但是奇数偶数要分开讨论, 代码难免繁琐, 而且数据量小的时候没有优势

奇数可以先把第一个元素作为max和min的初值



```cpp
if (len <= 0) {
    return;
}
int end = len;
T max;
T min;
if (len % 2) {
    max = arr[len - 1];
    min = arr[len - 1];
    end--;
} else {
    T ele1 = arr[len - 1];
    T ele2 = arr[len - 2];
    if (ele1 > ele2) {
        max = ele1;
        min = ele2;
    } else {
        max = ele2;
        min = ele1;
    }
    end -= 2;
}
end = end / 2 - 1;
for (int i = 0; i < end; ++i) {
    int ele1 = arr[i * 2];
    int ele2 = arr[i * 2 + 1];
    if (ele1 > ele2) {
        if (ele1 > max) {
            max = ele1;
        }
        if (ele2 < min) {
            min = ele2;
        }
    } else {
        if (ele2 > max) {
            max = ele2;
        }
        if (ele1 < min) {
            min = ele1;
        }
    }
}
```





## 选择算法

找出数组中第i小的元素

已知快速排序时, 能够确定mid的位置绝对准确

所以, 当mid的值和 i 的值恰好相等时, 说明 i 已经找到

但是如果用快速排序的算法, 在mid之前和之后的都需要排, 但找到第i小的不需要这么做

只需要在每次排完mid之后, 如果 i 在A[start:mid]内, 就排A[start:mid], 如果 i 在A[mid+1:end]内, 就排A[mid+1:end]

此法的时间复杂度为O(n)

```cpp
template<class T>
T Arrays::tail(Array<T> &arr, const int level) {
    // level = 1表示smallest, index = 0
    if (level <= 0) {
        throw IllegalArgumentException();
    }
    return Arrays::randomSelect(arr, 0, arr.length(), level - 1);
}


template<class T>
T Arrays::randomSelect(Array<T> &arr, int start, int end, int level) {
    if (start == end) {
        return arr[start];
    }
    int mid = QuickSort<T>(arr).randomPartition(start, end);
    if (level == mid) {
        return arr[mid];
    } else if (level < mid) {
        return Arrays::randomSelect(arr, start, mid, level);
    } else {
        return Arrays::randomSelect(arr, mid + 1, end, level);
    }
}
```

