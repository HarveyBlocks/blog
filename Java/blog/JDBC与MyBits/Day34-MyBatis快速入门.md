# 快速入门

## mybaits-config.xml

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
        <mapper resource="UserMapper.xml"/>
    </mappers>

</configuration>
```



## UserMapper.xml

-   映射文件

-   xml文件名一般是数据表对应的类的类名+Mapper

```xml
<?xml version="1.0" encoding="UTF-8" ?>


<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "https://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="learnMyBatis"><!--名字随便写-->

    <!--id是这一句命令的唯一标识,不能重复-->
    <select id="selectAll" resultType="com.harvey.pojo.User">
        select * from user ;
    </select>


</mapper>
```

## Mysql建表

```mysql
use company;
insert into user(id, name, age, status, gender)
    value (121,'A',32,'2','男'),
            (122,'B',34,'1','男'),
            (123,'L',32,'2','男'),
            (124,'V',32,'0','女'),
            (125,'D',32,'2','男'),
            (126,'G',34,'1','女'),
            (127,'E',32,'2','男'),
            (128,'S',31,'2','男'),
            (129,'H',33,'1','男')
;
```

## User类

-   属性见上表
-   然后get-set一堆
-   此处略

## Demo

```java
package com.harveyblocks;

import com.harvey.pojo.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Hello world!
 *
 * @author 27970
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        //加载MyBatis 的核心配置文件,获取sqlSessionFactory,格式固定
        String resource = "mybatis-config.xml";//mybatis核心配置文件
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        //获取SQLfSession对象
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //执行sql
        List<User> users = sqlSession.selectList("learnMyBatis" + "." + "selectAll");
                                                //这个,还是硬编码,悲
        //UserMapper文件中的namespace+id


        for (User user :users) {
            System.out.println(user);
        }

        //释放资源
        sqlSession.close();
    }


}
```

## SqlSessionFactory代码优化

###存在问题

```java
String resource = "mybatis-config.xml";//mybatis核心配置文件
InputStream inputStream = Resources.getResourceAsStream(resource);
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
```

-   以上几步都是固定的,每次获取Mapper都要写一遍,合理吗?不合理!
-   SqlSessionFactory,工厂类每次创建工厂对象都会创建连接池,多个连接池会造成巨大的资源消耗!

### 解决方法:工具类抽取

-   静态代码块就是只执行一次的嘛



```java
public class SqlSessionFactoryUtils {
    private static SqlSessionFactory sqlSessionFactory;
    static {
        try {
            String resource = "mybatis-config.xml";//mybatis核心配置文件
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static SqlSessionFactory getFactory(){
        return sqlSessionFactory;
    }
}
```

-   **sqlSession不能放到工具类里**
    -   每个SqlSession都代表一个连接,所用用户公用一个连接就很不合理
    -   会让多个用户多个功能之间产生影响