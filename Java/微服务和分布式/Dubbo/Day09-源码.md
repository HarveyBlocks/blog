# 源码



## SPI

>   Service Provider Interface

服务发现机制

将接口实现类的全限定名配置在文件中, 并由服务加载器读取配置文件, 加载实现类(Protobuf)

运行时, 动态为接口替换实现类(解耦)

容易通过SPI机制为程序提供拓展功能

Dubbo对Java原生的SPI机制进行了增强

### JavaSPI

1.  resources中创建`/META-INF/services`目录

2.  `/services`目录下新建文件. 文件名为接口的全类名

3.  文件内容为接口实现类的全类名

4.  每个实现类占一行(😓不能用yml, xml, properties吗?)

5.  获取实现类

    ```java
    ServiceLoader<UserService> loader = ServiceLoader.load(UserService.class);
    Iterator<UserService> iterator = loader.iterator();
    while (iterator.hasNext()){
        UserService next = iterator.next();
        User user = next.sayHello("张三");
    }
    ```



缺点

-   所有配置了的实现类全部都会加载并实例化
-   无法根据参数来获取对应的实现类

### DubboSPI

####使用

1.  resources中创建`/META-INF/dubbo`目录

2.  `/dubbo`目录下新建文件. 文件名为接口的全类名

3.  文件内容为`key=value` 的实现类名(同文件中唯一标识)

    ```properties
    impl1=com.harvey.dubbo.service.impl.UserServiceImpl1
    impl2=com.harvey.dubbo.service.impl.UserServiceImpl2
    ```

4.  在接口上注解`@SPI`

    ```java
    import com.alibaba.dubbo.common.extension.SPI;
    
    @SPI
    public interface UserService {
        User sayHello(String name);
    }
    ```

5.  获取实现类

    ```java
    ExtensionLoader<UserService> extensionLoader =
            ExtensionLoader.getExtensionLoader(UserService.class);
    UserService impl0 = extensionLoader.getExtension("impl0");
    impl0.sayHello("王五");
    ```



#### 源码

`com.alibaba.dubbo.common.extension.ExtensionLoader#getExtensionLoader`

```java
// 从缓存中获取加载器
ExtensionLoader<T> loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
if (loader == null) {
    // 未命中, 从配置文件中获取, 然后加入缓存
    EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<T>(type)); // 为什么不让这个方法的返回值, 直接是缓存里的对象?
    // 再次获取加载器
    loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
}
```

`com.alibaba.dubbo.common.extension.ExtensionLoader#getExtension`

```java
// 从缓存中获取服务实现类的信息
Holder<Object> holder = cachedInstances.get(name);
if (holder == null) {
    // 未命中, 从配置文件中获取, 并加载再次获取实现类对象信息, 并加入缓存
    cachedInstances.putIfAbsent(name, new Holder<Object>());
    // 再次从缓存中获取实现类对象信息
    holder = cachedInstances.get(name);
}
// 获取实现类的实例化对象
Object instance = holder.get();
if (instance == null) {
    // 实现类对象未被实例化
    synchronized (holder) {
        instance = holder.get();
        if (instance == null) {
            // 创建实例化对象
            instance = createExtension(name);
            // 加入holder;
            holder.set(instance);
        }
    }
}
return (T) instance;
```



`com.alibaba.dubbo.common.extension.ExtensionLoader#createExtension`

```java
// 依据配置文件中的`key`获取实现类类对象
Class<?> clazz = getExtensionClasses().get(name);
```

```java
T instance = (T) EXTENSION_INSTANCES.get(clazz);
if (instance == null) {
    // 通过反射, 实体类创建
    EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
    instance = (T) EXTENSION_INSTANCES.get(clazz);
}
// 向实例中注入依赖, 原理是检测方法名setXXX, 判断是否有sertter
injectExtension(instance);
Set<Class<?>> wrapperClasses = cachedWrapperClasses;
if (wrapperClasses != null && !wrapperClasses.isEmpty()) {
    for (Class<?> wrapperClass : wrapperClasses) {
        instance = injectExtension((T) wrapperClass.getConstructor(type).newInstance(instance));
    }
}
return instance;
```



## 服务暴露

就是放到注册中心(远程服务)或本地服务(可配置)的过程



## 服务注入

###注入时机

1.  Spring容器调用`ReferenceBean#afterPropertiesSet`方法时引用服务, 饿汉式
2.  `ReferrenceBean`对应的服务被注入到其他类中时引用.懒汉式(默认)
3.  可用xml标签`dubbo:reference`的`init`属性配置开启饿汉

### 注入机制

1.  服务被注入到其他类中时, Spring第一时间调用`getObject`方法

2.  进行配置检查与收集工作

3.  根据收集到的信息决定服务用的方式

    -   本地(JVM)服务
    -   通过直连方式获得的远程服务
    -   通过注册中心的服务

4.  获取`invoker`实例(

    -   如果有多个注册中心,多个服务提供者, 就获取一组`Invoker`实例

    -   通过集群管理类`Cluster`将多个`Invoker`合并成一个`Invoker`实例

5.  框架通过代理工厂类(ProxyFactory)为服务接口生成代理类, 让代理去调用`Invoke`逻辑

    -   避免Dubbo框架代码对业务代码的入侵





