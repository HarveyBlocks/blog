# 优先队列

基于堆的实现

[堆.md](../../Data-Struction/tree/Day05-Heap.md)

基于最大堆实现的最大优先队列

基于最小堆实现的最小优先队列

此处讨论最大优先队列

## 概念

优先队列是用来维护由一组元素构成的**集合**的数据结构

其中的每一 个元素都有一个相关的值，称为关键字 (key) , score?Prior?



## 构建优先队列

优先队列依据优先值升序排序, 而堆天然有根节点最大的特点

故在优先队列构建时先构建最大堆

```cpp
explicit PriorQueue(const Array<T> &arr, bool ascending = true) :
        HeapSort<T>(arr, ascending), heapSize(arr.length()) {
    this->buildMaxHeap();
}
```

## 获取最优先事件

```cpp
T max() {
    return this->arr[0];
}
```

## 最优先事件出队

```cpp
T extractMax() {
    if (this->heapSize < 0) {
        throw Exception("Heap underflow");
    }
    if (this->heapSize == 0) {
        throw Exception("Heap is Empty");
    }
    T max = this->arr[0];
    this->arr.elementSwap(0, this->heapSize - 1); // 从堆中逻辑删除事件
    this->heapSize--;
    this->maxHeapify(0);
    return max;
}
```

此行为和堆排序的for循环中逻辑相似

故堆排序可改为

```cpp
template<class T>
void HeapSort<T>::sort() {
    while (this->heapSize > 0) {
        extractMax();
    }
}
```

## 将事件的优先级提高

已知某一事件所在的索引, 提高这一事件的优先级

```cpp
void increasePriority(int index, T newPriority) {
    if (this->cmp(newPriority, this->arr[index]) < 0) {
        throw IllegalArgumentException("New priority is smaller than current key");
    }
    this->arr[index] = newPriority;
    while (index > 0 && this->cmp(this->arr[index], this->arr(parent(index))) > 0) {
        // 如果当前节点和它爹倒反天罡了, 就换爹
        this->arr.elementSwap(index, parent(index));
        index = parent(index);
    }
}
```

<img src="../../assetss/Day06-PriorQueue/image-20240618182511967.png" alt="image-20240618182511967" style="zoom:80%;" />

将要加入的元素加入堆/队尾

```cpp
void increasePriority(T newPriority) {
    this->arr[this->heapSize] = INT_MIN;
    heapSize++;
    increasePriority(newPriority);
}
```

