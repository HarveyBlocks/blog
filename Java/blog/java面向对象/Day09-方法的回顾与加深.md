# 方法的回顾和加深

``` java
public void readFile (String file) throws IOExpection{

}
```

## 方法调用规则

静态方法--加static      	类名.方法名-->在另一个类中调用方法

![image-20230807142807201](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java面向对象/Day09-方法的回顾与加深/image-20230807142807201.png)

非静态方法   					先实例化这个类（new）

``` java
对象类型 对象名 =new 对象值;
对象名.方法名;
```

![image-20230807143459592](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java面向对象/Day09-方法的回顾与加深/image-20230807143459592.png)

``` java
package com.pac;

public class Student {
    public static void main(String[] args) {
        new Student().say();								//或者这样做
    }
    public void say(){System.out.println("hello");}
}

```

## 值传递和引用传递

java 是值传递

``` java
public class Student {
    public static void main(String[] args) {
        int x=2;
        new Student().change(x);
        System.out.println(x);             //2
    }
    public void change(int a){
        a=10;
    }
}
```

引用传递

``` java
public class Student {
    String name;    
    public void setName(String name) {
        this.name=name;
        /*
        依据引用传递，Student类里里的name
        确实改变
         */
    }
}
```

static方法中不能有this调用属性,实例方法中可以

![image-20230901001456683](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java面向对象/Day09-方法的回顾与加深/image-20230901001456683.png)

static是和类一起加载的

非静态对象创建之后（=类实例化之后=new了之后）才存在

static 方法属于类，非静态方法属于实例

所以static的方法里不能引用非静态的方法

有方法a(),b()

**在还没有new一个方法时**

一行四列例：static b()不能引用~~static~~ a（）--------------------------------------------------------------------------↓

| 能否引用 | ~~static~~ a() | static a() | ~~static~~ b() | static b() |
| -------------- | -------------- | ---------- | -------------- | ---------- |
| ~~static~~ a() | 否 | / | 否 | 否 |
| static a()     | / | 能 | 能 | 能 |
| ~~static~~ b()   | 否 | 否         | 否 | / |
|static b() | 能 | 能 | / | 能 |

