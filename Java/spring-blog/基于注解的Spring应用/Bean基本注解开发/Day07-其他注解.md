# @Primary提高Bean的优先级

>   用于标注**相同类型**的Bean优先被使用

-   与@Componect和@Bean一起被使用,标注该Bean的优先级更高,

-   在**通过类型**获取Bean或通过@Autowired**根据类型**注入时,会选择优先级更高的

```java
 	@Autowired
    @Primary
    public void xxx(UserDao userDao3){
//        System.out.println("xxx:"+userDao3);
    }
```

# @Profile限定范围

```xml
<beans profile="test">
```

切环境的

@Profile标注在**类或方法**上,标注当前的Bean**从属于哪个环境**,只有激活了当前的环境.被被标注的Bean才能被注册到S[ring容器里,**不指定环境的Bean,任何环境都能注册到Spring容器里去**

```java
@Component
@Primary
@Profile("test")
public class UserServiceImpl implements UserService {
	...
}
```

-   "不能注册到Spring环境"是以`NoSuchBeanDefinitionException`的形式报错给你的

![image-20231107132648728](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/基于注解的Spring应用/Bean基本注解开发/Day07-其他注解/image-20231107132648728.png)

![image-20231107132354465](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/基于注解的Spring应用/Bean基本注解开发/Day07-其他注解/image-20231107132354465.png)

-   翻翻以前的笔记

```java
System.setProperty("spring.profiles.active", "test");
```

