# 查找排序

## 简单查找排序

>   Simple Selection Sort

从剩余数组中找到最小的元素加入到子数组的最前面

```cpp
void sort() override {
    int size = this->arr.getSize();
    for (int i = 0; i < size-1; ++i) {
        int min = this->arr.at(i);
        int minIndex = i;
        for (int j = i + 1; j < size; ++j) {
            T &item = this->arr.at(j);
            if (this->cmp(item, min) < 0) {
                min = item;
                minIndex = j;
            }
        }
        this->arr.elementSwap(i, minIndex);
    }
}
```
