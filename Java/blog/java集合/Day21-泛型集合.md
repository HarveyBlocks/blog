# 泛型集合

在集合里添加元素时,我们发现可以在一个集合中添加**任意类型**的元素

其实际上,传进去的类型都变成了**Object**

那么,当要去读取集合里的数据时,返回的也就是**Object**了

想要原来的类型,需要**强制转换**,就有判断原来的类型,很容易发生错误



```java
package CollectionLearning;

import java.util.ArrayList;

/**
 * @author HarveyBlocks
 * @date 2023/08/28 11:02
 **/
public class Demo03 {
    public static void main(String[] args) {
        ArrayList arrayList = new ArrayList();
        arrayList.add("111");
        arrayList.add("222");
        arrayList.add("333");
        arrayList.add(444);
        arrayList.add(555);

        for (Object object:
             arrayList) {
            String str = (String) object;
            System.out.print(str + ",");
        }
    }
}
```

输出结果:

```java
D:\IT_study\JDK\bin\java.exe "-javaagent:D:\IT_study\IntelliJ IDEA 2023.2\lib\idea_rt.jar=51154:D:\IT_study\IntelliJ IDEA 2023.2\bin" -Dfile.encoding=UTF-8 -classpath 
```

报错惹



## 概念

参数化类型,类型安全的集合,**强制集合元素类型必须一致**

## 特点

- 编译时即可检查,而非运行时抛出异常
- 访问时不必类型转换(拆箱)
- 不同泛型之间引用不能相互赋值,泛型不存在多态

## 用泛型解决开头的问题

```java
public class Demo03 {
    public static void main(String[] args) {
        ArrayList<String> arrayList = new ArrayList();
       									//或ArrayList<>()或ArrayList<String>()
        arrayList.add("111");
        arrayList.add("222");
        arrayList.add("333");
        arrayList.add(444);//直接报错
        arrayList.add(555);//一下子就检查出来了

    }
}
```

```java
for (String str://遍历的时候直接就可以是String了,避免了强制类型转换
     arrayList) {
    System.out.print(str + ",");
}
```



## 示例:ArrayList<Student>

```java
System.out.println("\n=========ArrayList<Student>=========");

ArrayList<Student> als = new ArrayList<>();

//实例化Student
Student s1 = new Student("张三",12,96);
Student s2 = new Student("李四",12,92);
Student s3 = new Student("王五",11,94);
Student s4 = new Student("赵六",12,99);
Student s5 = new Student("钱七",13,96);

//将实例化的Student对象装进ars里
als.add(s1);
als.add(s2);
als.add(s3);
als.add(s4);
als.add(s5);

Iterator<Student> its = als.iterator();//在迭代器里也声明一下泛型即可
while (its.hasNext()) {
    System.out.println(its.next());
}
```



### 泛型不同,不能相互赋值

```java
als = arrayList;//报错,泛型不同,不能相互赋值
```

