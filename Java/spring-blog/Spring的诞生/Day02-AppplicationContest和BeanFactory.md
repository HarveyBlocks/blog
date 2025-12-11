# BeanFactory和ApplicationContext的关系

![image-20231030015316068](../../assets/Day02-AppplicationContest和BeanFactory/image-20231030015316068.png)

![image-20231030015614966](../../assets/Day02-AppplicationContest和BeanFactory/image-20231030015614966.png)

对于4)

-   BeanFactory

	```java
	@Test
	public void testFactory() {
	    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
	reader.loadBeanDefinitions("beans.xml");
  	UserService userService =(UserService) beanFactory.getBean("userService");

	    UserDao userDao =(UserDao) beanFactory.getBean("userDao");//	这一步创建对象
	}
	```

-   对于ApplicationContest

    ```java
    @Test
    public void test(){
        ApplicationContext applicationContext =
            new ClassPathXmlApplicationContext("beans.xml");//这一步就完成创建
    
    
        UserService userService = (UserService) applicationContext.getBean("userService");
        TestLogger.LOGGER.info(""+userService);
    }
    ```

-   可以在UserService的无参构造里加一句打印测试上面的事情

![image-20231030020726370](../../assets/Day02-AppplicationContest和BeanFactory/image-20231030020726370.png)

![image-20231030020739480](../../assets/Day02-AppplicationContest和BeanFactory/image-20231030020739480.png)

-   ApplicationContext内部维护着一个BeanFactory

# BeanFactory的继承体系



## DefaultListableBeanFactory

-   看上面张图的第二个框灰色的字
-   ApplicationContext里维护的就是它

```java
private final Map<String, BeanDefinition> beanDefinitionMap;
```

-   BeanDefinition
    -   Bean定义对象,对xml文件标签里的内容进行解析,并封装到一个实体类里





## ApplicationContext的继承体系

![image-20231030021550777](../../assets/Day02-AppplicationContest和BeanFactory/image-20231030021550777.png)

-   XMl配置方案
-   注解(Annotation)配置方案

![image-20231030022015315](../../assets/Day02-AppplicationContest和BeanFactory/image-20231030022015315.png)

-   配置文件放在D盘了,你用FileSystemXMLApplicationContext
    -   翻译:一辈子用不到几回

