# Spring-Framework

-   核心容器 Core Container
    -   Beans
    -   Core
    -   Context
    -   Expression
-   AOP和设配支持 
    -   AOP
    -   Aspects
    -   Instruments
-   数据访问与集成 Data Access/Integration
    -   JDBC
    -   OXM
    -   JMS
    -   Transactions
-   Web组件
    -   WebSocket
    -   WebMVC
    -   Web
    -   WebFlux
-   通信报文
    -   Message
-   集成测试
    -   Test

### Application-Context.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="userMapper" class="com.harvey.dp.spring.demo.UserMapper"/>
    <bean id="userService" class="com.harvey.dp.spring.demo.UserServiceImpl">
        <property name="userMapper" ref="userMapper"/>
    </bean>
    <bean id="userController" class="com.harvey.dp.spring.demo.UserController">
        <property name="userService" ref="userService"/>
    </bean>
</beans>
```

