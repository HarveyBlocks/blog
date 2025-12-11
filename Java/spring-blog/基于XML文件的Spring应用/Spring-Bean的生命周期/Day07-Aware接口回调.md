# Aware接口

>   框架具有高度封装性,我们接触到的一般都是业务代码,一个底层功能API不能轻易地获取到
>
>   但如果是在想要这个对象,就可以使用框架提供的Aware接口,让框架帮我们注入该对象

-   写程序主打一个有求必应

## 常用是Aware接口

![image-20231104142314254](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/基于XML文件的Spring应用/Spring-Bean的生命周期/Day07-Aware接口回调/image-20231104142314254.png)

## 使用

-   接口UserService,继承上边的Aware接口

	```java
public interface UserService extends InitializingBean, ApplicationContextAware, BeanNameAware, BeanFactoryAware {
    @Override
    void afterPropertiesSet() throws Exception;

    void setUserDao(UserDao userDao);
    void destroy114514();
    void show();
    void init114514();
}
	```

-   实现类

	```java
public class UserServiceImpl implements UserService{
    @Override
    public void setApplicationContext(
        ApplicationContext applicationContext) 
        throws BeansException {
        Log.info("ApplicationContextAware:"+applicationContext.getClass().getSimpleName());
    }

    @Override
    public void setBeanFactory(
        BeanFactory beanFactory) 
        throws BeansException {
        Log.info("BeanFactoryAware:"+beanFactory.getClass().getSimpleName());
    }

    @Override
    public void setBeanName(String s) {
        Log.info("BeanNameAware:"+s);
    }
 	...   
}
	```

-   输出结果

    ![image-20231104144431855](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/基于XML文件的Spring应用/Spring-Bean的生命周期/Day07-Aware接口回调/image-20231104144431855.png)

