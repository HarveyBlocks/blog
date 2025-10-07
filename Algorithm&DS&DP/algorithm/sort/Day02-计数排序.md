# 计数排序

当知道有n个元素小于X时, 元素X就应该放到索引为n的位置上

考虑到有元素相等, 需要略作调整

## 思路

Hash映射, 构建一个HashMap,key-value=value-count

依据这个CountMap作一个map->array的映射

## 实现



```cpp
// 适合整形排序, 小数量的(256不能再多了), 范围限定的, 范围不大的
int maxInArr = INT_MIN;
int minInArr = INT_MAX;
int len = this->arr.length();
if (len <= 1) {
    return;
}
// 已知数组中的最大值
for (int i = 0; i < len; ++i) {
    if (this->arr[i] > maxInArr) {
        maxInArr = this->arr[i];
    }
    if (this->arr[i] < minInArr) {
        minInArr = this->arr[i];
    }
}
int scope = maxInArr - minInArr + 1;
int indexLib_[scope];
Array<int> indexLib(indexLib_, scope);
for (int i = 0; i < scope; ++i) {
    indexLib[i] = 0;
}

for (int i = 0; i < len; ++i) {
    indexLib[this->arr[i] - minInArr]++; // 统计数量
}
indexLib[0]--; // 需要让索引0也会有数据
for (int i = 1; i < scope; ++i) {
    indexLib[i] += indexLib[i - 1];
}
int result_[len];
Array<int> result(result_, len);
for (int i = len - 1; i >= 0; --i) {
    int element = this->arr[i];
    result[indexLib[element - minInArr]--] = element;
    // 倒序
    // result[len - 1 - (indexLib[element - minInArr]--)] = element;
}
Array<int>::copy(result, arr);
```

适合整形排序, 小数量的(256不能再多了), 范围限定的, 范围不大的, 例如日期年月日的统计排序