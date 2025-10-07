# HashMap类

- 线程不安全
- 运行效率快

- 基于哈希表的实现的Map接口。  
- 此类实现了所有可选的Map操作
- 允许 null值 和 null键
- 存储结构为哈系树

## 构造器

- HashMap()  
  - 构造一个空的 HashMap 
  - 默认初始容量（16）
- 默认负载系数（0.75）。[负载系数指达到容量(16)的(0.75)时,即12(16*0.75)时进行扩容]
- HashMap(int initialCapacity) 
  - 构造一个空的 HashMap
    - 指定初始容量
    - 默认负载因子（0.75）
- HashMap(int initialCapacity,  float loadFactor) 
  - 构造一个空的HashMap
    - 指定初始容量(initialCapacity)
    - 指定负载因子(loadFactor)。

## 方法

一样一样的 [英语翻译到中文.html](..\..\..\..\英语翻译到中文.html) 

因为hashMap()的数据结构是哈希树

加入新元素时判断key是否重复依据时key的类的equals()和hashCode()

## 源码分析

### 属性

```java
static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
//hashMap默认_初始_容量大小
static final int MAXIMUM_CAPACITY = 1 << 30;
//hashMap的数组最大容量
static final float DEFAULT_LOAD_FACTOR = 0.75f;
//默认负载系数

static final int TREEIFY_THRESHOLD = 8;//树化阈值
//hashMap本来是哈希表,当哈希表的链表长度大于8时
static final int MIN_TREEIFY_CAPACITY = 64;//最小树化容量
//当集合元素个数大于等于64时
//          ↓
//当哈希表的链表长度大于8 && 集合元素个数大于等于64
//调整成红黑树


static final int UNTREEIFY_THRESHOLD = 6;//取消树化阈值
//当链表长度小于6时调整成链表

transient Node<K,V>[] table;//默认null
//哈希表中的数组
transient int size;//默认0
//元素个数,默认为0节省空间
```

### Node<K,V> 内部类

```java
static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;
    final K key;
    V value;
    Node<K,V> next;
    <...方法>
}    
```

### put()

table;//默认null
size;//默认0

```java
public V put(K key, V value) {
    return putVal(hash(key), key, value, false, true);
    //-------------↑产生hash值,不管他
}//转putVal()↓
```

```java
final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
               boolean evict) {
    Node<K,V>[] tab; Node<K,V> p; int n, i;
    if ((tab = table) == null || (n = tab.length) == 0)
        n = (tab = resize()).length;
    <......>//------↑
}//转resize()↓
```

对不起,可怜的我不知道接下来的东西怎么记到笔记里qwq

```java
final Node<K,V>[] resize() {
    <...>
    newCap = DEFAULT_INITIAL_CAPACITY;//16
    <...>
    Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];//newCap == 16
    table = newTab;
    <...>
}
```

将table初始化为了16

```java
//回到putVal()
final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
    <...>
     if ((tab = table) == null || (n = tab.length) == 0)
            n = (tab = resize()).length;//n = 16
     if ((p = tab[i = (n - 1) & hash]) == null)
            tab[i] = newNode(hash, key, value, null);
    <...>
    //不断加元素↓直到↓大于16*0.75
    if (++size > threshold)
            resize()//回到resize(),每次都是原来长度的2倍,目的是为了减少调整元素的个数
}
```

```java

final Node<K,V>[] resize() {
    <...>
    newCap = DEFAULT_INITIAL_CAPACITY;//16
    <...>//---------------左移1位,放大两倍------最大容量
        else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
             oldCap >= DEFAULT_INITIAL_CAPACITY)
        newThr = oldThr << 1; // double threshold
}
```

## hashSet()和hashMap()之间的关系

我们来看一看hashSet()的源码:

只见那里赫然写着一句:

```java
private transient HashMap<E,Object> map;
public HashSet() {map = new HashMap<>();}
```

这何尝不是一种NTR

```java
public boolean add(E e) {return map.put(e, PRESENT)==null;}
```

HashSet里的值就是用HashMap里的键来存的

