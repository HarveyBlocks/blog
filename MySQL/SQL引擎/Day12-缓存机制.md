# 缓存

## Buffer Pool

> flush和free不是每一个控制块都有的属性, 而是放在两个不同的链表是吗? 也就是说, 在某个缓存也的属性取决于这个缓存页对应的控制块属于哪个链表是吗? 
>
> 什么时候free, 什么时候flush, 什么时候什么都不是? 有改变需要落盘的, 是为flush吗? 那free呢? 有新的缓存需要进入, 允许free的free吗? 

MySQL申请一片连续的空间用于缓存

读数据的最小单位是页, 缓存也以页为单位进行存储

缓冲页大小和存储的磁盘页大小一致(16KB)

![BufferPool](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MySQL/SQL引擎/Day12-缓存机制/BufferPool.png)

控制块会分为`free` 链表和`frush`链表

- frush 链表, 缓存被写过的但没有落盘的 **脏页**
- free 存储空闲页, 啥都没有

## IRU 链表

磁盘读入内存的页, 会放入LRU链表

LRU链表的节点是由frush链表中的所有节点和另一部分的只读未写的节点组成的

LRU 前 $75\%$ 是young区, 后 $25\%$ 是old 区

young 区节点被访问后需要先判断距离上次被访问的时间是否大于一个阈值的时间, 不大于则不移动

old 区的页优先被驱逐



## 脏页落盘

后台线程定期执行工作

- LRU 的 old 中的脏页进行刷新
- flush链表中刷新 **一部分**



