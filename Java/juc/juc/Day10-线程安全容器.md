# 线程安全容器

## 线程安全集合的原生实现

-   Vector
-   HashTable

历史悠久, 简单粗暴的方法上synchrinuzed

## 修饰器线程安全集合

```java
Collection<Object> collection = Collections.synchronizedCollection(list);
```

简单的方法内synchrinuzed修饰

## JUC线程安全集合

### Blocking-

用ReentrantLock实现的阻塞集合

### CopyOnWrite-

写入时拷贝, 写操作时拷贝一份, 更改操作在新数组上执行, 此时不影响旧数组上的读写

只有在写写时阻塞

类似不可变类的思想, 来回拷贝保证集合的不变, 以达到线程安全的目的

开销大, 适合读多写少的场景

**弱一致性**

删除第0个元素, 然后按索引读取, 删除完之后覆盖数组(字段赋值新数组的引用,当然时间很短), 然后索引和元素完全错位了

### Concurrent-

用CAS优化, 锁的粒度低,提供较高的吞吐量

弱一致性

-   遍历时, 如果容器发生修改 , 迭代器可以继续遍历, 不过内容是旧的
-   size的值也是弱一致性的
-   读取时是弱一致性的

>   对于非线程安全容器来说, 使用fail-fast机制, 发生线程安全问题, 立刻让遍历失败, 抛出`ConcurrentModificationException`

