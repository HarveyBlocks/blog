
# SpringBean 的配置

![image-20231030130011011](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/基于XML文件的Spring应用/xml与Spring基础应用/Day02-SpringBean的配置/image-20231030130011011.png)

这些配置不分先后

## id

**唯一标识**

为了:

 1.    和xml文件里的其他Bean的property产生关联

       ```name
       <property name="userDao" ref="userDao"/>
       ```

 2.    BeanFactory(或其他创建工厂的类)在创建工厂时的识别
       ```java
       UserService userService =(UserService) beanFactory.getBean("userService");
       ```

## class

填入全类名

连接实体类

### id-class

```xml
<bean id="userService" class="com.harvey.Impl.UserServiceImpl"/>
```

![image-20231030131034992](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/基于XML文件的Spring应用/xml与Spring基础应用/Day02-SpringBean的配置/image-20231030131034992.png)

-   其实是在BeanFactory里形成了key-value

#### 如果不配id

```xml
    <bean  class="com.harvey.Impl.UserServiceImpl">
        <!--id="userService"-->
```

![image-20231030131340505](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/基于XML文件的Spring应用/xml与Spring基础应用/Day02-SpringBean的配置/image-20231030131340505.png)

-   它也是有Key的,是**全类名**

    -   Key选择全类名适用BeanFactory和ApplicationContext
        -   因为ApplicationContext底层还是用的BeanFactory
        -   其他类我不知道

    ```java
    UserService userService =
        (UserService) beanFactory.getBean("com.harvey.Impl.UserServiceImpl");
    ```

    在不给id的时候也是可以执行的

## name取别名

```xml
<bean 
      id="userService" 
      name = "us,US,Us,uS,UserService" 
      class="com.harvey.Impl.UserServiceImpl"
/>
```

-   这样us,US,Us,uS,UserService都可以用来getBean,例如:

    ```java
    UserService userService =
        (UserService) beanFactory.getBean("UserService");
    ```

![image-20231030132328860](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/基于XML文件的Spring应用/xml与Spring基础应用/Day02-SpringBean的配置/image-20231030132328860.png)

-   BeanFactory使用了映射

### 不配id配name

-   **name指向全类名**
-   一般不配name配id

## scope作用范围

-   单纯的Spring环境(2个):

### singleton - 单例

-   默认,业务中也一般这个
-   Spring容器创建时实例化Bean,并存储到容器的**单例池(singletonObjects)**中
-   每次getBean都是从单例池中获取相同的Bean

### prototype - 原型

-   Spring容器初始化不会创建Bean实例,当调用getBean时才会实例化Bean
-   每次getBean创建一个新的Bean实例

## lazy-init 延迟加载

-   **对BeanFactory无效,因为BeanFactory在getBean创建对象,没救了**
-   否则,会把在`new xxx(aaa.xml)`时候创建的对象延迟到`getBean("xxx")`创建

```xml
<bean id="userService"  class="com.harvey.Impl.UserServiceImpl" lazy-init="true">
```

## 初始化和销毁方式

-   init-method 初始化方式 
-   destroy-method 销毁方式

-   Java和xml连接起来

### 在Java文件

```java
public class UserServiceImpl implements UserService {
    ......

    public void init114514(){
        System.out.println("用init114514()初始化中");
    }
    public void destroy114514(){
        System.out.println("用destory114514()销毁中");
    }
}
```

### 在xml文件:

```java
<bean id="userService"  
      class="com.harvey.Impl.UserServiceImpl" 
      init-method="init114514" destroy-method="destroy114514"
>
```

### 执行顺序

-   构造方法->init114514()

```
UserService创建
UserDao创建
BeanFactory去调用该方法获取userDao设置到此处com.harvey.Impl.UserDaoImpl@4f9a3314
用init114514()初始化中
```

要创建了再初始化

-   显式地关闭了容器-->destory114514()
-   ClassPathXmlApplicationContext里有close()方法,顺带的,会把destroy都执行一遍的

### P.S.

-   `init-meshod`偶尔用

-   `destroy-method`几乎不会用(但我真的觉得超级好用!!!)

-   **在scope="singleton"(默认)下使用!**否则调用不来

-   **Bean的销毁和Bean的销毁方法的调用是两回事**

    -   Bean在销毁方法调用之前就被销毁了,也是完全有可能的
    -   Spring不会知道Bean快要挂掉,就不会调用一些销毁方法帮助我们,但是,你把ApplicationCollection关掉的时候,Spring就知道了
    -   **注意: ApplicationContext没有close(),他的子类ClassPathXmlApplicationContext有**

    ```java
    applicationContext.close();
    ```

-   **我们还可以通过实现InitializingBean接口,完成Bean的初始化操作**

-   然后Spring发现我们的类实现了InitializingBean接口就会自动地帮我们做它的afterPropertiesSet()方法

-   Java类:

    ```java
    public class UserServiceImpl implements UserService, InitializingBean {
    	...
        public void afterPropertiesSet() throws Exception {//爱抛不抛
            System.out.println("这是实现InitializingBean接口的afterPropertiesSet方法做的初始化");
        }
    }
    ```

    返回情况:

    ```
    UserService创建
    UserDao创建
    BeanFactory去调用该方法获取userDao设置到此处com.harvey.Impl.UserDaoImpl@3b2c72c2
    这是实现InitializingBean接口的afterPropertiesSet方法做的初始化
    用init114514()初始化中
    23-10-30 14:23 [main] INFO  TestSpring - com.harvey.Impl.UserServiceImpl@971d0d8
    ```

## factory-method和factory-bean

     [Spring实例化的方式](Day02😘-Spring实例化的方式.md)

