# ApplicationContesxt

>   应用上下文

Spring的容器,内部封装BeanFactory,比BeanFactory功能丰富强大

-   xml文件习惯写成applicationContest.xml

-   使用多态

```java
package org.springframework.context.support;

import ...

public class ClassPathXmlApplicationContext 
    extends AbstractXmlApplicationContext {
	...
}
```

这个是可以用的

## java的代码实现

```java
@Test
public void test(){
    //创建ApplicationContest,加载配置文件,实例化容器
    ApplicationContext applicationContext =
        new ClassPathXmlApplicationContext("beans.xml");
    //用bean.xml是因为不想重新写一个了,反正一模一样

    UserService userService = (UserService) applicationContext.getBean("userService");
    TestLogger.LOGGER.info(""+userService);
}
```

```log
BeanFactory去调用该方法获取userDao设置到此处com.harvey.Impl.UserDaoImpl@56a6d5a6
23-10-30 01:49 [main] INFO  TestSpring - com.harvey.Impl.UserServiceImpl@5ccddd20
```

