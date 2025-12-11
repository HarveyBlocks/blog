# 抽象类

## 声明一个抽象类

``` java
public abstract class Student extends Person{
    public Student() {
    }
}
```





## 抽象类的性质



``` java
//抽象类的所有方法必须要有子类实现
//    除非子类也是抽象类
//Java没有多继承，但要用，怎么办呢？接口可以多继承！
public abstract class Student extends Person{
    public Student() {
    }

    public abstract void doSomething();//约束，即大框架。该方法还没有实现。
    public static  void sleep(){
        System.out.println("He is falling asleep at class!");
    }
    
}
//抽象类不能new，只能由子类去实现它,只能去new它的子类对象
//抽象类里能够有正常的方法（不能new怎么调用呢？？？？？？？？？）
//但是，一个类里如果有抽象方法，那么这个类一定要是抽象类！
```

``` java
//学霸类
public class GradeAStudent extends Student{
    //必须要重写doSomething方法，否则报错。
    @Override//重写方法
    public void doSomething() {
        System.out.println("He is doing his schoolwork at every time!");
    }
}

```

## abstract不能用来修饰的

### 私有方法
- 私有方法只能在本类中被访问
- 子类虽然会继承到此方法,但是访问不到,就是不能被重写(其实就是隐式的final修饰的方法)

### 静态方法
-  静态方法表示可以直接用类名调用此方法
-  abstract修饰的方法没有方法体,所属类类必定是一个抽象类,不能实例化,必须让子类去重写,创建对象才能调用
-  如果static和abstract同时修饰一个方法,就表示可以**类名直接调用这个没有方法体的抽象方法**,这样是错误的

### final修饰的类,方法

-  final修饰的类表示不能被继承,
-  修饰的方法表示不能被重写

