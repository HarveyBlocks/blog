# 归井排序

>   Merge Sort

## 思想

分治

![img](../../assets/Day01-%E5%BD%92%E4%BA%95%E6%8E%92%E5%BA%8F/20200524221406526.gif)

<img src="../../assets/Day01-%E5%BD%92%E4%BA%95%E6%8E%92%E5%BA%8F/image-20240617145849061.png" alt="image-20240617145849061" style="zoom:67%;" />

## 实现

合并数组(Merge)

```cpp
template<class T>
void SortUtil<T>::merge(int start, int mid, int end) {
    // start, mid, end为索引
    // arr[start:mid]已排序
    // arr[mid:end]已排序
    int len1 = mid - start;
    int len2 = end - mid;

    // 合并两个数组, 合成一个新有序数组
    T temp1[len1];
    T temp2[len2];
    for (int i = 0; i < len1; i++) {
        temp1[i] = arr[start + i];
    }
    for (int i = 0; i < len2; i++) {
        temp2[i] = arr[mid + i];
    }
    int i = 0;
    int j = 0;
    int k = start;

    for (; k < end; k++) {
        if (i != len1 && (j == len2 || this->cmp(temp1[i], temp2[j]) < 0)) {
            this->arr[k] = temp1[i];
            i++;
        } else {
            this->arr[k] = temp2[j];
            j++;
        }
    }
}

```

分治

```cpp
template<class T>
void SortUtil<T>::mergePartition(int start, int end) {
    if (1 >= end - start) {
        return;
    }
    int mid = (start + end) / 2;
    mergePartition(start, mid);
    mergePartition(mid, end);
    merge(start, mid, end);
}
```

暴露接口

```cpp
template<class T>
void SortUtil<T>::mergeSort() {
    mergePartition(0, len);
}
```



## 复杂度



时间复杂度 O(nlog(n)) 

空间复杂度O(n)

