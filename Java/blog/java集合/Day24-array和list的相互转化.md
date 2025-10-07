# Array与LIst的转换



先创建集合并添加元素

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





    }
}
```

## list转array

```java
    //list转成数组
    //Integer[] array1 = (Integer[]) list.toArray();//.ClassCastException类强制转换异常
    Integer[] array2 = list.toArray(new Integer[0]);//长度(这里是0)若小于list则补全,若大于则填充null
    System.out.println(array2.toString());//HashCode
```
## array转list

```java
    //array转成list(不会自动装箱)
    List<Integer> list2 = Arrays.asList(new Integer[]{1,2,3});//后面一个不能是int
    List<int[]> list3 = Arrays.asList(new int[]{1, 2, 3,4,5,6,7,8});//要么这样
    //但是
    Iterator it = list3.listIterator();
    while(it.hasNext()){
        System.out.println(it.next());
    }
    //肯定不符合要求,不要这么去写

    //从array转来的list是受限集合,不能添加和删除,因为数组的长度是固定的

    //否则:.ClassCastException
    list2.add(2);//.ClassCastException
    list2.remove(13);//.ClassCastException
```