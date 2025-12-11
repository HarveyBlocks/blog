# Spring的get方法

![image-20231101001118392](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/基于XML文件的Spring应用/xml与Spring基础应用/Day03-get方法/image-20231101001118392.png)

![image-20231101001147032](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/基于XML文件的Spring应用/xml与Spring基础应用/Day03-get方法/image-20231101001147032.png)

-   你已经写过了

    ```java
    UserService userService = (UserService) applicationContext.getBean("userService");
    ```
    ```java
	UserService userService = (UserService) beanFactory.getBean("myBeanFactory");
	```

-   你还是没记住

