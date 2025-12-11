```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{YY-MM-dd HH:mm} [%highlight(%thread)] %highlight(%-5level) %logger{36} - %highlight(%msg%n)</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <encoder>
            <pattern>%d{YY-MM-DD HH:mm} [%thread] %-5level %logger{36} - %msg%n</pattern>
            <charset>utf-8</charset>
        </encoder>
        <file>log/output.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.FixedWindowRollingPolicy">
            <fileNamePattern>log/output.log.%i</fileNamePattern>
        </rollingPolicy>
        <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
            <MaxFileSize>1MB</MaxFileSize>
        </triggeringPolicy>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

-   logback.xml

# 日志技术

## 日志的作用

- 查看哪些数据在什么时候被谁操作
- 分析用户浏览系统具体情况,挖掘用户喜好
- 系统再发布或上线后出现bug,崩溃了,可用于分析,定位bug
- 不会有人还想用print()记录日志吧

## 日志的好处

- 可以将系统执行的信息方便地记录到指定位置(控制台,文件,数据库)
- 可以随时以开关的形式控制日志的启停,无需侵入源代码中修改(针对print要侵入源代码)

## 日志技术的体系结构

### 日志框架

> Java或第三方公司已经做好的实现代码,后来者可以直接拿去使用

- JUI(java.util.loggiing)
- Log4j
- **logback**
- 其他

### 日志接口

> 设计日志框架的一套标准.日志框架需要实现这些接口

- Commons Logging(JCL)
  - JUI(java.util.loggiing)
- Simple Logging Facade for Java (SLF4J)
  - Log4j
  - **logback**

## Logback

- logback-core
  - 基础模块
- logback -classic
  - 同化了Log4j
  - 实现了SLF4J API
- logback-access
  - 提供HTTP访问日志功能

### 使用Logback

> 需求:记录系统运行信息

- 前期准备:
  - slf4j-api日志接口
  - logback-core
  - logback-class

```java
package learnSpecialDoc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author HarveyBlocks
 * @date 2023/10/04 22:28
 **/
public class LearnLog {
    public static final Logger LOGGER = LoggerFactory.getLogger("LearnLog");
    public static void main(String[] args) {
        Double ans = null;
        try {
            LOGGER.info("除法开始执行");
            // info表示重要
            // 日志会记录到文件,控制台
            // 是由核心控制文件控制的
            ans = divide(10, 0.0);

            LOGGER.info("除法执行成功");
            //System.out.println(ans);没必要了
        }catch (Exception e){
            LOGGER.error("除零错误,除法执行失败");//规范且支持后面的使用
        }
        LOGGER.info("ans = " + ans);
    }
    public static Double divide(double a,double b) throws Exception {
        LOGGER.debug("参数a:"+a);//程序的执行流程
        LOGGER.debug("参数b:"+b);//程序的执行流程
        if (b<1e-15){
            throw new Exception("divide 0");
        }
        Double c = a / b;
        LOGGER.debug("return "+c);
        return c;
    }
}
```

一定要配置logback.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>

<!-- 配置文件修改时重新加载，默认true -->
<configuration scan="true">

    <!--定义日志文件的存储地址 勿在 LogBack 的配置中使用相对路径-->
    <property name="CATALINA_BASE" value="**/logs"></property>

    <!-- 控制台输出 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <target>System.out</target>
        <encoder charset="UTF-8">
            <!-- 输出日志记录格式 -->
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 第一个文件输出,每天产生一个文件 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <encoder charset="UTF-8">
            <!-- 输出日志记录格式 -->
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            <charset>utf-8</charset>
        </encoder>

        <file>C:\Users\27970\Desktop\aa.log</file>

        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 输出文件路径+文件名 -->
            <fileNamePattern>C:\Users\27970\Desktop\aa-%i-%d{yyyy-MM-dd}-.log.gz</fileNamePattern>
            <!-- 保存30天的日志 -->
            <maxHistory>30</maxHistory>
            <maxFileSize>1MB</maxFileSize>
        </rollingPolicy>
    </appender>
    <!-- 设置日志输出级别 level=(ALL,OFF,ERROR,INFO)-->
    <root level="ALL">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>

</configuration>
```

- 越往下越高

![image-20231005010410191](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java特殊文件/Day30-日志技术/image-20231005010410191.png)

为**OFF、FATAL(严重错误)、ERROR、WARN、INFO、DEBUG、ALL**或者您定义的级别。Log4j建议只使用四个级别，优先级 从高到低分别是 **ERROR、WARN、INFO、DEBUG**。 

- 只有日志级别**高于等于核心配置文件配置的日志级别**,才会被记录

