# 接口

Java没有多继承，但要用，怎么办呢？接口可以多继承！

- 普通类：只有具体实现
- 抽象类：具体实现和规范（抽象方法）都有
- 接口：只有规范，自己无法写方法。实现约束和现实分离：面向接口编程



- 接口就是规范，定义的是一组规则，体现了“如果你是·······则必须能······”的思想。如果你是天使，则必须能飞；如果你是汽车，则必须能跑；如果你是好人，则必须干掉坏人；如果你是坏人，就必须欺负好人。
- **接口的本质是契约**，如法律，制定好后大家都要遵守
- 对抽象的抽象，最能体现这一点的就是接口。为什么我们讨论设计模式都只针对具备抽象能力的语言（c++，java，c#等），就是因为设计模式所研究的，实际上就是理解如何去抽象

## interface

> 声明类的关键字是class，声明接口的关键字是interface

```java
//学霸接口
//接口一般都需要一个实现类，一般命名为：接口名+Impl
public interface GradeAStudent {
    //接口中的所有定义都是抽象的，public
    public abstract void runAway();//public abstract是默认的，不用写
   void involute(int Day);

}

```



``` java
//用{GradeAStudentImpl}类 {implements} 实现 {GradeAStudent} 接口
//快捷键：Alt+Insert->实现方法....    
//实现接口的类，就一定要重写接口的方法    
public class GradeAStudentImpl implements GradeAStudent{
    @Override
    public void runAway() {
        System.out.println("runAway();");
    }

    @Override
    public void involute(int day) {
        System.out.println( "involute("+day+");" );
    }
}

```



理由接口侧面实现多继承

``` java
public interface GradeAStudent {
    public abstract void runAway();//public abstract是默认的，不用写
   void involute(int Day);
}
```

```java
public interface GradeBStudent {
    void sleep(int Day);
}
```

``` java
public class GradeAStudentImpl implements GradeAStudent,GradeBStudent{//多个接口
    @Override
    public void sleep(int day) {
        System.out.println( "involute("+day+");" );//多项继承
    }

    @Override
    public void runAway() {
        System.out.println("runAway();");
    }

    @Override
    public void involute(int day) {
        System.out.println( "involute("+day+");" );
    }
}
```

从接口写属性

``` java
//学霸接口
public interface GradeAStudent {
    //接口中的所有定义的属性都是常量
    public static final int FULLSCORE=750;//public static final是默认的，不用写
    double PI = 3.1415926535;
            
    //接口中的所有定义的方法都是抽象的
    public abstract void runAway();//public abstract是默认的，不用写
   void involute(int Day);

}
```



实现接口和继承父类同时实现

``` java
 class GradeAStudentImpl exends Person implements GradeAStudent,GradeBStudent{
     //要先有亲爸再有干爹
 }
     
```

### 多继承的注意

- 一个接口继承多个接口,如果接口方法重名,则不支持多继承
- 一个类实现多个接口,如果接口方法重名,则不支持多实现
- 一个类继承父类,又实现接口,父类和接口方法重名,实现类优先使用父类的
- 一个类实现多个接口,如果接口默认(default)方法重名,可以不冲突,这个类重写该方法即可



## JDK8之后接口的新增方法

### 默认方法

- 必须使用default修饰,否则会被默认的public修饰

```java
public interface A {

    /*
    1.默认方法:必须使用default
    实例方法,对象的方法.必须使用实现类的对象来访问
    */
    default void test1(){

    }
    
}
```

