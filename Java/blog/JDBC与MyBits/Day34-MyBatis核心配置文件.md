# mybatis-config.xml

[官网]([mybatis – MyBatis 3 | 配置⁤](https://mybatis.org/mybatis-3/zh/configuration.html))

![image-20231016163640903](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/JDBC与MyBits/Day34-MyBatis核心配置文件/image-20231016163640903.png)

-   直接上mybatis-config.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>

<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "https://mybatis.org/dtd/mybatis-3-config.dtd">

<configuration>

    <!--注意这里几个的顺序,否则会报错qwq-->

    <!--起别名,不用区分大小写,不用带包名,给UserMapper.xml提供了方便-->
    <typeAliases>
        <package name="com.harvey.pojo"/><!--这里与其说是起别名,其实是省略了包名,真想看起一个新名字,去看官网-->
        <package name="com.harvey.mapper"/><!--上面这句有用,下面这句没用,看来代理接口如果要省,去看下面的包扫描-->
    </typeAliases>

    <!--
    envaroments:
    环境配置
    配置数据库连接环境信息
    可以配置多个environment
    通过default属性切换不同发envaroment
    -->
    <environments default="development">

        <!--development 开发化境-->
        <environment id="development">

            <transactionManager type="JDBC"/>

            <!-- 默认的数据库连接池  POOLED -->
            <dataSource type="POOLED">
                <!-- JDBC文件连接 -->
                <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
                <!--数据库连接-->
                <property name="url" value="jdbc:mysql:///company?useSSL=false"/>
                <!--用户名-->
                <property name="username" value="root"/>
                <!--密码-->
                <property name="password" value="123456"/>
            </dataSource>

        </environment>

        <!--test 测试环境-->
        <environment id="test">

            <transactionManager type="JDBC"/>

            <dataSource type="POOLED">
                <!-- JDBC文件连接 -->
                <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
                <!--数据库连接-->
                <property name="url" value="jdbc:mysql:///company?useSSL=false"/>
                <!--用户名-->
                <property name="username" value="root"/>
                <!--密码-->
                <property name="password" value="123456"/>
            </dataSource>

        </environment>

        <!--还有生产库等-->
    </environments>

    <!--对应映射文件-->
    <mappers>
        <package name= "com/harvey/mapper"/>
    </mappers>

</configuration>
```

