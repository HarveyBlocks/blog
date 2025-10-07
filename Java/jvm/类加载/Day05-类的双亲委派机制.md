#类的双亲委派机制

双亲是parent的翻译, 不是真的有爹妈, 其实只有一个爹

## 类加载的要求

-   保证类加载的安全性
    -   避免恶意代码替换JDK中的核心类库
    -   确保核心类库的完整性和安全性
-   避免重复加载

## 概念

当一个类接收到加载类的任务时, 会**自底向上**查找是否被加载过, 再**由顶向下**进行加载

1.  需要加载一个类ObjectA
2.  Applicion是否加载过? 加载过? 返回该Class对象 .没加载过? 委派 Extension
3.  Extension是否加载过? 加载过? 返回该Class对象 .没加载过? 委派 Bootstrap
4.  Bootstrap是否加载过? 加载过? 返回该Class对象 .没加载过? 尝试加载
5.  Bootstrap是否能够(是否在加载目录)加载? 能就加载, 不能向下尝试
6.  Extension是否能够(是否在加载目录)加载? 能就加载, 不能向下尝试
7.  Application是否能够加载? 能就加载

```mermaid
graph LR
Bootstrap
Extension
Applicaion
```

-   Application继承Extension
-   Extension继承Bootstrap
-   向上委派查找确保了加载的唯一性
-   向下委派确定了加载的优先级



Q: 如果三个类加载器的加载目录都有A类, 谁去加载

A: Bootstrap

Q: java.lang.String能否被覆盖

A: 不能, Bootstrap先加载了java原生String, 到了加载自定义的String的时候, 检查到已经加载过了, 就不会重复加载

### 父类加载器

在类加载器中有一个父类加载器的字段

```java
private final ClassLoader parent;
```

以这种字段的方式在形式上继承, `parent=null`表示使用了Bootstrap加载器

查看加载器之间的继承关系

arthus

```shell
classloader -t
```



## 指定类加载器加载类

1.  `Class.forname()` 使用当前类的类加载器去加载指定的类

2.  获取类加载器, 通过类加载器的loadClass方法指定某个类加载器加载

    ```java
    ClassLoader classLoader = ObjectA.class.getClassLoader();
    Class<?> type = classLoader.loadClass("com.harvey.obj.ObjectB");
    ```

    ```java
    ClassLoader classLoader = ClassLoadApplication.class.getClassLoader();
    System.out.println("classLoader = " + classLoader);// ClassLoaders$AppClassLoader
    Class<?> stringClass = classLoader.loadClass("java.lang.String");
    System.out.println(stringClass.getClassLoader()); // null
    ```

    



## ClassNotFoundException

三个类加载器都无法加载这个类

## 打破双清委派机制

1.  自定义类加载器
    -   重写loadClass方法
    -   Tomcat用这种方法实现应用之间的隔离
2.  线程上下文类加载器
    -   如JDBC, JNDI
3.  Osgi框架的类加载器
    -   允许统计之间委派进行类的加载

### 需求

例如Tomcat支持一台Tomcat服务器上运行多台应用, 如果遇到两个应用有一个类的全类名完全相同, 就会出现加载不出后面的类的情况



### 自定义类加载器

自定义的类加载器可以不用再向上委派类的加载任务, 打破双亲委派机制



```java
/**
 * 类加载的入口, 提供双亲委派机制
 * 内部调用findClass
 */
public Class<?> loadClass(String name);
// resolve默认false, 通过loadClass加载类不会做连接
protected Class<?> loadClass(String name, boolean resolve);
```

```cpp
try {
    if (parent != null) {
        c = parent.loadClass(name, false);
    } else {
        c = findBootstrapClassOrNull(name);
    }
} catch (ClassNotFoundException e) {
    // ClassNotFoundException thrown if class not found
    // from the non-null parent class loader
}
```



```java
/**
 * 由类加载器子类实现, 获取二进制数据调用defineClass
 * 比如URLCLassLoader会根据文件路径去获取类文件中的二进制数据
 */
protected Class<?> findClass(String name);
```



```cpp
/**
 * 做类名的校验
 * 调用虚拟机底层的方法将字节码信息加载到虚拟机内存
 */
protected final Class<?> defineClass(String name, byte[] b, int off, int len);
```



```java
/**
 * 类生命周期中的连接
 */
protected final void  resolveClass(Class<?> c);
```



#### 实现



```java
public class MyClassLoader extends ClassLoader {
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Assert.notNull(name, "load class name can not be null");
        String classpath = "D:\\IT_study\\JDK\\17!\\java.base\\" //FAKE
                + String.join("\\", name.split("\\.")) + ".class";
        try (FileInputStream fis = new FileInputStream(classpath)) {
            byte[] data = fis.readAllBytes();
            return defineClass(name, data, 0, data.length);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        MyClassLoader myClassLoader = new MyClassLoader();
        try {
            Class<?> aClass = myClassLoader.loadClass("java.lang.String");
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }
}
```

自定义的ClassLoader的双亲默认是Appliction, 可通过`getParent()`查看



若要增加新功能, 不应该破坏双亲委派机制, 而双亲委派机制再loadClass中实现, 就不要重写loadClass方法

重写findClass方法, 校验这个类在不在负责的加载路径上, 然后调用defineClass();

### 线程上下文的类加载器

不要困惑, JDBC没有打破双亲委派机制, 只是一本有历史遗留问题的书认为打破了



例如JDBC, JNDI

JDBC是在java中操作数据库, 但是不想出现特定的语法, 希望增加泛用性, 以致能对接任何一种数据库

JDBC中使用了**DriverManager**来管理项目中引入的不同数据库驱动,例如mysql驱动, oracle驱动

**DriverManager**是JDK提供的, 位于`rt.jar`中, 由Bootstrap加载

Application加载引入的jar包中的驱动类

![image-20240518091301497](../asset/Day05-%E7%B1%BB%E7%9A%84%E5%8F%8C%E4%BA%B2%E5%A7%94%E6%B4%BE%E6%9C%BA%E5%88%B6/image-20240518091301497.png)



####SPI机制

>   Service Provider Interface

DriverManager如何找到加载的Jar包的驱动类在哪?

SPI机制是JDK内置的一种服务提供发现的机制

如果想要加载接口的实现类对象, 就使用SPI快速找到?????????????

1.  Mysql的Jar包暴露驱动给DriverManager使用

    在`META-INF/services/`目录下创建`java.sql.Driver`(被实现接口名)文件, 内容写实现接口的类

    SPI扫描该目录下的文件, 加载文件内容里的实现类

2.  实现该接口

    ```java
    package com.mysql.cj.jdbc;
    
    import ...
    
    public class Driver extends NonRegisteringDriver implements java.sql.Driver{
        pulic Driver() throws SQLException{
            
        }
        static {
            try{
                DriverManager.registerDriver(new Driver());
            }catch(SQLException e){
                throw new RuntimeException("Can't register driver!");
            }
        }
    }
    ```

3.  `java.sql.DriverManager#ensureDriversInitialized`

    ```java
    ServiceLoader<java.sql.Driver> loadedDrivers = ServiceLoader.load(java.sql.Driver.class);
    ```

    ![image-20240518110900127](../asset/Day05-%E7%B1%BB%E7%9A%84%E5%8F%8C%E4%BA%B2%E5%A7%94%E6%B4%BE%E6%9C%BA%E5%88%B6/image-20240518110900127.png) 

4.  SPI中使用线程上下文中保存的类加载器进行加载, 这个类加载器一般是Application

    `java.util.ServiceLoader`

    ```java
    public static <S> ServiceLoader<S> load(Class<S> service){
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return Serviceloader.load(service, cl);
    }
    ```

    

### Osgi模块化框架

早期的Java没有模块化思想, 所有核心类放在`rt.jar`

Osgi框架将功能相似的类以Bundle的方式放到Jar包里去维护

Osgi框架支持同级的类加载器互相委派

Osgi还实现了热部署的功能, 服务不停止的情况下, 动态更新字节码文件到字节码中

#### Arthus热部署

类加载器加载新加入的字节码文件到内存中

1.  反编译class文件

    ```shell
    jad --source-only 全类名 > 目录/文件名.java
    ```

2.  修改源码

3.  编译修改过的代码

    ```shell
    mc -c 类加载器的hashcode 目录/文件名.java -d 输出目录
    ```

    `-c 类加载器的hashcode`: 编译一个类可能需要别的类, 指定类加载器, 同一个类加载器加载的类都能相互联系

4.  加载新的字节码

    ```shell
    retransform class文件目录/文件名.class
    ```

    



注意, 重启之后, 原本的字节码文件就会恢复(更改了内存, 没更改磁盘)

除非将class文件放入jar包中进行更新

`retrasform`不能添加方法或者字段, 也不能更新正在执行中的方法















