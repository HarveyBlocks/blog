# Spring Task

## 定时任务

-   凌晨备份数据
-   每个月清空日志
-   广告固定时间删除
-   定时清空Cookie

## 实现方式

1.  JDK原生API

    -   `Java.util.Timer`

        定时器类

    -   `java.util.TimerTask`

        定时任务类

    -   缺点: 不能在指定时间使用(不能做闹钟)

2.  Quartz开源框架

    -   功能强大, 能闹钟, 能秒表
    -   使用起来稍显复杂

3.  Spring Task

    -   Spring 3.0之后**自带**了task工具
    -   功能强大, 使用简单方便

## 准备SpringTask

### 引入依赖

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <!--<version>5.3.23</version>需要在3.0以上版本-->
</dependency>
```

-   但是我使用了SpringBoot



```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-task</artifactId>
</dependency>
```

### 基本配置

-   `/WEB-INF/web.xml`

    ```xml
    <?xml version="1.0" encoding="UTF-8" ?>
    
    <!DOCTYPE web-app PUBLIC
            "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
            "http://java.sun.com/dtd/web-app_2_3.dtd" >
    
    <web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
             http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
             version="4.0">
    
        <session-config>
            <session-timeout>30</session-timeout>
        </session-config>
        <context-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:application-context.xml</param-value>
        </context-param>
        <listener>
            <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
        </listener>
    </web-app>
    ```

-   `application-context.xml`

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    
    </beans>
    ```

    

## XML方式使用Spring Task

### 引入命名空间

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:task="http://www.springframework.org/schema/task"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/task ">
    
</beans>
```




### 业务

```java
public class TaskServiceImpl implements TaskService {
    public static final Logger LOG = LoggerFactory.getLogger("Service");
    @Override
    public void firstTask() {
        LOG.info("这是第一个定时");
    }

    @Override
    public void secondTask() {
        LOG.info("这是第二个定时");
    }
}
```





### 配置定时任务



```xml
<bean id="taskService" class="com.harvey.spring.task.service.impl.TaskServiceImpl"/>
<!--配置定时规则-->
<task:scheduled-tasks>
    <!--可以配置多个定时任务-->
    <task:scheduled ref="taskService" method="firstTask"/>
    <task:scheduled ref="taskService" method="secondTask"/>
</task:scheduled-tasks>
```



### 配置定时规则

```xml
<task:scheduled ref="taskService" method="firstTask" initial-delay="1000" fixed-delay="1000"/>
<task:scheduled ref="taskService" method="secondTask" initial-delay="500" fixed-delay="1000"/>
```

-   initial-delay=""
    -    Tomcat启动完毕后, 等多少ms**启动定时任务**
-   fixed-delay=""
    -   设置每**隔多少ms**,就运行一次定时任务

#### Cron表达式

>   用来定义复杂的定时规则

-   由七部分组成
-   每个部分用空格隔开

##### 组成部分表

| 组成部分 | 含义         | 取值范围                 |
| -------- | ------------ | ------------------------ |
| 第一部分 | Seconds      | 0-59                     |
| 第二部分 | Minutes      | 0-59                     |
| 第三部分 | Hours        | 0-23                     |
| 第四部分 | Day-of-Month | 1-31                     |
| 第五部分 | Month        | 0-1 or JAN-DEC           |
| 第六部分 | Day-of-Week  | 1(SUN)-7(SAT) or SUN-SAT |
| 第七部分 | Year(可选)   | 1970-2099                |

##### 占位符表

| 符号 | 含义                                                         |
| ---- | ------------------------------------------------------------ |
| *?*  | 不确定的值, 任意一天                                         |
| *\** | 整个时间段(24h,30/31天)                                      |
| *,*  | 设置多个值, ***"26,29,33"*** 表示在26,29,33各自运行一次任务  |
| *-*  | 设置取值范围, ***"5-20"***表示在5-20(min)每min运行一次       |
| */*  | 设置频率或间隔,如**"1/15"**表示从1分开始.每隔15分钟运行一次  |
| *L*  | 用于每月或每周, 表示每月的最后一天,或每个月的最后星期几. ***"6L"*** 表示每月的最后一个星期五 |
| *W*  | 表示离给定日期最近的工作日, 例如***"15W"***放在每月(day-of-month)表示"离本月15日最近的工作日" |
| *#*  | 表示该月的第几个周几,例如***"6#3"***表示该月的第三个周五     |

![image-20240117122047590](../assets/Day01-SpringTask%E6%A6%82%E8%BF%B0/image-20240117122047590.png)

```xml
<task:scheduled ref="taskService" method="secondTask" cron="*/5 * * * * ?"/>
```

## 注解方式使用Spring Task

### 在业务方法上添加注解

```java
@Scheduled(initialDelay = 500,fixedDelay = 3000)
```



```java
@Service
public class TaskServiceImpl implements TaskService {
    public static final Logger LOG = LoggerFactory.getLogger("Service");
    @Override
    @Scheduled(initialDelay = 1000,fixedDelay = 1000)
    public void firstTask() {
        LOG.info("这是第一个定时");
    }

    @Override
    @Scheduled(initialDelay = 500,fixedDelay = 3000)
    public void secondTask() {
        LOG.error("这是第二个定时");
    }
}
```

### 开启注解支持

```java
@SpringBootApplication
@EnableScheduling
public class TaskApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(TaskApplication.class, args);
    }

}
```

### 测试运行

![image-20240117123609275](../assets/Day01-SpringTask%E6%A6%82%E8%BF%B0/image-20240117123609275.png)

### 自行开启关闭

```java
@Service
public class TaskServiceImpl implements TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class.getSimpleName());

    // Spring Framework
    private static final ThreadPoolTaskScheduler THREAD_POOL_TASK_SCHEDULER = new ThreadPoolTaskScheduler();

    static {
        THREAD_POOL_TASK_SCHEDULER.initialize();
        logger.info("初始化线程池...");
    }


    public void start() {
        java.util.Date startTime = new java.util.Date();
        ScheduledFuture<?> scheduledFuture = THREAD_POOL_TASK_SCHEDULER.schedule(() -> {
            while (true) {
                try {
                    logger.info("执行定时任务");
                    Thread.sleep(1000*1);
                } catch (InterruptedException e) {
                    logger.error("任务被强制终止");
                    return;
                }
            }
        }, startTime);
        logger.info("3");
        try {
            Thread.sleep(1000*20);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.info("4");
        scheduledFuture.cancel(true);// true表示任务被强制终止
    }
}
```

