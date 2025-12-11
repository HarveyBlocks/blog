# Map父接口

## 创建Map对象

```java
package LearnMap;

import LearnCollection.Student;

import java.util.HashMap;
import java.util.Iterator;//有用,非必须
import java.util.Map;
import java.util.Set;//有用,非必须

public class Demo01 {
    public static void main(String[] args) {
        //创建Map集合
        Map<Student, String> map = new HashMap<>();//注意<>里的逗号
    }
}
```

## 方法

| 返回值类型    | 方法名及参数         | 描述                                          |
| ------------- | -------------------- | --------------------------------------------- |
|int |hashCode( )| 返回map的哈希值 |
|int |size( )| 返回map的大小 |

### 添加键值对与替换值

| 返回值类型    | 方法名及参数         | 描述                                          |
| ------------- | -------------------- | --------------------------------------------- |
| V             | put(K key ,V value)  | 将对象存入到集合中,关联键值.key重复则覆盖原值(替代了replase的作用) |

```java
//添加元素
Student s1 = new Student("A",11,90);
Student s2 = new Student("B",12,95);
Student s3 = new Student("C",11,93);
Student s4 = new Student("D",10,94);
Student s5 = new Student("E",13,97);
Student s6 = new Student("F",14,91);
Student s7 = new Student("G",11,91);

map.put(s1, "China");//注意put()而不是add()
map.put(s2, "UK");
map.put(s3, "Japan");
map.put(s4, "USA");
map.put(s5, "China");//value可重复
map.put(s6, "Canada");
map.put(s7, "Japan");

System.out.println(map.size());//7
System.out.println(map.toString());
/*{
 Student{name='F', age=14, score=91}=Canada,
 Student{name='B', age=12, score=95}=UK,
 Student{name='C', age=11, score=93}=Japan,
 Student{name='D', age=10, score=94}=USA,
 Student{name='G', age=11, score=91}=Japan,
 Student{name='E', age=13, score=97}=China,
 Student{name='A', age=11, score=90}=China
}*/
//可见是无序的

//替换
map.put(s3, "USA");//key重复则替换
System.out.println(map);
```

因为hashMap()的数据结构是哈希树

加入新元素时判断key是否重复依据时**key 的类的equals()和hushCode()**

### 遍历与获取

| 返回值类型    | 方法名及参数         | 描述                                          |
| ------------- | -------------------- | --------------------------------------------- |
| V        | get(Object key)      | 根据键获取对应的值                            |
| Set<K>|keySet()| 返回一个Set接口,其中包含map中所有的key |
|Set<Map.Entry<K,V>>  |entrySet()|返回一个Set接口,其中包含map中所有的entry    |
| Collection<V> | values()             | 返回包含所有值的Collection集合(Collection集合元素可重复) |

法一:用keySet()获取key的集合,再遍历

```java
//遍历
System.out.println("-------------------keySet()--------------------");
Set<Student> keySet = map.keySet();//返回key的 Set()集合

System.out.println("==================foreach=================");
for (Student key:keySet) {
    System.out.println(key+":"+map.get(key));
}

System.out.println("==================Iterator=================");
Iterator it = keySet.iterator();
while (it.hasNext()) {
    Student key = (Student) it.next();
    System.out.println(key+":"+map.get(key));
}
```

法二:用entrySet()获取键值对的集合,再遍历

```java
System.out.println("-------------------entrySet()--------------------");
Set<Map.Entry<Student , String>> entrys = map.entrySet();
//entry就是一个键值对类型,Entry是一个Map之下的接口

System.out.println("================Entry-Iterator===================");
Iterator eit = entrys.iterator();
while (eit.hasNext()) {
    System.out.println(eit.next());
}

System.out.println("================Entry-foreach===================");
Student key;
String value;
for (Map.Entry<Student , String> entry:entrys) {
    key = entry.getKey();
    value = entry.getValue();
    System.out.println(key + ":" + value);
}
```

Q : 是keySet()遍历的效率高,还是entrySet()遍历的效率高

A : **entrySet()**.

Descrip : 使用keySet()遍历,先得到了一个存有key的Set集合
                          					然后再用key去一遍一遍地遍历map去获取value
          	   使用entrySet()遍历,先得到了一个存有Map.Entry()的Set集合,键与值已经匹配,
​													直接可以获取value

```java
Collection values = map.values();
for (Object value: values) {//这里返回的值是Object
    System.out.println(value);
}
```

### 判断

| 返回值类型    | 方法名及参数         | 描述                                          |
| ------------- | -------------------- | --------------------------------------------- |
|boolean |equals(Object o)| 判断map是否一致,非HashCode |
|boolean | containsKey(Object key)| 判断是否包含key |
|boolean |containsValue(Object value)| 判断 是否包含value|
|boolean |isEmpty()| 判断是否为空 |

```java
Map<Student, String> map = new HashMap<>();

Map<Student, String> map2 = new HashMap<>();
System.out.println(map.equals(map2));//true,看来是被重写过了
```

```java
System.out.println(map.containsKey(new Student("Z",12,91)));//false
System.out.println(map.containsValue("USA"));//true
System.out.println(map.isEmpty());//false
```

### 删除

| 返回值类型    | 方法名及参数         | 描述                                          |
| ------------- | -------------------- | --------------------------------------------- |
| void| clear() | 清空map|
|V |remove(Object key)|依据key删除元素,返回values |
|default boolean |remove(Object key, Object value)|依据key和value删除元素 |

