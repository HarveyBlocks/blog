# 包装类

八个和基本类型存在栈里

包装类的默认值是null

Object可统一所有数据，包装类默认值是null

| 基本数据类型 | 包装类型  |
| ------------ | --------- |
| byte         | Byte      |
| short        | Short     |
| int          | Interger  |
| long         | Long      |
| float        | Float     |
| double       | Double    |
| boolean      | Boolean   |
| char         | Character |

包装类就有了属性和方法

## 类型转换

基本类型和包装类型的转换

基本类型存于栈，包装类型存于栈

```JAVA
public class Main {
    public static void main(String[] args) {
        //装箱法一
        int num1 = 18;
        Integer integer1_1 = new Integer(num1);
        System.out.println("interger1_1="+integer1_1);
        //装箱法二
        Integer integer1_2 = Integer.valueOf(num1);
        System.out.println("interger1_2="+integer1_2);
        //拆箱
        Integer integer2_1 = new Integer(100);
        int num2 = integer2_1.intValue();
        System.out.println("num2="+num2);
        //JDK1.5之后：自动装箱拆箱的功能
        //自动装箱
        int num3 = 12;
        Integer integer3_1=num3;
        System.out.println("interger3_1="+integer3_1);
        //自动拆箱
        int num4 = integer3_1;
        System.out.println("num4="+num4);

    }

}
```



## 装箱

基本类型转换为对象（从栈到堆）

看包装类的构造方法，如：

```java
public Integer(int value) {
    this.value = value;
}
```

或Number的方法提供装箱的方法 valueOf()

```java
public static Integer valueOf(int i) {
    if (i >= IntegerCache.low && i <= IntegerCache.high)
        return IntegerCache.cache[i + (-IntegerCache.low)];
    return new Integer(i);
}
```

## 拆箱

引用类型转化为基本数据类型（从堆到栈）

Integer的方法提供拆箱的方法 intvalue()

## 自动装箱拆箱

把编译后的class文件转回java文件后可以发现，其本质依然是原本朴素的装箱拆箱法