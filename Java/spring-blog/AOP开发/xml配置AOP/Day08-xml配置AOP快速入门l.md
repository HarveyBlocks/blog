# xml配置AOP

##问题及解决

我们写的代码,包是写死的

-   通过Spring配置,指定哪些包,哪些类,哪些方法需要被增强
    -   切点表达式的配置
-   配置目标方法要被哪些通知方法所增强,在目标方法之前还是之后执行



>   配置方式的设计,配置文件(注解)的解析工作,Spring已经帮我们封装好了



##步骤

-   先把模拟AOP注释掉

    ```xml
    <!--    <bean id="mock" class="com.harvey.processor.MockAopBeanPostProfessor"/>-->
    ```

    

1.  导入包的相关坐标

    -   aspect(切面)j(ava)

        ```xml
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.9.6</version>
        </dependency>
        ```

        Spring也有AOP的包,但是aspctj更好用,但是Spring整合了aspectj

2.  准备目标类,增强类,交给Spring管理

3.  配置切点表达式

    -   指定哪些类可以被增强
    -   类里的哪些方法可以被增强

4.  配置织入

    -   切点被哪些通知方法增强
    -   通知方法是前置增强还是后置增强

###配置XML

####配置目标类和增强类

```xml
<!--配置目标类-->
<bean id="userService" class="com.harvey.service.impl.UserServiceImpl"/>
<!--配置通知类-->
<bean id="myAdvice" class="com.harvey.advice.MyAdvice"/>
```

-   就是普通配置一个类

#### 配置AOP的XSD

```java
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/aop
       http://www.springframework.org/schema/aop/spring-aop.xsd">
```

重点是这几句:

```xml
xmlns:aop="http://www.springframework.org/schema/aop"
http://www.springframework.org/schema/aop
http://www.springframework.org/schema/aop/spring-aop.xsd"
```



#### 配置AOP:config

```xml
<!--配置aop-->
<aop:config>
</aop:config>
```

####配置切点表达式

```xml
<aop:config>

    <!--配置切点表达式-->
    <!--指定哪些方法被增强 id,老规矩,随便取,唯一标识 -->
    <aop:pointcut id="myPrintCutShow1" expression=
            "execution(void com.harvey.service.UserService.show1())"/>
              <!--增强  无返回值的                全类名.方法名(参数列表)-->
              <!--可以指定类,也可以指定接口,其实现类皆会增强(当然)-->

    <aop:pointcut id="myPrintCutToString" expression=
            "execution(void com.harvey.service.impl.UserServiceImpl.toString())"/>
    		<!--发现没有,void是错的,但它不会检查,不会编译错误,不会运行时异常(*Φ皿Φ*)-->
	<!--可以配置多个切点-->
	
</aop:config>
```

#### 配置织入

```xml
<aop:config>
    <aop:pointcut id="myPrintCutShow1" expression=
            "execution(void com.harvey.service.UserService.show1())"/>
    <aop:pointcut id="myPrintCutToString" expression=
            "execution(void com.harvey.service.impl.UserServiceImpl.toString())"/>

    
    
    <!--配置织入,哪些切点和哪些通知结合-->
    <!--            指定通知类-->
    <aop:aspect ref="myAdvice">
        <!--前置增强方法与我们的命名无关-->
        <aop:before method="before" pointcut-ref="myPrintCutShow1"/>
        <!--指定增强方法类型   指定增强方法           指定被增强的切点-->

    </aop:aspect>
</aop:config>
```