# 循环引用

>   相关死锁一样

## 目标:

>   解决你依赖我,我依赖你的问题

![image-20231104123244652](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/基于XML文件的Spring应用/Spring-Bean的生命周期/Day06-Bean的循环引用/image-20231104123244652.png)

-   这个死循环,这个不好

## 解决方案-三级缓存

### 简介

>   存储完整Bean实例和半成品Bean实例
>
>   在**DefaultListableBeanFactiry**的上四级父类**DefaultSinglentonBeanRegistry**中提供如下三个Map:

#### DefaultSinglentonBeanRegistry的三个Map

```java
public class 
    DefaultSingletonBeanRegistry 
    extends SimpleAliasRegistry 
    implements SingletonBeanRegistry {
    ...

    //一级缓存->存储完整Bean的单例池
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap(256);

    //二级缓存->早期单例池,Bean还没有创建完毕,不是完整的Bean
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap(16);

    //三级缓存->单例Bean的工厂池,缓存半成品对象,对象未被引用,使用时在通过工厂创建Bean
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap(16);
    ...
}
```

-   补充ObjectFactory函数式接口

    ```java
    @FunctionalInterface
    public interface ObjectFactory<T> {
        //这个方法是用来提供真实的ObjectBean对象的
        T getObject() throws BeansException;
    }
    ```

## 流程

1.  UserService实例化得到*userService*,还未进行初始化方法
2.  *userService*改造
    1.  为*userService*创建一个对应的ObjectFactory
    2.  通过对应objectFactory.getObject()得到返回值*userService*(经包浆,**未被别人引用**)
    3.  将*userService*(经包浆,**未被别人引用**)**存入三级缓存**
3.  去**一->二->三存**寻找需要注入的userDao
    -   没找到
4.  去创建userDao
    1.  UserDao实例化得到userDao,还未进行初始化方法
    2.  userDao改造
        1.  为userDao创建一个对应的ObjectFactory
        2.  通过对应objectFactory.getObject()得到返回值userDao(经包浆,**未被别人引用**)
        3.  将userDao(经包浆,**未被别人引用**)**存入三级缓存**
    3.  去**一->二->三级缓存**寻找需要注入的*userService*
        -   **找到啦!(在三级缓存)**
    4.  把*userService*注入userDao
    5.  把*userService*从**三级缓存中移除**
    6.  把*userService***存入二级缓存**
        -   代表*userService*尽管是个半成品,但它**能被引用了**
    7.  userDao执行[其他生命周期](Day06😐-Bean的初始化阶段 (copy).md)
    8.  userDao成为一个完整的Bean,
        1.  **存入一级缓存**
        2.  **从二,三级缓存移除**(虽然二级缓存里面没有userDao,但删一下又没有关系)
5.  *userService*注入userDao
6.  *userService*执行[其他生命周期](Day06😐-Bean的初始化阶段 (copy).md)
7.  *userService*成为一个完整的Bean,
    1.  **存入一级缓存**
    2.  **从二,三级缓存移除**(虽然三级缓存里面没有userDao,但删一下又没有关系)

