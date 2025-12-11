# 享元

利用共享来支持大量细粒度的对象复用

共享已经存在的对象来大幅度减少需要创建的对象数量, 避免大量相似对象的开销

## 状态

-   内部状态
    -   不会随着环境的改变而改变的可共享部分
-   外部状态
    -   随着环境改变而改变的不可共享部分
    -   不作为字段

区分两种状态, 将外部状态外部化

## 缺点

区分内部状态和外部状态, 使程序逻辑复杂

需要维护一个享元池, 你得看他值不值

## 适用场景

大部分属性可以外部化

系统有大量重复又类似的对象



## 结构

-   抽象享元
    -   Flyweight
    -   声明具体享元的共有方法, 方法可以向外界提供享元对象的内部状态, 也可以设置外部状态
-   具体享元
    -   ConcreteFlyweight
    -   实现了抽象享元类, 称为享元对象
    -   为内部状态提供了存储空间
    -   通常结合单例来设计具体享元类, 为每一具体享元类提供唯一享元状态
-   非享元
    -   Unsharable Flyweight
    -   不能被共享的子类
    -   需要一个非共享具体类的对象可以直接通过实例化创建
-   享元工厂
    -   Flyweight Factory
    -   创建管理享元
    -   当客户对象请求一个享元对象, 享元工厂检查系统中是否存在符合要求的享元
    -   如果存在享元提供给用户
    -   不存在需要的享元创建新享元

## JDK中的享元

Integer的自动装箱带有享元

```java
Integer i1 = -129;
Integer i2 = -129;
Integer i3 = -128;
Integer i4 = -128;
Integer i5 = 127;
Integer i6 = 127;
Integer i7 = 128;
Integer i8 = 128;
System.out.println(i1 == i2); // false
System.out.println(i3 == i4); // true
System.out.println(i5 == i6); // true
System.out.println(i7 == i8); // false
```

其他包装类都有用享元

BigInteger, BigDecimal

String池

