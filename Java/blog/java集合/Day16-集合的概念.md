# 集合的概念

- 可变长度的数组
- 数组可以存储基本类型和引用类型，集合只能存储引用类型（用装箱把基本类型转换成引用类型）

![Interface Collection](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java集合/Day16-集合的概念/Interface Collection.png)

## Interface Collection

### Interface List

有序，有下标，元素可重复

#### class ArrayList
数组列表

#### class LinkedList
链表

#### class Vector
老了，不用了

### Interface Set

无序，无下标，元素不重复

#### class HashSet

存储结构哈希表

​		class LInkedHashSet是链表,其他完全一样

#### Interface SortedSet
用于排序

##### class TreeSet

存储结构红黑树

![Map集合体系](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java集合/Day16-集合的概念/Map集合体系.png)

## Interface Map

用于存储任意键值对

键:无序,无下标,不允许重复(唯一)

值:无序,无下标,允许重复

### class HashMap

存储结构哈希表

### Interface SortedMap

用于排序

#### class TreeMap

存储结构红黑树

## Collections工具类

