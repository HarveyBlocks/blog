# 查找

## 顺序查找

略

## 二叉查找

没找到, 就返回复数索引

```cpp
/**
 * @param low 索引, 包含
 * @param high 索引, 包含
 */
template<class T>
static int binarySearch(const Array<T> &arr, T key, Compare<T> compare,
                        int low, int high, int step = 1,
                        bool ascending = true) {
    if (step < 0) {
        throw IllegalArgumentException();
    }
    unsigned int num = (high - low) / step;
    if (high - low != num * step) {
        throw IllegalArgumentException();
    }
    int weight = ascending ? 1 : -1;
    while (low <= high) {

        num = (high - low) / step;
        int mid = (int) (low + (num >> 1) * step); // TODO 0

        long midVal = arr[mid];
        int cmpResult = compare(midVal, key) * weight;
        if (cmpResult < 0) {
            low = mid + step; // TODO 1
        } else if (cmpResult > 0) {
            high = mid - step; // TODO 1
        } else {
            return (int) mid; // key found
        }
    }
    return -(low + 1);  // key not found.
}
```

