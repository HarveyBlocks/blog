# 对象引用

可达性算法中描述的对象引用, 一般指强引用, 即GCRoot对象对普通对象有引用关系,只要这层关系存在, 普通对象就不会被回收

## 软引用

### 作用

如果一个对象只有软引用关联到它, 当程序内存不足时, 就会将软引用的数据进行回收

内存不足时

1.  虚拟机将没有被引用的垃圾回收
2.  还是不足, 将软引用的对象回收
3.  还是不足, `OutOfMemoryError`

### 应用场景

缓存

###使用

JDK1.2后提供了`SoftReference`对一个对象进行增强, 来指明对一个对象的引用在**不被强引用**且**内存不足**时就可以被回收

为了防止`SoftReference`对象不被回收, `SoftReference`应该被强引用

上面这句话不用太过在意

因为如果是缓存数据, 就必定存在一个多元素的数据容器里, 数据容器的元素必是`SoftReference`形成强引用, `SoftReference`内的对象和`SoftReference`形成弱引用, `SoftReference`内的元素当只剩下和`SoftReference`的软引用且内存不足时, `SoftReference`内的元素可被GC释放. 在`SoftReference`内的元素被释放时, 同时考虑到`SoftReference`本身也被释放

可以使用`ReferenceQueue`保存那些**释放掉`SoftReference`内数据**的`SoftReference`

```java
ReferenceQueue<byte[]> queue = new ReferenceQueue<>();
for (int i = 0; i < 10; i++) {
    byte[] bytes = new byte[1024 * 1024 * 100];
    SoftReference<byte[]> value = new SoftReference<>(bytes, queue);
    data.add(value);
}
```

很没用的积累功能





###Caffine中的软引用

Caffine有使用`SoftReference`的软引用的API

<img src="../asset/Day07-%E5%AF%B9%E8%B1%A1%E5%BC%95%E7%94%A8%E6%96%B9%E5%BC%8F/image-20240519165528163.png" alt="image-20240519165528163" style="zoom:50%;" />

```java
Cache<String, Object> cache = Caffeine.newBuilder().softValues().build();
cache.put("key","value");
```



## 弱引用

弱引用包含的对象在垃圾回收时, 不管内存够不够, 都会直接被回收

### 应用场景

ThreadLocal



###使用

JDK1.2之后提供WeakReference类来实现弱引用

弱引用对象也能使用队列来进行回收



```java
public static void main(String[] args) throws IOException {
    WeakReference<byte[]> weakReference = getWeakReference();
    System.out.println(weakReference.get() == null);
    System.gc();
    System.out.println(weakReference.get() == null);
}

private static WeakReference<byte[]> getWeakReference() {
    byte[] bytes = new byte[1024 * 1024 * 100];
    return new WeakReference<byte[]>(bytes);
}
```

## 虚引用

幽灵引用或幻影引用

不能通过虚引用对象获取到包含的对象. 

### 作用

当对象被垃圾回收器回收时接收到相应的通知

直接内存种为例即使知道直接内存对象不再使用, 从而回收内存, 即用虚引用实现

### 用法

PhantomReference

<img src="../asset/Day07-%E5%AF%B9%E8%B1%A1%E5%BC%95%E7%94%A8%E6%96%B9%E5%BC%8F/image-20240519204535900.png" alt="image-20240519204535900" style="zoom:50%;" />

写死了



## 终结器引用

对象需要被回收时, 终结器引用关联对象并放置在Finalizer类的引用队列种, 在稍后的一条由FinalizerThread线程从队列中获取队列, 然后执行对象的finalize方法, 在对象的第二次被回收时, 该对象才真正的被回收

这个过程可以在finalize方法(Object里的通用方法)中再将自身对象使用强引用关联上, 但是不建议

就算是清理工作, finalize不一定会被调用, 所以也不合适

一个对象的finalize方法只会被调用一次

![image-20240519210940014](../asset/Day07-%E5%AF%B9%E8%B1%A1%E5%BC%95%E7%94%A8%E6%96%B9%E5%BC%8F/image-20240519210940014.png)

