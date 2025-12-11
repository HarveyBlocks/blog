```
TREE . /F /A
dir /S/B *.md
```

```Test
C:\USERS\27970\DESKTOP\IT\JDK\SPRING-TEST
|   .gitignore
|   pom.xml
|
+---.idea
|	 |	...
|
+---log
|       output.log
|
+---src
|   +---main
|   |   +---java
|   |   |   \---com
|   |   |       \---harvey
|   |   |           |   App.java
|   |   |           |
|   |   |           +---advice
|   |   |           |       MyAdvice.java
|   |   |           |
|   |   |           +---processor
|   |   |           |       MockAopBeanPostProfessor.java
|   |   |           |
|   |   |           +---service
|   |   |           |   |   UserService.java
|   |   |           |   |
|   |   |           |   \---impl
|   |   |           |           UserServiceImpl.java
|   |   |           |
|   |   |           \---utils
|   |   |                   Log.java
|   |   |                   Time.java
|   |   |
|   |   \---resources
|   |           applicationContest.xml
|   |           logback.xml
|   |
|   \---test
|       |	...
\---target
    |   ...

```

# main/java/com/harvey

-   主目录

## App.java

-   测试类

```java
package com.harvey;

import com.harvey.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @ClassName: App
 * @Author: Harvey Blocks
 * @Description: 简单的,虚假的测试类
 * @Date: 2023/11/09 16:38
 * @Version: 1.0
 */
public class App {
    public static void main( String[] args ){
        ApplicationContext app = new ClassPathXmlApplicationContext("applicationContest.xml");
        UserService userService =(UserService) app.getBean("userService");
        System.out.println("-------------测试开始----------------");
        System.out.println(userService);
        System.out.println("-------------show1()--------------");
        userService.show1();
        System.out.println("-------------show2()--------------");
        userService.show2();
    }
}
```

## advice

-   增强类包

### MyAdvice.java

-   增强对象

```java
package com.harvey.advice;

import com.harvey.utils.Log;

/**
 * @ClassName: UserServiceImpl
 * @Author: Harvey Blocks
 * @Description: 增强类, 内部提供增强方法
 * @Date: 2023/11/09 15:44
 * @Version: 1.0
 */
public class MyAdvice {
    public void before() {
        System.out.println("前期增强");
    }

    public void after() {
        System.out.println("后期增强");
    }

}
```

## processor

-   进程类包

### MockAopBeanPostProfessor.java

-   模拟AOP模式

```java
package com.harvey.processor;

import com.harvey.advice.MyAdvice;
import com.harvey.service.impl.UserServiceImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Proxy;

/**
 * @ClassName: MockAopBeanPostProfessor
 * @Author: Harvey Blocks
 * @Description: TODO
 * @Date: 2023/11/09 16:16
 * @Version: 1.0
 */
public class MockAopBeanPostProfessor
        implements BeanPostProcessor, ApplicationContextAware {

    /**
     * @description 对UserService中的show1和show2方法进行增强.
     * 需要对bean进行筛选.筛选条件?
     * service.impl包下的所有类进行增强.
     * 获取MyAdvice?
     * 通过把MyAdvice和UserServiceImpl配置到singletonObjectives里去.
     */
    @Override
    public Object postProcessAfterInitialization(
            Object bean, String beanName)
            throws BeansException {
        if (bean.getClass().getPackage()
                .equals(UserServiceImpl.class.getPackage())
                //这样不好,万一UserService一换位置,就不好了
        ) {
            Object beanProxy = adviceService(bean);
            return beanProxy;//分成两行写更清楚
        }
        return bean;
    }

    /**
     * @description 把Service增强
     * @param bean 需要被增强的原对象
     * */
    public Object adviceService(Object bean) {
        MyAdvice myAdvice = applicationContext.getBean(MyAdvice.class);
        return Proxy.newProxyInstance(
                bean.getClass().getClassLoader(),
                bean.getClass().getInterfaces(),
                (proxy, method, args) -> {
                    // 1. 执行前期增强方法
                    myAdvice.before();
                    // 2.执行目标方法
                    Object methodResult = method.invoke(bean, args);
                    // 3.执行后期增强方法
                    myAdvice.after();
                    return methodResult;
                }
        );
    }

    private ApplicationContext applicationContext;

    /**
     * @param applicationContext 获得到的applicationContest,你可以用它getBean
     * @description 获取增强对象
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) 
        throws BeansException {
        this.applicationContext = applicationContext;
    }
}
```

## service

-   服务层包

### UserService.java

-   用户服务层接口

```java
package com.harvey.service;

/**
 * @ClassName: UserService
 * @Author: Harvey Blocks
 * @Description: 接口
 * @Date: 2023/11/09 15:40
 * @Version: 1.0
 */
public interface UserService {
    void show1();
    void show2();
}
```

### impl

-   实现类包

#### UserServiceImpl.java

-   原始对象

```java
package com.harvey.service.impl;

import com.harvey.service.UserService;

/**
 * @ClassName: UserServiceImpl
 * @Author: Harvey Blocks
 * @Description: 目标对象
 * @Date: 2023/11/09 15:42
 * @Version: 1.0
 */
public class UserServiceImpl implements UserService {
    public UserServiceImpl() {
        System.out.println("构造函数");
    }

    @Override
    public String toString() {
        System.out.println("toString");
        return "UserServiceImpl{}";
    }

    @Override
    public void show1() {
        System.out.println("show1");
    }

    @Override
    public void show2() {
        System.out.println("show2");
    }
}
```

## utils

-   工具包

### Log.java

-   日志工具

```java
package com.harvey.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: Log
 * @Author: Harvey Blocks
 * @Description: 日志的类,把自己经常用的几个日志的功能做了一个整合
 * @Date: 2023/11/09 16:03
 * @Version: 1.0
 */
public class Log {

    public static final Logger LOGGER;
    public static final String LOGGER_NAME="Test";
    static {
        LOGGER=LoggerFactory.getLogger(LOGGER_NAME);
    }

    /**
     * @description info方法的简单瞎搞
     * @param o 输入的内容,管你输入啥,统统嘀输出
     */
    public static void info(Object o){
        if(o==null) {
            LOGGER.info("");
        }else if(o instanceof String){
            LOGGER.info((String) o);
        }else{
            LOGGER.info(o.toString(),o);
        }
    }
    /**
     * @description error方法的瞎搞
     * @param o 输入的内容
     * */
    public static void error(Object o){
        if(o==null) {
            LOGGER.error("");
        }else if (o instanceof Throwable){
            Throwable throwable =  (Throwable) o;
            LOGGER.error(throwable.getMessage(),throwable);
        }else if(o instanceof String){
            LOGGER.error((String) o);
        }else{
            LOGGER.error(o.toString(),o);
        }
    }
}
```

### Time.java

-   时间工具

```java
package com.harvey.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @ClassName: Time
 * @Author: Harvey Blocks
 * @Description: 时间类,简化了各色时间类,只能得到当前时间的字符串
 * @Date: 2023/11/09 16:38
 * @Version: 1.0
 */
public class Time {
    public static String nowTime = Time.getTime();
    private static String pattern;
    static {
        // 默认PATTERN
        pattern = "yy-MM-dd HH:mm";
    }
    /**
     * @param pattern 模式
     * @description 设置时间模式
     * */
    public static void setPattern(String pattern){
        if(pattern==null) {
            pattern = "";
        }
        Time.pattern = pattern;
    }

    /**
     * @return 返回格式化之后的时间的字符串
     * */
    public static String getTime() {
        assert pattern != null;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date time = Calendar.getInstance().getTime();
        return sdf.format(time);
    }
}
```

# resources

-   资源目录

## applicationContest.xml

-   Spring配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="userService" class="com.harvey.service.impl.UserServiceImpl"/>
    <bean id="myAdvice" class="com.harvey.advice.MyAdvice"/>

    <bean id="mock" class="com.harvey.processor.MockAopBeanPostProfessor"/>
</beans>
```

## logback.xml

-   日志配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{YY-MM-dd HH:mm} [%highlight(%thread)] %highlight(%-5level) %logger{36} - %highlight(%msg%n)</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <encoder>
            <pattern>%d{YY-MM-DD HH:mm} [%thread] %-5level %logger{36} - %msg%n</pattern>
            <charset>utf-8</charset>
        </encoder>
        <file>log/output.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.FixedWindowRollingPolicy">
            <fileNamePattern>log/output.log.%i</fileNamePattern>
        </rollingPolicy>
        <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
            <MaxFileSize>1MB</MaxFileSize>
        </triggeringPolicy>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

