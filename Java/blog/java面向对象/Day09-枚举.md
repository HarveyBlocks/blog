# 枚举

-   第一行只能使用常量名

-   支持字段(静态),构造器(**只有私有**,不论写不写),和成员方法(静态)

-   枚举类被finnal修饰(编译成自解码文件后自动加的),是最总类,不能被继承

-   自动继承**Enum类**, 但主动继承就报错

-   有额外的API, 是本来就存在的

    ```java
    public enum A{
    	X,Y,Z;
        
        private String name;
        public String getName(){
            return name;
        }
        
        public void setName(String name){
            this.name = name;
        }
        
    }
    class Demo{
        public static void main(String[] args){
            A a1 = A.X;//A不能调用非静态的方法,但是a1可以
            System.out.println(a1);//X
    
            //额外的API
            A[] as = A.values();
            A a3 = A.valueOf("Z");
            System.out.println(a3);// Z
            System.out.println(a3.name());// X
            System.out.println(a3.ordinal());// 2
        }
    }
    
    ```





## 抽象枚举

```java
package com.harvey.mybatis.plus;

public enum A{
    // 这些没写的也调用了构造器,是无参的构造器
    // 所以有有参构造器之后也要写无参构造器,否则这些会没有构造器
    X,Y,Z,// 这些其实是单例
    // 类似于构造器,
    ALPHA("阿尔法"){
        @Override
        public String getName() {
            return "ALPHA";
        }
    },BETA(){
        @Override
        public String getName() {
            return "BETA";
        }
    };

    private A(String name){
        this.name = name;
    }

    private String name;

    A() {

    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

}
class Demo{
    public static void main(String[] args){
        A a1 = A.X;//A不能调用非静态的方法,但是a1可以
        System.out.println(a1);//X

        //额外的API
        A[] as = A.values();
        A a3 = A.valueOf("Z");
        System.out.println(a3);// Z
        System.out.println(a3.name());// X
        System.out.println(a3.ordinal());// 2
    }
}
```

