# Spring实例化的两种方式

1.  构造方法实例化
    -   底层通过构造方法对Bean进行实例化
2.  工厂方法实例化
    -   底层通过调用工厂方法对Bean实例化

**BeanFactory(包括其子类ApplicaationContext)本身是用工厂方法创建的,但是实例化的方式和这个工厂方法不一样**

## 构造方法实例化

### 底层原理:

-   反射

### 用有参构造器实例化对象

由于没有设置,**默认**用的是**无参的构造方法**

**有参的构造方法**是不能被执行的

-   注释掉无参构造,报错 **No Default Cinstructor Found** **不能找到对应的构造器**

```java
public class UserServiceImpl implements UserService {
    public UserServiceImpl() {
        System.out.println("UserService无参构造");
    }
    public UserServiceImpl(String message,int number){
        System.out.println("UserService有参构造,message="+message+",number="+number);
    }

    private UserDao userDao;
    //Bean工厂去调用 从容器中获取userDao设置到此处
    public void setUserDao(UserDao userDao){
		...
    }
}
```

#### 解决方法 : constructor-arg

在xml文件:

```xml
<bean id="userService" class="com.harvey.Impl.UserServiceImpl">

    <!--name是形参名-->
    <constructor-arg name="message" value="你好"/>
	<constructor-arg name="number" value="2"/>

    <property name="userDao" ref="userDao"/>
</bean>
```

arg->argument:参数

-   测试:

    ```
    UserService有参构造,message=你好,number=250
    UserDao创建
    BeanFactory去调用setUserDao(UserDao userDao)获取userDao注入到UserServiceImpl
    23-10-30 15:20 [main] INFO  TestSpring - com.harvey.Impl.UserServiceImpl@3b2cf7ab
    ```

-   但是:

    -   有参无参都存在->实行有参

    -   注释掉无参->实行有参

    -   注释掉有参->报错

        `Could not resolve matching constructor on bean class`

-   **就从一个极端走向另一个极端了呗**

-   其实可以多做几个Bean

## 工厂方法实例化

-   自定义( 提供 )一个工厂
-   工厂方法分成三类
    1.  静态工厂方法实例化
    2.  动态工厂方法实例化
    3.  实现FactoryBean规范延迟实例化Bean

### 工厂方法的好处

在创建工厂之前做点事儿

```java
public static UserService getUserService(){
    //做点事
    return new UserServiceImpl();
}
```

### 静态工厂方法实例化

>   静态方法产生一个Bean,交给Spring管理

-   Java

    ```java
    public class MyBeanFactory {
        public static UserService getUserService(){
            return new UserServiceImpl();
        }
    }
    ```

-   Xml

    ```xml
    <!--无可奈何取了这个id-->
    <bean id="getUserService" 
          class="com.harvey.factory.MyBeanFactory" 
          factory-method="getUserService"
    />
    ```

分析Spring的心路历程:

1.  依据**class=全类名**找到了工厂类
2.  看见了**factory-method=**
3.  懂了:不是要**MyBeanFactory**当作对象,而是要**getUserService**的返回值
4.  然后他就把**getUserService**的返回值**UserService**当作对象
5.  再以指定的**id作为BeanName**存储到容器中

### 非静态工厂方法实例化

>   非静态方法产生一个Bean,交给Spring管理

-   java

	```java
public class MyBeanFactory {
        public UserService getUserService(){
            return new UserServiceImpl();
        }
}
	```

-   xml

    ```xml
    <bean id="myBeanFactory" 
          class="com.harvey.factory.MyBeanFactory" />

    <!--factory-bean,先实例化看myBeanFactory (id) ,然后使用里面的非静态实例化方法-->
    <bean id="getUserService" 
          factory-bean="myBeanFactory" 
          factory-method="getUserService"/>
    ```

-   与静态工厂方法实例化的**另一区别**:
    -   使用静态,Spring容器中不会存在工厂类(不用实例化)
    -   使用动态,Spring容器中会有工厂类(为了实例化)

### constructor-arg  给工厂方法配参数

静态工厂和动态工厂和构造方法加参数是一样的,这里再演示一个动态工厂的xml文件

```xml
<bean id="myBeanFactory" class="com.harvey.factory.MyBeanFactory" />

<!--要用factory-bean=id指定非静态工厂方法的类好去实例化这个类-->
<bean id="getUserService" factory-bean="myBeanFactory" factory-method="getUserService">
    <constructor-arg name="message" value="除夕不放假"/>
    <constructor-arg name="number" value="555"/>
</bean>
```

```
工厂类惨遭实例化
工厂方法执行,message=除夕不放假,number=555
UserService无参构造
23-10-30 16:04 [main] INFO  TestSpring - 成功创建com.harvey.Impl.UserServiceImpl@8e0379d
```

### 实现FactoryBean规范延迟实例化Bean

-   自己的Factory类实现FactoryBean接口,实现FactoryBean的规范

额.....我突然发现好像**有FactoryBean,也有BeanFactory**

#### 问题的产生

工厂方法名字都是我自定义的,所以要告诉xml文件我的工厂方法名是啥

但是实现FactoryBean之后,就不用再写了

#### 康康FactoryBean的源码

```java
package org.springframework.beans.factory;

import org.springframework.lang.Nullable;

public interface FactoryBean<T> {
    String OBJECT_TYPE_ATTRIBUTE = "factoryBeanObjectType";

    @Nullable
    T getObject() throws Exception;

    @Nullable
    Class<?> getObjectType();

    default boolean isSingleton() {
        return true;
    }
}
```

#### 使用规范化

```java
public class MyBeanFactory implements FactoryBean<UserService> {
    public MyBeanFactory(){
        System.out.println("工厂类实例化");
    }

    public UserService getObject() throws Exception {
        System.out.println("getObject(),启动");
        return new UserServiceImpl();
    }

    public Class<> getObjectType() {
        System.out.println("getObjectType(),启动");
        return UserService.class;  
    }
}
```

-   xml

    ```xml
    <bean id="myBeanFactory" class="com.harvey.factory.MyBeanFactory" />
    ```

    看你**MyBeanFactory实现了FactoryBean**,Spring明白了一切

-   输出结果

    ```
    工厂类遭实例化
    getObject(),启动
    UserService无参构造
    23-10-30 16:25 [main] INFO  TestSpring - 成功创建com.harvey.Impl.UserServiceImpl@290dbf45
    ```

![image-20231030163230085](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/基于XML文件的Spring应用/xml与Spring基础应用/Day02-Spring实例化的方式/image-20231030163230085.png)

-   啊?你怎么是MyBeanFactory捏?

![image-20231030163544481](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/基于XML文件的Spring应用/xml与Spring基础应用/Day02-Spring实例化的方式/image-20231030163544481.png)

-   此乃真身😀

```markdown
工厂类遭实例化 **这一步的存在,导致其产生了替身**
getObject(),启动 
UserService无参构造
23-10-30 16:25 [main] INFO  TestSpring - 成功创建com.harvey.Impl.UserServiceImpl@290dbf45
```

#### 解释规范化注入的不同

-   关于上面真假身的问题

    ```java
    	@Test
        public void testFactory() {

            DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

            XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);

            reader.loadBeanDefinitions("beans.xml");
        //这一步做了所有的事情:
            /*
            工厂类遭实例化
    		getObject(),启动
    		UserService无参构造
            */
            UserService userService = (UserService) beanFactory.getBean("myBeanFactory");
            TestLogger.LOGGER.info("成功创建"+userService);
    }
    ```

延迟了调用

好处在于可以把临时产生的Bean的需求先产生,对于Bean的具体产生放在后面

