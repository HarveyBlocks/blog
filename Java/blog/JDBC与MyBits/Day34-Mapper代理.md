# Mapper代理

## 配置Mappper文件

![image-20231016154437216](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/JDBC与MyBits/Day34-Mapper代理/image-20231016154437216.png)

1.  在resource下创建这个**目录**,和对应接口Mapper一致

![image-20231016154752652](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/JDBC与MyBits/Day34-Mapper代理/image-20231016154752652.png)

-   注意是斜杠

2.  编译

3.  打开classes:

    ![image-20231016155532157](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/JDBC与MyBits/Day34-Mapper代理/image-20231016155532157.png)

可以看到,其实是在同一个目录下的.

![image-20231016155604856](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/JDBC与MyBits/Day34-Mapper代理/image-20231016155604856.png)

## 写UerMapper代理接口

```java
package com.harvey.mapper;

import com.harvey.pojo.User;
import java.util.List;

/**
 * @author HarveyBlocks
 * @date 2023/10/16 15:46
 **/
public interface UserMapper {
    //在接口写sql语句对应的对应方法
    List<User> selectUser();
    //注意是List ,因为select *

    //还可以继续放下写

}
```

## 改核心配置文件里对Mapper文件的路径

```xml
<!--对应映射文件-->
<mappers>
    <mapper resource="com/harvey/mapper/UserMapper.xml"/>
</mappers>
```

-   包扫描

    ```xml
    <mappers>
        <mapper resource="com/harvey/mapper/UserMapper.xml"/>
        <!--用Mapper代理,且.xml文件都在一个目录下,就会扫描这个包-->
        <package name= "com/harvey/mapper"/>
    </mappers>
    ```

-   完整文件如下:

    ```xml
    <?xml version="1.0" encoding="UTF-8" ?>

    <!DOCTYPE configuration
            PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
            "https://mybatis.org/dtd/mybatis-3-config.dtd">

    <configuration>

        <environments default="development">

            <environment id="development">

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

        </environments>

        <!--对应映射文件-->
        <mappers>
            <mapper resource="com/harvey/mapper/UserMapper.xml"/>
        </mappers>

    </configuration>
    ```

## 改UserMapper.xml文件里的接口路径

-   namespace改成全类名

```xml
    <mapper namespace="com.harvey.mapper.UserMapper">
```

-   id和接口中的方法名称保持

```xml
<select id="selectUser" resultType="com.harvey.pojo.User">
```

-   完整代码:

    ```xml
    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE mapper
            PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
            "https://mybatis.org/dtd/mybatis-3-mapper.dtd">

    <mapper namespace="com.harvey.mapper.UserMapper">

        <!--id是这一句命令的唯一标识,不能重复-->
        <select id="selectUser" resultType="com.harvey.pojo.User">
            select * from user ;
        </select>
    </mapper>
    ```

