# 构造器

- 一个类即使什么也不写，它也有一个方法，即构造方法，构造器
- 如果没有构造器，new一个对象会报错
- new一个对象本质上是在new它的构造器，使其构造器方法执行
- 不能在创建对象之后再次调用构造器会编译时异常

1. 必须和类名相同
2. 必须没有返回值类型，也不能写void
3. 带有public

``` java
package com.pac;

public class Student {
    public Student(){
        ...
    }
}
```

# 作用

1. 初始值一些类中的变量
2. new一个对象本质上是在new它的构造器

``` java
package com.pac;

public class Student {
    String name；
    public Student(){
        this.name="Confucious";
    }
}
```

# 有参构造器与无参构造器

无参构造器

```java 
//同上
```

有参构造器

``` java
package com.pac;

public class Student {
    String name；
    public Student(String name){
        this.name=name;
    }
}
```

 一旦定义了有参构造，要用无参就必须**显示定义 **（相对于自动构造的）

### 实现定义默认值

```java
//重载一个有参构造器

public Son(int chinese) {
    this.chinese = chinese;
    this.math = 0;
}
```

没有在属性上马上赋值,就没有和**非静态属性一般不赋值**矛盾,也算尽到了规范

当然,也可以写的更**NB**些***简洁就是男人的浪漫!!***,**而且一目了然**

```java
public Son(int chinese) {
    this(chinese,0)
}
```

