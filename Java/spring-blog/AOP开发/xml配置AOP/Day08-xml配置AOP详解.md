# 切点表达式的配置方式

-   配置可以配到外面,也可以里面

    ```xml
    <aop:config>
        <aop:pointcut id="userServiceShow1" 
                      expression="execution(void com.harvey.service.impl.UserServiceImpl.show1())"/>
        <aop:aspect ref="myAdvice">
            <aop:before method="before" pointcut-ref="userServiceShow1"/>
        </aop:aspect>
        <aop:aspect ref="myAdvice">
            <aop:pointcut id="userServiceToString" 
                          expression="execution(String com.harvey.service.impl.UserServiceImpl.toString())"/>
            <aop:before method="before" pointcut-ref = "userServiceToString"/>
            <aop:before method="before" 
                        pointcut="execution(String com.harvey.service.impl.UserServiceImpl.toString())"/>
        </aop:aspect>
    </aop:config>
    ```

    配在里外都一样,原理和作用域一样

    但**为了效率,要合理地决定这些配置的位置**

-   切点表达式可以配多个

## 切点表达式配置语法(之一)

```xml
execution([访问修饰符] 返回值类型 包名.类名.方法(参数列表))
```

-   访问修饰符可以省略不写
-   **返回值类型,** ***某一级*** **包名,类名,方法名,可以用\*标识任意**
-   报名与类名之间使用单点 **.** 标识该把奥下的类,**使用双点 .. 表示该包及其子包下的类**
-   参数列表可以**使用双点 .. 表示任意参数**
-   类可以是接口***(?)***

```properties
pointcut="execution(public * com.harvey.service..UserServiceImpl.*(..))"
```

-   小练习![image-20231110110720348](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/AOP开发/xml配置AOP/Day08-xml配置AOP详解/image-20231110110720348.png)
-   织入可以叠加

![image-20231110110435402](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/AOP开发/xml配置AOP/Day08-xml配置AOP详解/image-20231110110435402.png)

```xml
<aop:before method="before" pointcut="execution(* *..*.*(..))"/>
<aop:before method="before" pointcut="execution(* *..*.*(..))"/>
```

# 通知的类型(五种)

![image-20231110110900189](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/AOP开发/xml配置AOP/Day08-xml配置AOP详解/image-20231110110900189.png)

## 测试

### 修改目标方法

```java
@Override
public void show1(Object s) {
    if(s==null){
        System.out.println("show1:null");
        throw new NullPointerException();
    }else {
        System.out.println("show1:"+s);
    }
}
```

-   为了测试,给show1()做了小修改,使其能抛出异常

### 修改通知类

```java
public class MyAdvice {
    /**
     * @description 前置通知,目标方法执行之前执行
     * */
    public void before() {
        System.out.println("before");
    }

    /**
     * @description 后置通知,目标方法之后执行,目标方法异常时,不再执行
     * */
    public void afterReturning() {
        System.out.println("after-returning");
    }
    /**
     * @description 环绕通知,目标方法执行前后执行,目标方法异常时,不再执行
     * @param point 正在执行的连接点
     * @return 目标方法可能的返回值
     * @throws Throwable point可能产生的异常
     * */
    public Object around(ProceedingJoinPoint point) throws Throwable {
        System.out.println("环绕前的通知");
        Object result  //目标方法可能的返回值
                = point.proceed();//执行目标方法
        System.out.println("环绕后的通知");
        return result;
    }

    /**
     * @description 异常通知,目标方法爬抛出异常时执行
     * */
    public void afterThrowing(){
        System.out.println("afterThrowing");
    }

    /**
     * @description 最终通知,不管方法是否有异常,最终都会执行
     * */
    public void after() {
        System.out.println("after");
    }
}
```

### 修改xml配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd

       http://www.springframework.org/schema/aop
       http://www.springframework.org/schema/aop/spring-aop.xsd">

    <bean id="userService" class="com.harvey.service.impl.UserServiceImpl"/>

    <bean id="myAdvice" class="com.harvey.advice.MyAdvice"/>

    <aop:config>
        <aop:pointcut id="myPrintCutShow1" expression=
                "execution(void com.harvey.service.impl.UserServiceImpl.show1(Object))"/>
        <!--参数改变啦!!!!!!!!!!!!!!!!!!!!!要写个参数!否则你Ctrl+左键进得去,却对应不上方法的,也不会报错-->

        <aop:aspect ref="myAdvice">
            <aop:before method="before" pointcut-ref="myPrintCutShow1"/>
            <aop:after-returning method="afterReturning" pointcut-ref="myPrintCutShow1"/>
            <aop:around method="around" pointcut-ref="myPrintCutShow1"/>
            <aop:after-throwing method="afterThrowing" pointcut-ref="myPrintCutShow1"/>
            <aop:after method="after" pointcut-ref="myPrintCutShow1"/>
        </aop:aspect>

    </aop:config>
</beans>
```

### 测试结果(节选)

```Text
-------------show1()--------------
------12-----
before
环绕前的通知
show1:12
after
环绕后的通知
after-returning
------null-----
before
环绕前的通知
show1:null
after
afterThrowing
Exception in thread "main" java.lang.NullPointerException
	at com.harvey.service.impl.UserServiceImpl.show1(UserServiceImpl.java:27)
	at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103)
	at java.base/java.lang.reflect.Method.invoke(Method.java:580)

```

## 其他

**如果有抛出异常,但被around catch住了,after-throwing又该如何应对?**

### around方法

```java
public Object around(ProceedingJoinPoint point)  {
    System.out.println("环绕前的通知");
    Object result  //目标方法可能的返回值
            = null;//执行目标方法
    try {
        result = point.proceed();
    } catch (Throwable e) {
        System.out.println(e.getMessage()+" point exception");
    }
    System.out.println("环绕后的通知");
    return result;
}
```

### 测试结果

```text
-----null------
before
环绕前的通知
show1:null
after
afterThrowing
null point exception
环绕后的通知
after-returning
```

-   会走,会在环绕后通知前走

## 连接点

```java
public Object around(ProceedingJoinPoint point) throws  Throwable{
    System.out.println("环绕前的通知");
    Object result = point.proceed();
    System.out.println("环绕后的通知");
    return result;
}
```

![image-20231110131212861](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/AOP开发/xml配置AOP/Day08-xml配置AOP详解/image-20231110131212861.png)

### 小测试

-   这么写可以,但很不人道,也没有意义

	```java
public Object around()  {
    System.out.println("环绕前的通知:");

    System.out.println("环绕后的通知");
    return null;
}
	```

-   两个参数,不行`IllegalArgumentException`

	```java
public Object around(ProceedingJoinPoint point,JoinPoint joinPoint)  {return null;}
	```

-   两个重载方法,不行`IllegalArgumentException`

  ```java
  public Object around(ProceedingJoinPoint point)  {
    System.out.println("环绕前的通知:");
    Object result  //目标方法可能的返回值
            = null;//执行目标方法
    try {
        result = point.proceed();
    } catch (Throwable e) {
        System.out.println(e.getMessage()+" point exception");
    }
    System.out.println("环绕后的通知");
    return result;
  }
  public void around(JoinPoint joinPoint){
      System.out.println(joinPoint.getTarget());
  }
  ```

  但是

  ```java
  public Object around(ProceedingJoinPoint point)  {
      System.out.println("环绕前的通知:");
      Object result  //目标方法可能的返回值
              = null;//执行目标方法
      try {
          result = point.proceed();
      } catch (Throwable e) {
          System.out.println(e.getMessage()+" point exception");
      }
      System.out.println("环绕后的通知");
      return result;
  }

  public void around(){
      System.out.println("yes");
  }
  ```

  一个有参一个无参的行,运行了无参的(离谱)

-   不是around方法也能加参数;
  ```java
  public void before(JoinPoint joinPoint) {
      System.out.println("before "+joinPoint.getTarget()+" before");
  }
  ```

### JoinPoint

>    动态获得AOP配置信息

```java
/**
 * @description 前置通知,目标方法执行之前执行
 * @param joinPoint 动态获得AOP配置信息
 * */
public void before(JoinPoint joinPoint) {
    System.out.println("before");
    System.out.println("当前目标对象:"+joinPoint.getTarget());
    System.out.println("当前(正在执行的[和配的不一样])切点表达式:"+joinPoint.getStaticPart());
    System.out.println("before");
}
```

```txt
before
当前目标对象:com.harvey.service.impl.UserServiceImpl@f107c50
当前(正在执行的[和配的不一样])切点表达式:execution(void com.harvey.service.UserService.show1(Object))
before
```

### ProceedingJoinPoint

>   动态获取被增强的方法

```java
public Object around(ProceedingJoinPoint point) throws  Throwable{
    System.out.println("环绕前的通知");
    Object result = point.proceed();
    System.out.println("环绕后的通知");
    return result;
}
```

-   给around之外的配,会狠狠地报错

![image-20231110134859631](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/AOP开发/xml配置AOP/Day08-xml配置AOP详解/image-20231110134859631.png)

### Throwable

>得到目标对象的方法抛出的异常
>
>需要进行响应的配置

![image-20231110134324744](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/AOP开发/xml配置AOP/Day08-xml配置AOP详解/image-20231110134324744.png)

-   不配置,就报错

这个throwing的配置只有after-throwing能配:

```xml
<aop:after-throwing method="afterThrowing" 
                    pointcut-ref="myPrintCutShow1" 
                    throwing="throwable"/>
							<!--对应    ↓-->
```

```java
public void afterThrowing(Throwable throwable){
    System.out.println("afterThrowing");
    throwable.printStackTrace();
    System.out.println("afterThrowing");
}
```

![image-20231110134558895](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/AOP开发/xml配置AOP/Day08-xml配置AOP详解/image-20231110134558895.png)

-   报错地心满意足
-   它的生命周期很怪

```text
-----------
before
当前目标对象:com.harvey.service.impl.UserServiceImpl@35645047
当前(正在执行的[和配的不一样])切点表达式:execution(void com.harvey.service.UserService.show1(Object))
before
环绕前的通知:
show1:null
after
afterThrowing
afterThrowing
环绕后的通知
after-returning
afterThrowingjava.lang.NullPointerException
around:java.lang.NullPointerException
```

## \<advisor\>配置切面

### AOP的xml配置的两种方式

1.  \<advisor\>配置切面
2.  \<aspect\>配置切面(我们之前讲的都是这种)

### Advice接口

>   Spring定义了一个Advice接口,实现了该接口的类都可以作为通知类出现

#### 康康源码

![image-20231110141411743](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/AOP开发/xml配置AOP/Day08-xml配置AOP详解/image-20231110141411743.png)

>   啥都没有的接口------标示(Serializable)接口

实现它靠其子接口

### 实现Advice的子接口

```java
public class MyAdvice2 implements MethodBeforeAdvice, AfterReturningAdvice {

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        System.out.println("MyAdvice2------------------------afterReturning");
    }

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("MyAdvice2------------------------before");
    }
}
```

### xml文件

```java
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd

       http://www.springframework.org/schema/aop
       http://www.springframework.org/schema/aop/spring-aop.xsd">

    <bean id="userService" class="com.harvey.service.impl.UserServiceImpl"/>

    <bean id="myAdvice2" class="com.harvey.advice.MyAdvice2"/>

    <aop:config>
        <aop:pointcut id="myPrintCutShow1" expression=
                "execution(void com.harvey.service.impl.UserServiceImpl.show1(Object))"/>
        <aop:advisor advice-ref="myAdvice2" pointcut-ref="myPrintCutShow1"/>

    </aop:config>
</beans>
```

### 结果

![image-20231110142335511](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/AOP开发/xml配置AOP/Day08-xml配置AOP详解/image-20231110142335511.png)

### 详解MethodInterceptor

>   类似于around()

```java
public class MyAdvice2 implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("换绕前");
        //执行目标方法
        Object result = invocation.getMethod().invoke(
                invocation.getThis(), //获取当前对象
                invocation.getArguments()//获取参数
        );
        System.out.println("换绕后");
        return result;
    }
}
```

#### 结果

![image-20231110143108353](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/AOP开发/xml配置AOP/Day08-xml配置AOP详解/image-20231110143108353.png)

### 与\<aspect\>的区别

-   用接口就没有\<aspect\>灵活了

    \<aspect>可以这么玩儿:

    ```xml
    <aop:aspect ref="myAdvice">
        <aop:before method="after" pointcut-ref="myPrintCutShow1"/>
    </aop:aspect>
    ```

    随便取反人类的名字

-   可配置切面数不同\<aspect\>想配几个就配几个

    -   这个我解释一波:

        第二个\<aspect\>里可以只要第一个\<aspect\>里的其中一个增强方法,例如around()

        但是,第二个\<advisor>要么不配,你想要抽一个方法,你就要再去建一个类,里面只写需要的方法

-   使用环境不同

    -   需要随意搭配时使用\<aspect\>
    -   **通知类型单一,切面单一**的情况下可以使用\<advisor>
    -   **通知类型固定**,不用人为指定通知类型时,可以使用\<advisor>进行配置
        -   **通知类型固定**:指Jar包中用接口配置好了
        -   例如Spring事物控制的配置

