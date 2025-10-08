# Set接口

## 特点

- 无序
- 无下标
- 元素不可重复

## 方法

没有定义自己的方法,方法全是继承自collection的

## Set实际使用

### 创建Set集合

```java
package CollectionLearning;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 测试Set接口的使用
 * 无序
 * 无下标
 * 不能重复
 * @author HarveyBlocks
 * @date 2023/08/29 10:44
 **/
public class Demo04 {
    public static void main(String[] args) {
        //创建集合
        Set<String> set = new HashSet<>();
    }
}
```



### 添加元素

```java
//添加数据

System.out.println(set.add("010"));//true
System.out.println(set.add("020"));//true
System.out.println(set.add("030"));//true
System.out.println(set.size());//3
System.out.println(set.add("010"));//false
System.out.println(set.size());//3

String str = "040";
System.out.println(set.add(str));//true
System.out.println(set.size());//4
System.out.println(set.add(str));//false
System.out.println(set.size());//4
System.out.println(set.toString());//[040, 030, 020, 010]


/*
HushSet实现的add()源代码
public boolean add(E e) {
    return map.put(e, PRESENT)==null;
}
*/
```



### 删除元素



```java
//删除

set.remove("010");
System.out.println(set.toString());//[040, 030, 020]
```



### 遍历

```java
//遍历,继承collection的Interator

Iterator it = set.iterator();

while (it.hasNext()) {
    System.out.print(it.next()+",");
}
System.out.println();
//040,030,020,

//当然,没有下标,就和Collection一样能foreach不能fori
```



### 判断

```java
System.out.println(set.contains("020"));//true
System.out.println(set.isEmpty());//false
```
