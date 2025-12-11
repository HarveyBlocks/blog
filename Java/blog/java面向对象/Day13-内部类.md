# 内部类

- 内部类就是在一个类的内部再定义了一个类。比如，A类中定义了一个B类，那么B类相对于A类来说就称为内部类，而A类相对于B类来说就是外部类了。

- 成员内部类
- 静态内部类
- 局部内部类
- 匿名内部类

## 成员内部类

### 内部类的创建
```java
class Outer {
    private int id;
    public void  out(){
        System.out.println("out");

    }
    public class Inner{//就算有了public,由于创建对象时要Outer.Inner,所以不是真public
        public void setId(int id) {
            //this.id = id;//报错,找不到this.id
        }

        public int getID(){
            System.out.println(id);
            return id;
        }
        public void in(){
            System.out.println("in");
        }
    }
}
```

### 实例化内部类

```java
class Outer {

        public static void main(String[] args) {
            Outer outer = new Outer();
            //通过外部类实例化内部类
            Outer.Inner inner=outer.new Inner();//注意大小写
            inner.in();
            Inner instanceof Outer报错

        }
        public class Inner{
            public void in() {
                System.out.println("in");
            }
        }
}
```

当然继承也没什么问题

```java
package LearnOOP;

public class Outer {
    public static void main(String[] args) {
        Outer outer = new Outer();
        Outer.Inner inner = outer.new Inner();

        System.out.println(inner instanceof Outer);//true
    }
    public class Inner extends Outer{}
}
```

### 内部类方法对外部属性的引用以及外部类方法对内部属性的引用

**外部类.this**

```java
package LearnOOP;

public class Outer {
    private String str = "Outer";

    public static void main(String[] args) {
        String str = "OuterMain";
        System.out.println(str);//OuterMain

        Outer outer = new Outer();
        System.out.println(outer.str);//Outer

        Outer.Inner inner = outer.new Inner();
        System.out.println(inner.str);//Inner

        inner.main();
    }
    public class Inner{
        private String str = "Inner";
        public void main() {
            String str = "InnerMain";
            System.out.println(str);//InnerMain

            System.out.println(this.str);//Inner

            //外部类.this
            System.out.println(Outer.this.str);//Outer

            Outer outer = new Outer();
            System.out.println(outer.str);//Outer

        }
    }s
}
```

## 静态内部类

### 创建对象

```java
publi class MAin{
    public static void main(String[] args){
        Outer.Inner inner = new Outer.Inner();
    }
}
```

```java
public class Outer {
    private int id;
    private static String name = "Outer";
    public static class Inner{//static
        public void get(){
            System.out.println(Outer.id);//报错，拿不到id，因为外部类的id不是static
            Outer outer = new Outer();
            System.out.println(outer.id);
            System.out.println(<Outer.>name);
        }
    }
}

```

## 一个java文件下可以有多个class，但只能有一个public class

## 局部内部类

定义在方法,代码块,构造器等执行器中

``` java
public class Outer {
    public void main() {
        class Inner{

        }
    }
}
```

## 匿名内部类[重点]

- 所谓匿名,就是程序员不需要为这个类声明名字

``` java
<Inner inner => new Inner();//<>内省去
```

即语法:

``` java
new 类(接口)名().方法(<参数>)

//或   

new 类(接口)名(){
    @Override
    重写方法
};
```

``` java
public class Outer {
    public static void main(String[] args) {
        //没有名字初始化类，不用将实例保存到变量中。
        new A(<可加参数>).say();
    }
}
class A{
    public A(<可加参数>){<...>}
    public void say(){
        System.out.println("hi");
    }
}
```

- 运行原理:

  1. 计算机把匿名内部类转化为其声明的类的子类

  2. 创建一个子类对象

### 匿名内部类实现成员内部类

```java
package LearnOOP;

public class Outer {
    public static void main(String[] args) {;
        Inner in = new Inner() {
            @Override
            public void say() {
                System.out.println("OverrideInner");
            }
        };
        in.say();//OverrideInner

    }
    public static class Inner{
        public void say(){
            System.out.println("Inner");
        }
    }
}
```

### 匿名内部类实现接口

```java
public class Outer {
    public static void main(String[] args) {
        UserService userService = new UserService() {
            @Override
            public void hello() {
                System.out.println("hi");
            }
        };
        userService.hello();
    }
}
interface UserService{
    void hello();
}
```

### 案例应用

猫狗游泳比赛

```java
package LearnOOP;
public class Outer {
    public static void main(String[] args) {
        //实现dog的Swim().speed()
        Swim dog = new Swim() {
            @Override
            public void speed() {
                System.out.println("dog swims fast");
            }
        };

        //开始比赛
        game(dog);//dog swims fast
        game(new Swim() {//实现cat的Swim().speed()
            @Override
            public void speed() {
                System.out.println("cat swims slow");
            }
        });//cat swims slow

    }
    //一个可以接收Swim的一切实现类对象的接口的方法,让对象内的speed()方法运行
    public static void game(Swim animal){
        animal.speed();
    }
}
interface Swim{
    void speed();
}
```

