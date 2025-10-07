# ArrayList类[重点]

- 数组结构实现
  - 查询快,增减慢
- 运行效率高,线程不安全
- 有序,有下标,可重复

## 用法



```java
package CollectionLearning;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

//List接口的使用
public class Demo02 {
    public static void main(String[] args) {

        ArrayList arrayList = new ArrayList();
        ArrayList arrayList0 = new ArrayList<>();

        //添加
        Student s1 = new Student("张三",15);
        Student s2 = new Student("李四",16);
        Student s3 = new Student("王五",15);
        Student s4 = new Student("赵六",14);
        Student s5 = new Student("钱二",14);

        arrayList.add(s1);
        arrayList.add(s2);
        arrayList.add(s1);
        arrayList.add(s3);
        arrayList.add(s4);
        arrayList.add(s5);

        System.out.println(arrayList.size());//6

        //删除
        arrayList0.add(s1);
        arrayList.removeAll(arrayList0);//后面前移
        System.out.println(arrayList.size());//4

            /*
            我们知道,通过new 一个对象不能删除,
            因为原码用o.equals(elementData[index]),
            而Object的equals()比较的是Hush值
            现在,我就是想实现通过new 一个对象
            名字年龄一样就是同一个人
            来删除这个人,怎么做?
            在Student类里重写这个方法
            */

        arrayList.remove(
                new Student("赵六",14)
        );

        System.out.println(arrayList.size());//3

        //遍历[重点]
        //fori,foreach

        Iterator it = arrayList.iterator();
        ListIterator lit = arrayList.listIterator();

        //判断
        System.out.println(arrayList
                .contains(
                        new Student("钱二", 14)
                )
        );
        //这里用的也是equals(),so,true

    }
}
```

突然产生的奇奇怪怪的想法:

ArrayList()和ArrayList<>()有什么区别,泛型

ArrayList<>();里加了参数是怎么样的-->就是复制一份

```java
package CollectionLearning;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

//List接口的使用
public class Demo02 {
    public static void main(String[] args) {
       
        ArrayList arrayList = new ArrayList();


        //添加
        Student s1 = new Student("张三",15);
        Student s2 = new Student("李四",16);
        Student s3 = new Student("王五",15);
        Student s4 = new Student("赵六",14);
        Student s5 = new Student("钱二",14);

        arrayList.add(s1);
        arrayList.add(s2);
        arrayList.add(s1);
        arrayList.add(s3);
        arrayList.add(s4);
        arrayList.add(s5);
        arrayList.add("nihao");

        System.out.println(arrayList);//6
        ArrayList arrayList0 = new ArrayList<>(arrayList);
        System.out.println(arrayList0);
    }
}
```

## 源码分析:

### 属性

```java
private static final int DEFAULT_CAPACITY = 10;//默认容量大小
transient Object[] elementData; //存放元素的数组
private int size;//实际的元素个数<=容量
```

### 构造方法

```java
public ArrayList() {
    this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
}//由此可以看出如果没向集合中加元素,那它的容量是0
```

### add()方法

```java
public boolean add(E e) {
    ensureCapacityInternal(size + 1);  // 增长修改个数
    //↑确保  内部    容量
    
    elementData[size++] = e;
    return true;
}

private static int calculateCapacity(Object[] elementData, int minCapacity) {
    if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
        return Math.max(DEFAULT_CAPACITY, minCapacity);
    }
    return minCapacity;
}

private void ensureCapacityInternal(int minCapacity) {
	ensureExplicitCapacity(calculateCapacity(elementData, minCapacity));
}

private void ensureExplicitCapacity(int minCapacity) {
	modCount++;

	// overflow-conscious code
    if (minCapacity - elementData.length > 0)
        grow(minCapacity);//数组扩容
}

//数组扩容
private void grow(int minCapacity) {
    // overflow-conscious code
    int oldCapacity = elementData.length;
    int newCapacity = oldCapacity + (oldCapacity >> 1);//>>1 右移一位
    if (newCapacity - minCapacity < 0)
        newCapacity = minCapacity;
    if (newCapacity - MAX_ARRAY_SIZE > 0)
        newCapacity = hugeCapacity(minCapacity);//每次扩容都是原来的1.5倍,右移一位就是除以二
    // minCapacity is usually close to size, so this is a win:
    elementData = Arrays.copyOf(elementData, newCapacity);
}
```

