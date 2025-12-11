# TreeMap()

- 实现SortedMap接口
- 可以对key自动排序
- 存储结构是红黑树(特殊的二分查找树)





```java
//创建Map集合
TreeMap<Student, String> treeMap = new TreeMap<>();//注意<>里的,

//添加元素
Student s1 = new Student("A",11,90);
Student s2 = new Student("B",12,95);
Student s3 = new Student("C",11,93);
Student s4 = new Student("D",10,94);
Student s5 = new Student("E",13,97);
Student s6 = new Student("F",14,91);
Student s7 = new Student("G",11,91);

treeMap.put(s1, "China");//注意put()而不是add()
treeMap.put(s2, "UK");
treeMap.put(s3, "Japan");
treeMap.put(s4, "USA");
treeMap.put(s5, "China");//value可重复
treeMap.put(s6, "Canada");
treeMap.put(s7, "Japan");

System.out.println(treeMap.size());//7
```

运行报错ClassCastException类型转换异常

```java
//源码注释:
/**
*抛出:
*ClassCastException – if the specified key cannot be compared with the keys currently in the map
*NullPointerException – if the specified key is null and this map uses natural ordering, or its comparator does not permit null keys
*/
```



Tree!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

想到TreeSet也是报了这个错

原来是没有实现**Comparable**类的compareTo()

再implements的时候千万别忘了Comparable\<Student\>千万要声明T的类

其他一样

