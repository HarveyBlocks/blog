# Json处理器

-   其实,数据库里有一种数据类型, 叫做JSON(啊?)
-   可以直接放到Java的字符串里字符串,但是也不方便~

![image-20231209223715985](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-boot/mybatis-plus/Day06-Json处理器/image-20231209223715985.png)

-   针对不同Json转换的依赖的处理器
-   因为SpringMVC的集成的Json处理器是Jackson,所以使用JacksonTypeHandler不用另外导依赖

## 使用Json处理器

-   没有适合的全局变量做配置,但可以**使用@TableField注解**

    ```java
    @TableName(value = "tb_user" ,autoResultMap = true)
    public class User {
        @TableField(typeHandler = JacksonTypeHandler.class)
        private Section section;
        ...
    }
    ```

-   由于User里嵌套了另一个类Section,做映射会很麻烦,所以要给类加注解

    ```java
    @TableName(value = "tb_user" ,autoResultMap = true)
    ```

​	

