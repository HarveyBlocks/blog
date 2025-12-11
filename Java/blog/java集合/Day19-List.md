# List接口

- 有序(添加顺序和遍历顺序一致)
- 有下标
- 元素可以重复

## 实例化

```java
package CollectionLearning;

import java.util.ArrayList;
import java.util.List;

//List接口的使用
public class Demo02 {
    public static void main(String[] args) {
        //创建集合对象
        List list = new ArrayList();
    }
}
```

## 方法

还是没有直接用于"替换"的方法ListIterator.set(E e)

### 增加元素

| 返回值类型 | 函数名及参数                   | 描述                                    |
| ---------- | ------------------------------ | --------------------------------------- |
| void       | add(int index,object o)        | 在index位置插入对象                     |
| boolean    | addAll(int index,Collection c) | 将集合中的元素添加到此集合中的index位置 |

```java
list.add("01");
list.add("02");
list.add("01");
list.add("03");
list.add("04");
list.add("05");
list.add("06");
System.out.println(list.size());//子接口继承父接口方法
```

### 获取元素,s索引与切片

| 返回值类型 | 函数名及参数                       | 描述                                 |
| ---------- | ---------------------------------- | ------------------------------------ |
| Object     | get(int index)                     | 返回index位置的元素                  |
| int        | indexOf(Object o)                  | 返回列表中第一次出现的指定元素的索引 |
| List       | subList(int fromIndex,int toIndex) | List的切片                           |

```java
System.out.println(list.subList(1,3));
//[02, 01]
```

### 删除

| 返回值类型 | 函数名及参数      | 描述                       |
| ---------- | ----------------- | -------------------------- |
| boolean    | remove(Object o)  | 依左往右元素删除一个该元素 |
| boolean    | remove(int index) | 依脚标删除                 |

```java
list.remove("01");
System.out.println(list);
list.remove(0);
System.out.println(list);
```
输出结果:
```java
/*
[02, 01, 03, 04, 05, 06]
[01, 03, 04, 05, 06]
*/
```
突发奇想:remove删除了元素之后,该位置是null了,还是会被后面的元素顶替?

```java
package CollectionLearning;
import java.util.*;
public class Demo02 {
    public static void main(String[] args) {
        List list = new ArrayList();
        list.add("01");
        list.add("02");
        list.add("03");
        list.add("04");

        while (! list.isEmpty()){
            System.out.println(list.toString());
            System.out.println(list.get(0));
            list.remove(0);
        }

        //看来是顶替了

    }
}
```

输出结果:

```java
/*
[01, 02, 03, 04]
01
[02, 03, 04]
02
[03, 04]
03
[04]
04
*/
```

### 遍历

foreach,fori支持!

List的ListInterator 功能更强大

方法::

| 返回值类型    | 函数名及参数             | 描述                      |
| ------------- | ------------------------ | ------------------------- |
| ListInterator | ListInterator()          | 按一定顺序遍历            |
| ListInterator | ListInterator(int Index) | 从index按一定顺序开始遍历 |

ListIterator的方法(除其继承于Interator的方法)

| 返回值类型 | 函数名及参数    | 描述                                           |
| ---------- | --------------- | ---------------------------------------------- |
| void       | add(E e)        | 在遍历过程中将元素插入列表                     |
| int        | nextIndex()     | 下一个元素的索引                               |
| int        | previousIndex() | 逆序遍历列表时的nextIndex()                    |
| boolean    | hasPrevious()   | 逆序遍历列表时的hasNext()                      |
| E          | previous()      | 逆序遍历列表时的next()                         |
| void       | remove()        | 移除有next或previous返回的最后一个元素         |
| coid       | set(E e)        | 用指定元素替换next或previous返回的最后一个元素 |

**注意!**如果要逆序遍历一个list就先指针移到最后(后方),可以用ListInterator(int Index)改变指针位置或先正序遍历一遍

```java
package CollectionLearning;

import java.util.*;

//List接口的使用
public class Demo02 {
    public static void main(String[] args) {
        //创建集合对象
        List list = new ArrayList();
        //添加元素
        list.add("01");
        list.add("02");
        list.add("01");
        list.add("03");
        list.add("04");
        list.add("05");
        list.add("06");
        //使用迭代器
        ListIterator lit = list.listIterator();
        while (lit.hasNext()){
            System.out.print(lit.next()+",");
        }
        System.out.println();

        //使用列表迭代器,允许顺序,逆序遍历,增减元素
        while (lit.hasPrevious()) {
            System.out.print(lit.previousIndex()+":");
            System.out.print(lit.previous()+",");
            it.set("00");
        }
        System.out.println();

        //重置lit指针
        lit = list.listIterator();
        //虽然在这里鸟用没有

        while (lit.hasNext()){
            System.out.print(lit.next()+",");
            lit.remove();
        }
        System.out.println();
        System.out.println(list.size());

    }
}
```

输出结果:

```java
/*
01,02,01,03,04,05,06,
6:06,5:05,4:04,3:03,2:01,1:02,0:01,
00,00,00,00,00,00,00,
0
*/
```

## 判断

同collection

## 数字元素列表

添加数字元素, 其实省略了**装箱**这一步

```java
package CollectionLearning;

import java.util.*;

//List接口的使用
public class Demo02 {
    public static void main(String[] args) {
        //创建集合对象
        List list = new ArrayList();
        //添加数字元素,
        // 其实省略了装箱这一步
        list.add(1);//0
        list.add(2);//1
        list.add(1);//2
        list.add(3);//3
        list.add(4);//4
        list.add(5);//5
        list.add(6);//6
        list.remove(2);//想删除元素2
        System.out.println(list);
        //[1, 2, 3, 4, 5, 6]
        //结果时删了2索引上的元素,怎么办呢?
        //-->手动装箱
        list.remove((Object) 2);
        //(Integer) 2
        // new Integer(2)皆可
        System.out.println(list);
        //[1, 3, 4, 5, 6]
    }

}
```

