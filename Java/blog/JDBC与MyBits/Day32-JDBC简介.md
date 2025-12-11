# JDBC简介

-   Java语言操作关系型关系型数据库的一套API
-   全程(Java DataBase Connectivity)
-   **创造了一套规则(接口),用于操作所有不同的关系型数据库**
    -   实现类是关系型接口自己公司自己写的

-   数据库的实现类也叫做**驱动**的.jar包

![image-20231009193046103](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/JDBC与MyBits/Day32-JDBC简介/homeschool-f10a98.png)

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * @author HarveyBlocks
 * @date 2023/10/09 19:22
 **/
public class JDBCDemo {
    public static void main(String[] args) throws Exception {

        //注册驱动,导入包
        Class.forName("com.mysql.cj.jdbc.Driver");

        //获取连接
        String url = "jdbc:mysql://127.0.0.1:3306/company";
        String username = "root";
        String password = "123456";
        //以上这些可以写道属性文件.properties里去

        Connection conn = DriverManager.getConnection(url, username, password);

        //定义sql指令,结尾分号可写可不写
        String sql = "update employee set age = 27 where age = 26;";

        //获取执行Sql对象Statement
        Statement stmt = conn.createStatement();

        //执行sql
        int count = stmt.executeUpdate(sql);
        //返回影响的行数

        System.out.println(count+" hava been updated.");

        //释放资源
        //后开stmt,先释放stmt
        stmt.close();
        //先开conn,后释放conn
        conn.close();
    }
}

```

