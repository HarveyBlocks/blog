# 上手

## xml文件配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd
       http://www.springframework.org/schema/aop
       http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!--包扫描-->
    <context:component-scan base-package="com.harvey"/>
    <!--自动生成通知-->
    <aop:aspectj-autoproxy/>

</beans>
```

## 目标对象

```java
@Service("userService")
public class UserServiceImpl implements UserService {...}
```

## 增强对象

```java
package com.harvey.advice;

import ...

@Component
@Aspect
public class MyAdvice {
    public static final String 
        EXECUTION = "execution(* com.harvey.service.impl.UserServiceImpl.*(..))";

    @Before(EXECUTION)
    public void before() {
        System.out.println("before");
    }
    @AfterReturning(EXECUTION)
    public void afterReturning() {
        System.out.println("after-returning");
    }
    @Around(EXECUTION)
    public Object around(ProceedingJoinPoint point)  {
        System.out.println("环绕前的通知:");
        Object result  //目标方法可能的返回值
                = null;//执行目标方法
        try {
            result = point.proceed();
        } catch (Throwable e) {
            System.err.println("around:"+e);
        }
        System.out.println("环绕后的通知");
        return result;
    }
    @AfterThrowing(value = EXECUTION,throwing = "throwable")
    public void afterThrowing(Throwable throwable){
        System.out.println("afterThrowing");
        System.err.println("afterThrowing"+throwable);
        System.out.println("afterThrowing");

    }
    @After(EXECUTION)
    public void after(JoinPoint point) {
        Object target = point.getTarget();
        System.out.println(point.getStaticPart());

        System.out.println("after");
    }
}
```

## 注解配置切点表达式

```java
public class MyAdvice {
    public static final String 
        EXECUTION = "execution(* com.harvey.service.impl.UserServiceImpl.*(..))";

    /**
     * @description 切点表达式的切取.真是一点面子也不给啊
     * */
    @Pointcut(EXECUTION)
    public void myPointcut(){
        //不会被执行(悲)
        System.out.println("你好呀??????????????????????");
    }

    /**
     * @description 前置通知,目标方法执行之前执行
     * */
    @Before("MyAdvice.myPointcut()")//这个注解..
    //这时候你可以杠一杠:这不是静态方法,你怎么这么写呢?!也挺快乐的
    public void before() {
        System.out.println("before");
    }
    ...

}
```

>   注解和静态常量哪种更合理取决于具体的情况。 
>
>   使用注解作为切点表达式的好处是可以将切点定义与代码逻辑分离，便于管理和维护。通过在方法或类上添加注解，可以直接指定切点的位置。然而，使用注解的局限性在于，只能在代码中使用预定义的注解，并且难以进行动态配置。
>
>   相比之下，使用静态常量作为切点表达式更加灵活，可以根据需要进行动态配置。通过在配置文件或代码中定义常量，可以根据具体的切点规则进行灵活的配置。此外，使用静态常量还可以方便地进行代码的重用和管理。
>
>   **如果需要动态配置或更灵活的切点规则，可以选择静态常量；如果切点规则固定且易于管理，可以选择注解。**
>
>   ​																																								-------Chat-GPT

# 老规矩:核心配置类

```xml
<!--包扫描-->
<context:component-scan base-package="com.harvey"/>
<!--自动生成通知-->
<aop:aspectj-autoproxy/>
```

-   干掉他们!

-   需要完成的两个任务:

    1.  包扫描
    2.  自动生成通知

    ```java
    @Configuration
    @ComponentScan("com.harvey")
    @EnableAspectJAutoProxy()
    public class SpringConfig {
    }
    ```

    结束

### 改一改测试类,还能用

```java
public class App {
    public static void main(String[] args) {
        ApplicationContext app = new AnnotationConfigApplicationContext(SpringConfig.class);
        //改↑一句就好了啦
        System.out.println("================userService1==================");
        UserService userService = (UserService) app.getBean("userService");
        System.out.println("-------------测试开始----------------");
        System.out.println(userService);
        System.out.println("-------------show1()--------------");
        userService.show1(12);
        System.out.println("-----------");
        userService.show1(null);
    }
}
```

![image-20231110193611810](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/AOP开发/注解配置AOP/Day09-基本使用/image-20231110193611810.png)

#### 再见了,最后的XML战士🥺

