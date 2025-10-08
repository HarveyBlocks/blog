# ConcurrentHashMap

## 使用

统计一个二维数组中的所有Interger的个数

### 单线程+HashMap

```java
Map<Integer, Integer> result = new HashMap<>();
for (List<Integer> list : source) {
    for (Integer i : list) {
        result.merge(i, 1, Integer::sum);
    }
}
return result;
```
### 线程池+HashMap

```java
Map<Integer, Integer> result = new HashMap<>();
int size = source.size();
ExecutorService pool = Executors.newFixedThreadPool(size);
CountDownLatch latch = new CountDownLatch(size);
for (List<Integer> list : source) {
    pool.submit(() -> {
        try {
            for (Integer i : list) {
                result.merge(i, 1, Integer::sum);
            }
        } catch (Throwable t) {
            // ConcurrentModificationException
            // 不catch住, 线程池直接忽略
            t.printStackTrace(System.err);
        } finally {
            latch.countDown();
        }
    });
}
try {
    latch.await();
} catch (InterruptedException e) {
    throw new RuntimeException(e);
}
pool.shutdown();
return result;
```

抛出了异常ConcurrentModificationException

很愉快地产生了线程安全问题, 那肯定不行啊

### 线程池+ConcurrentHashMap

```java
Map<Integer, Integer> result = new ConcurrentHashMap<>();
int size = source.size();
ExecutorService pool = Executors.newFixedThreadPool(size);
CountDownLatch latch = new CountDownLatch(size);
for (List<Integer> list : source) {
    pool.submit(() -> {
        try {
            for (Integer i : list) {
                Integer count = result.get(i);
                result.put(i, count == null ? 1 : count + 1);
            }
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        } finally {
            latch.countDown();
        }
    });
}
try {
    latch.await();
} catch (InterruptedException e) {
    throw new RuntimeException(e);
}
pool.shutdown();
return result;
```

```log
{0=291, 1=469, 2=457, 3=468, 4=469, 5=462, 6=466, 7=478, 8=443, 9=483, 10=453, 11=476, 12=453, 
13=439, 14=453, 15=475, 16=467, 17=467, 18=416, 19=474, 20=468, 21=474, 22=479, 23=450, 24=470, 
25=473, 26=474, 27=465, 28=482, 29=469, 30=466, 31=483, 32=482, 33=471, 34=477, 35=467, 36=465, 
37=479, 38=312, 39=462, 40=471, 41=476, 42=483, 43=477, 44=463, 45=331, 46=483, 47=471, 48=475, 
49=464, 50=476, 51=469, 52=476, 53=473, 54=466, 55=470, 56=472, 57=471, 58=477, 59=469, 60=485, 
61=479, 62=472, 63=471, 64=440, 65=458, 66=476, 67=482, 68=476, 69=468, 70=467, 71=482, 72=482, 
73=459, 74=432, 75=471, 76=281, 77=474, 78=453, 79=477, 80=493, 81=458, 82=446, 83=466, 84=445, 
85=486, 86=478, 87=479, 88=446, 89=432, 90=455, 91=473, 92=469, 93=483, 94=460, 95=426, 96=474, 
97=483, 98=468, 99=477, 100=465, 101=485, 102=485, 103=468, 104=468, 105=232, 106=462, 107=475, 
108=448, 109=484, 110=301, 111=476, 112=469, 113=473, 114=473, 115=473, 116=476, 117=482, 118=465, 
119=466, 120=479, 121=476, 122=472, 123=464, 124=480, 125=465, 126=470, 127=485}
```

还是不对???

```java
Integer count = result.get(i);
result.put(i, count == null ? 1 : count + 1);
```
一读一写, 线程不安全!

```java
result.merge(i, 1, Integer::sum);
```

这样其实是没问题的.....这个API是IDEA提示的....

### computeIf....

```java
Map<Integer, LongAdder> result = new ConcurrentHashMap<>();
```

```java
LongAdder adder = result.computeIfAbsent(i, k -> new LongAdder());
adder.increment();
```

## JDK7的HashMap并发死链

-   HashMap对加入到数组同一个元素(Hash值相同), 但是不相等的元素, 会形成链表, JDK7新加入的Key会放在链表头;JDK8后加入的Key会放在链表尾
-   HashMap的元素达到其数组容量的3/4, 此时即将扩容而不会扩容, 再增加一个元素就扩容
-   HashMap的扩容会将所有元素的Hash值重新计算, 并取模(新容量), 然后存入新数组
-   多个线程同时对同一个HashMap上的同一个节点放置一个元素, 两个线程都将引发扩容
-   如果一个线程重新完成了扩容之后, 另一个线程还停留再扩容的正当时
    -   此时, HashMap的`transfer(Entry[],boolean)`方法中, 有局部变量e连接这老元素的头节点, next指向新元素的头节点
    -   这个新元素的头节点, 由于在扩容重排之后, 指向了老元素
    -   next->e, e->next, 就成死链了
    -   大概

JDK8 通过将新节点加到头, 修复了死链, 然而还是会有扩容的丢数据问题
