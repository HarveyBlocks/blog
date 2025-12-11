# TreeSet类

- 基于排序顺序实现元素不重复
- 实现了SortedSet接口,对集合元素自动排序
- 元素对象的类型必须实现Comparable接口,指定排序规则

- 存储结构是红黑树

## 红黑树

- 二叉树
- 二叉查找树:右子树比左子树小,中序遍历是升序
- 根节点是黑的
- nil节点是黑的

## 案例

简单类型见其他类

复杂类型:

```java
package CollectionLearning;

import java.util.TreeSet;

/**
 * @author HarveyBlocks
 * @date 2023/08/30 16:03
 **/
public class Demo05 {
    public static void main(String[] args) {
        //创建集合
        TreeSet<Student> treeSet = new TreeSet<Student>();
        //添加元素
        Student s1 = new Student("A",11,90);
        Student s2 = new Student("B",12,95);
        Student s3 = new Student("C",11,93);
        Student s4 = new Student("D",10,94);
        Student s5 = new Student("E",13,97);
        Student s6 = new Student("F",14,91);
        Student s7 = new Student("G",11,91);
        treeSet.add(s1);
        treeSet.add(s3);
        treeSet.add(s2);
        treeSet.add(s5);
        treeSet.add(s6);
        treeSet.add(s4);
        treeSet.add(s7);

        System.out.println(treeSet);
    }
}
```

输出结果:

```java
Exception in thread "main" java.lang.ClassCastException:
        CollectionLearning.Student cannot be cast to java.lang.Comparable
    at java.util.TreeMap.compare(TreeMap.java:1294)
    at java.util.TreeMap.put(TreeMap.java:538)
    at java.util.TreeSet.add(TreeSet.java:255)
    at CollectionLearning.Demo05.main(Demo05.java:21)
```

异常:**ClassCastException**

Student cannot be cast to java.lang.Comparable

Student 不能转成java的Comparable

**程序不知道比较的标准是什么**

***元素一定要实现Comparable***

### Comparable接口:

```java
public interface Comparable<T> {
    /**.........*/
    public int compareTo(T o);
}
```

只有一个抽象方法

我们需要重写这个方法,告诉他比较的标准是什么

```java
//想要先按分数比,再按年龄比,最后按姓名比
@Override
public int compareTo(Student o) {
    int nameDifference = name.compareTo(o.getName());
    //支持全角字符就离谱
    int ageDifference = age - o.getAge();
    int scoreDifference = score - o.getScore();
    
    //this. - o.get....() 升序
    //o.get....() -this.  降序
    
    return scoreDifference == 0?ageDifference == 0?nameDifference:ageDifference:scoreDifference;
    //够阴间不?
}
```

```java
/*
if (scoreDifference == 0){
    if (ageDifference == 0) {
        return nameDifference;
    } else {
        return ageDifference;
    }
} else {
    return scoreDifference;
}
*/

//↓

/*
return (scoreDifference == 0)? 
        (
        (ageDifference == 0)?
                nameDifference:ageDifference
        )
        :scoreDifference; 

*/ 
```
comparable接口实现之后,就已经代替了equals()的功能

例如remove(new ....)也可以将原来的删除了

因为TreeSet重写了remove()类

### Comparator接口

当使用也可以Comparator接口时

可以不继承comparable接口,不实现compareTo() 方法

如果实现了,**优先会使用Comparator接口**

```java
package LearnCollection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * TreeSet集合的使用
 * Comparator:实现定制比较(比较器)
 * Comparable:可比较的,元素的比较规则
 * @author HarveyBlocks
 * &#064;date  2023/08/30 16:03
 **/
public class Demo05 {
    public static void main(String[] args) {
        //创建集合
        TreeSet<Student> treeSet = new TreeSet<>(new Comparator<Student>() {//匿名内部类
            
            //自定义排序规则
            @Override
            public int compare(Student o1, Student o2) {//先年龄,再成绩,最后名字
                int nameDifference = o1.getName().compareTo(o2.getName());
                int ageDifference = o1.getAge() - o2.getAge();
                int scoreDifference = (o1.getScore() - o2.getScore());

                return ageDifference == 0 ?
                        scoreDifference == 0 ?
                                nameDifference :scoreDifference
                        :ageDifference;

            }
            
        });
        //添加元素
        {....}
		//遍历输出
        {....}

    }
}
```

### TreeSet与TreeMap的关系

同HashSet和HashMap的关系

