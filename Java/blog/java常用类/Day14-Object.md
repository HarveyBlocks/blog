# Object

- Object类直接或间接地是所有类的父类

- 其被成为”基类“或”超类“

- Object类中所定义的方法是所有对象所具备的方法

- Object类可以存储任何对象
  - 作为参数们，可以接受任何对象
  - 作为返回值，可以返回任何对象

## getClass()方法

```
public final native Class<?> getClass();
```

Student类

```java
/**
 * @author HarveyBlocks
 * @date 2023/08/15 14:43
 **/
public class Student {
    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Student() {

    }

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

Main类

```java
public class Main {
    public static void main(String[] args) {
        Student student1=new Student("Mike",16);
        Student student2=new Student("Amy",15);
        //判断student1和sudent2是不是同一个类型
        Class student1Class= student1.getClass();//返回一个class类型
        System.out.println(student1Class);
        Class student2Class= student2.getClass();
        System.out.println(student2Class);
        System.out.println("*=========================");
        System.out.println(student1Class == student2Class);
    }
}
```

输出结果：

class Student
class Student
*=========================
true

### 有关Class的几个方法

```java
package Generic;
public class Demo {
    public static void main(String[] args) {
        Demo demo = new Demo();

        System.out.println(demo.getClass());//class Generic.Demo
        System.out.println(demo.getClass().getName());//Generic.Demo
        System.out.println(demo.getClass().getSimpleName());Demo

    }
}
```

## hashCode()方法

```java
public native int hashCode();//十进制
```

返回该对象的hash码值

hash值根据对象的**地址**或**字符串**或**数字**使用hush算法计算出来的int值类型

```java
public class Main {
    public static void main(String[] args) {
        Student student1=new Student("Mike",16);
        Student student2=new Student("Amy",15);
        System.out.println(student1.hashCode());
        System.out.println("*=========================");
        System.out.println(student2.hashCode());
        Student student3=student2;
        System.out.println(student3.hashCode());
    }
}
```

输出结果：

460141958
*=========================
1163157884
1163157884

## toString()方法

```java
public String toString() {
    return getClass().getName() 
        + "@"
        + Integer.toHexString(hashCode());//十六进制
}
```

```java
public class Main {
    public static void main(String[] args) {
        Student student1=new Student("Mike",16);
        Student student2=new Student("Amy",15);
        System.out.println(student1.toString());
        System.out.println(student2.toString());
    }
}
```

输出结果：

Student@16f65612
Student@311d617d

ps.类如果有包，会连带包名称输出,例如：

com.Student@16f65612

- 常常会用子类方法去重写toString()方法

### toString()方法重写

```java
/**
 * @author HarveyBlocks
 * @date 2023/08/15 14:43
 **/
public class Student {
    private String name;
    private int age;
/*
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
*/
    public Student() {

    }

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }
//重写这个方法
    @Override
    public String toString() {
        return "name="+name
                +"\tage="+age;
    }
}
```

Main不变

输出结果：

name=Mike	age=16
name=Amy	age=15

## equals()方法

比较对象地址是否相等

```java
public boolean equals(Object obj) {
    return (this == obj);
}
```

### equal()方法重写

- 比较两个对象是否指向同一个对象
- 判断obj是否为null
- 判断两个引用指向的实际对象类型是否一致
- 强制类型转换
- 依次比较各个属性值是否相等

```java
@Override
public boolean equals(Object obj) {
    //比较两个对象是否指向同一个对象
    if(this==obj){ return true; };
    //判断obj是否为null
    if(obj==null){return false;}
    //判断两个引用指向的实际对象类型是否一致

    /*if (obj.getClass()==this.getClass()){
        return false;
    } else if () {

    }
    */

    if(obj instanceof Student){
        //强制类型转换
        Student studentObj=(Student) obj;
        //依次比较各个属性值是否相等
        if(this.name.equals(studentObj.getName())//字符串比较用str1.equal(str2)
                && this.age==studentObj.age){
            return true;
        }
    }        
    return false;
}
```

## finalize()方法

一般是程序自行调用，删除的没有真正用途的类的内存

