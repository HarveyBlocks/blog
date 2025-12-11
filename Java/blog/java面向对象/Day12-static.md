# static

静态的static隶属于类层级,每个类共有一个,静态成员被所有对象共享

非静态的隶属于对象层级,每个对象有一个

static是和类一起加载的

非静态对象创建之后（=类实例化之后=new了之后）才存在

在非静态的成员中既能访问静态的成员,也能访问非静态的成员

(成员 = 属性 + 方法)

static的成员被所有对象共享,推荐使用     

**类名.**

的方式引用  

在静态的方法中只能访问静态的成员,但不能访问非静态的成员

(成员 = 属性 + 方法)

不能滥用static

## 对属性的static

静态属性一定要赋初值

```java
public void say(){
    Student.grade = 9;

    //System.out.println(Student.age);
    System.out.println(Student.grade);//9

    Student student = new Student();

    System.out.println(student.age);//0
    System.out.println(student.grade);//9

    grade = 10;
    System.out.println(Student.grade);//10
    System.out.println(student.grade);//10

    student.grade = 11;
    System.out.println(Student.grade);//11
    System.out.println(student.grade);//11

    this.grade = 12;
    System.out.println(Student.grade);//12
    System.out.println(student.grade);//12

    System.out.println(age);  //0
    System.out.println(grade);//12

}
```

**注意区分static修饰的是属性，不是变量。区别属性和变量**

## 对方法的static

``` java
/**
 * @author HarveyBlocks
 * @date 2023/08/09 18:56
 **/
public class Student extends Person{
    private static void age(){

    }
    private void score(){

    }
    public void said(){
        Student.age();
        Student.score();//报错

        Student student=new Student();

        student.age();
        student.score();//不报错

    }
    public static void run(){
        age();
        Student.age();
        score();//报错
        Student.score();//报错

        Student student=new Student();

        student.age();
        student.score();//不报错

    }
}

```

static方法中不可出现this关键字

static 方法属于类，非静态方法属于实例

所以static的方法里不能引用非静态的方法

## 静态导入包

因为Math的构造器是私有的,方法全是public

``` java
public class Main {
    public static void main(String[] args) {
        System.out.println(Math.random());

    }
}
```

``` java
import java.lang.Math;
public class Main {
    public static void main(String[] args) {
        System.out.println(Math.random());

    }
}
```

静态导入包：

``` java
import java.lang.Math.random;//报错
import static java.lang.Math.random;//不报错

public class Main {
    public static void main(String[] args) {
        System.out.println(random());//简化了写法

    }
}

```

## static的应用:单例设计模式

设计模式:

- 一个问题的最优解
- 设计模式有23种,对应23种软件开发中会遇到的问题

单例设计模式:

- 确保一个类只有一个对象
- 因为只需要一次,再多浪费内存

### 单例模式的例子:

任务管理器

任务管理器的窗口是一个对象

因为一台电脑只需要一个任务管理器就可以管理它了

### 饿汉式单例模式

- 拿到对象时,对象就创建好了

  写法:

  - 把类的构造器私有
  - 定义一个static类变量记住类的一个对象
  - 定义一个static类方法,返回对象

![image-20230901213045997](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java面向对象/Day12-static/image-20230901213045997.png)

![image-20230901212223408](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java面向对象/Day12-static/image-20230901212223408.png)

因为java程序在运行的时候只需要一套运行环境,所以Runtime被设计成单例模式

### 懒汉式单例

- 拿对象时才开始创建对象

- 写法

  - 把类的构造器私有
  - 定义一个类变量用于存储对象,不要实例化
  - 提供一个类方法,保证第一次使用创建对象,后面调用返回的是同一个对象

![image-20230901214453733](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java面向对象/Day12-static/image-20230901214453733.png)

#### 什么时候饿汉,什么时候懒汉?

类用的不频繁懒汉式

类用的频繁就用饿汉式

