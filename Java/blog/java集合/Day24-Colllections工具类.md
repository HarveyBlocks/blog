# Colletions 工具类

- 集合工具类,定义了一组除了存取外的一系列集合常用方法

## 方法

| 返回值类型 | 方法名及参数 | 描述 |
| ---------- | ------------ | ---- |
|static <T> void | copy(List<?  super T> dest, List<? extends T> src)|复制|
|static <T extends Comparable<? super  T>> void | sort(List<T> list)|排序,默认升序|
|static <T> int|binarySearch(List<?  extends Comparable<? super T>> list,  T key)|二叉查找,需要列表为升序|
|static void|reverse(List<?> list)|反转列表顺序|
|static void|shuffle(List<?> list)|打乱元素顺序|
|static void|shuffle(List<?> list, Random rnd) |打乱元素顺序|

```java
package LearnCollections;

import java.util.*;

/**
 * @author HarveyBlocks
 * @date 2023/09/01 11:38
 **/
public class Demo01 {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(27);//自动装箱
        list.add(42);
        list.add(37);
        list.add(43);
        list.add(36);
        list.add(31);
        list.add(14);
        list.add(32);
        list.add(13);
        System.out.println(list);
        //sort
        Collections.sort(list);//默认升序
        System.out.println(list);

        //binarySeache()
        //二分查找一定要升序
        int num = Collections.binarySearch(list,33);//自动拆箱,13是自动装箱
        System.out.println(num);//没找到,-6,意思是在(4,5)(这是索引)
        num = Collections.binarySearch(list,36);
        System.out.println(num);//找到了,5


        //reverse()
        Collections.reverse(list);//逆序了
        System.out.println(list);

        //copy()
        List<Integer> newList = new ArrayList<>();
        /*
        Collections.copy(newList , list);
        System.out.println(newList);
        qwq报错.IndexOutOfBoundsException要求俩集合的大小相同
        */
        for (int i = 0; i < list.size(); i++) {newList.add(0);}
        Collections.copy(newList , list);
        System.out.println(newList);
        /*
        qwq
        都这样了,为啥不这么写呢:
        for (int i = 0; i < list.size(); i++) {newList.add(list.get(i));}
        */

        //shuffle()打乱,洗牌
        Collections.shuffle(list);
        System.out.println(list);

    }
}
```
