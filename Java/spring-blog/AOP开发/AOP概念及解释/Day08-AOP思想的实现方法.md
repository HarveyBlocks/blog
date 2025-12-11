# 实现方法

## 方案

动态代理技术

在运行期间,

对目标对象的方法进行增强,

代理对象同名方法内可以以执行原有逻辑的同时

嵌入执行其他程增强序

或其他对象的方法

-   还记得之前的**日志增强**的Demo吗?

### 实践:用动态代理对Bean进行时间日志增强

```java
public class TimeLogBeanProcessor implements BeanPostProcessor {
    //使用动态代理对Bean进行增强
    // 返回proxy对象,
    // 进而存储到单例池SingletonObjects中
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) 
        throws BeansException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        return Proxy.newProxyInstance(
                bean.getClass().getClassLoader(),
                bean.getClass().getInterfaces(),
                (proxy, method, args) -> {
                    // 1. 输出开始时间
                    Log.info(sdf.format(new Date()) + "开始"+beanName);
                    // 2.执行目标方法
                    Object o = method.invoke(bean, args);
                    // 3.输出结束时间
                    Log.info(sdf.format(new Date()) + "结束"+beanName);
                    return o;
                }
        );
    }
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) 
        throws BeansException {
        return bean;
    }
}
```

![image-20231109152215240](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/AOP开发/AOP概念及解释/Day08-AOP思想的实现方法/image-20231109152215240.png)

### 增强特点:

Proxy代理对象

内部方法的命名和原方法是一样的

类型也是一样的(代理实现同一个接口)

调用A对象的Proxy

## 代码

[代码附件](Day08-AOP思想的实现(代码附件).md)

## 奇怪的现象发生了

```java
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

![image-20231109185843023](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/AOP开发/AOP概念及解释/Day08-AOP思想的实现方法/image-20231109185843023.png)

### 产生这种现象的原因

**我猜测是构造函数**

***错!!!!!!!!!***

**是toString()方法覆写 ** 🫠

![image-20231109190326401](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/AOP开发/AOP概念及解释/Day08-AOP思想的实现方法/image-20231109190326401.png)

