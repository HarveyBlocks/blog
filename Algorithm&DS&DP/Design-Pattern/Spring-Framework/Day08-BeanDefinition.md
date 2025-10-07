# BeanDefinition

## BeanDefinition

```xml
<bean id="userMapper" class="com.harvey.dp.spring.demo.UserMapper"/>
<bean id="userService" class="com.harvey.dp.spring.demo.UserServiceImpl">
    <property name="userMapper" ref="userMapper"/>
</bean>
<bean id="userController" class="com.harvey.dp.spring.demo.UserController" scope="singleton">
    <property name="userService" ref="userService"/>
</bean>
```

AOP使用代理

选择JDK代理还是CgLib代理采用策略这模式

