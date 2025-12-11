# String类


```java
private final char value[];
```


## String 类概述

字符串是常量，创建之后不得改变

字符串字面值存储在字符串池中，可以共享

​	字符串池在方法区里（栈存基本类型；堆，存对象；方法区，存......）

​	目的是为了共享

### 为什么说字符串不能改变

```java
public class Main {
    public static void main(String[] args) {
        
        String str1 = "hello";
        //栈里的name->方法区字符串池里的"hello"，name里包含了"hello"的地址
        
        str1 = "hi";
        
        /*赋值过程是：
        在方法区字符串池重新创建了一个"hi"
        再把name里的地址改成了"hi"的地址
        于是name->"hi"
        "hello"如果没人用，就变成一个垃圾了
        触发了垃圾回收器finalize()，就回收了"hello"
        */

        String str2 = "hi";
        //如果再指向"hi"就不需要再开辟一片空间，反而节省了空间，也实现了共享
    }
}
```

### 但用new就不一样了

```java
String str1 = new String("hello");
```

它会在堆和池（字符串池之外？？？）中各存一个

str1的地址指向堆里的那个，str1存的是堆里的对象的地址

程序运行的时候，堆和池里的两个会合并，堆里没有这个"hello"，只有一个指向池里的"hello"的一个地址

这种方法可能浪费空间

以下是堆上述话的验证

```java
public class Main {
    public static void main(String[] args) {
        String str1 = new String("hello");
        String str2 = new String("hello");
        System.out.println(str1 == str2);//false
        System.out.println(str1.equals(str2));//true
        str1 = "hi";
        str2 = "hi";
        System.out.println(str1 == str2);//true
    }
}
```

#### 原因:来看看String.equals()的源码

```java
public boolean equals(Object anObject) {
    if (this == anObject) {
        return true;
    }
    if (anObject instanceof String) {//String没有子类，所以只有anObject是String才为true
        String anotherString = (String)anObject;
        int n = value.length;
        if (n == anotherString.value.length) {
            char v1[] = value;
            char v2[] = anotherString.value;
            int i = 0;
            while (n-- != 0) {//逐个比较
                if (v1[i] != v2[i])
                    return false;
                i++;
            }
            return true;
        }
    }
    return false;
}
```

