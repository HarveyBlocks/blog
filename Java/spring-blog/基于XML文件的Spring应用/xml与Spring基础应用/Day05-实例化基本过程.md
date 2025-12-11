# 实例化基本流程

## 流程

-   **当然,这里是用ApplicationContext实例化且不用lazy-init**

### Step1-封装信息

![image-20231102153031307](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/基于XML文件的Spring应用/xml与Spring基础应用/Day05-实例化基本过程/image-20231102153031307.png)

#### 注意

>   封装的是Bean的**信息**而不是对象(例如UserService)本身

### Step2-信息存入Map

![image-20231102153249958](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/基于XML文件的Spring应用/xml与Spring基础应用/Day05-实例化基本过程/image-20231102153249958.png)

![image-20231102154709525](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/基于XML文件的Spring应用/xml与Spring基础应用/Day05-实例化基本过程/image-20231102154709525.png)

### Step3-遍历

![image-20231102153308279](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/基于XML文件的Spring应用/xml与Spring基础应用/Day05-实例化基本过程/image-20231102153308279.png)

### Step4-反射

![image-20231102153406720](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/基于XML文件的Spring应用/xml与Spring基础应用/Day05-实例化基本过程/image-20231102153406720.png)

### Step5-对象存入Map

![image-20231102153450138](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/基于XML文件的Spring应用/xml与Spring基础应用/Day05-实例化基本过程/image-20231102153450138.png)

>   单例池 

![image-20231102154736756](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/基于XML文件的Spring应用/xml与Spring基础应用/Day05-实例化基本过程/image-20231102154736756.png)

### Step6-调用getBean("id")

![image-20231102153549220](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/基于XML文件的Spring应用/xml与Spring基础应用/Day05-实例化基本过程/image-20231102153549220.png) 

## 线程

