# 自定义认证

>   以生产为目标

## 自定义登录页面

-   配置视图解析器

```java
@Override
public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/").setViewName("redirect:/index");// 酱紫玩
    registry.addViewController("/index").setViewName("index.html");
    registry.addViewController("/login-view").setViewName("login.html");
    // 这样就请求转发到了自己的login页面
}
```

-   login.html

![image-20231218194854743](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/安全,认证,授权/Spring-Security/Day02-自定义认证/image-20231218194854743.png)

-   指定登录界面的URL

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
            ...
        	.permitAll()// 其余的可以通过
            .and()
            .formLogin()							//允许表单登录
            .loginPage("/login-view")				//自定义登录页面
            .loginProcessingUrl("/login")			//指定登录处理的URL
            .successForwardUrl("/login-success");	// 自定义登录成功的页面地址
}
```

这里会有问题

![image-20231218194831968](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/安全,认证,授权/Spring-Security/Day02-自定义认证/image-20231218194831968.png)

-   原因是SpringSecurity为了防止**CSRF**( Cross-site request forery **跨站请求伪造**), 限制了除了get意外的大多数的方法

-    **跨站请求伪造**可能会对网站的安全造成影响

解决方法1: 关闭对CSRF的防护(然后用其他方法解决CSRF的防护[暂且按下不提])

```java
http.csrf().disable()...
```

解决方法2: login.jsp设置为合法的token(但是**SpringBoot不支持JSP,** 因为JSP前后端粘连, 被淘汰了)

```jsp
<form action="login" method="post">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
    ...
</form>
```

要不就导入依赖:

```xml
<!‐‐用于编译jsp ‐‐>
<dependency>
	<groupId>org.apache.tomcat.embed</groupId>
	<artifactId>tomcat‐embed‐jasper</artifactId>
	<scope>provided</scope>
</dependency>
```

## 连接数据库

-   建个库

    ```mysql
    create database security character set 'utf8' collate 'utf8_general_ci';
    ```

-   创个表

    ```mysql
    CREATE TABLE t_user
    (
        id        bigint(20)   NOT NULL COMMENT '用户id',
        username  varchar(64)  NOT NULL,
        password  varchar(64)  NOT NULL,
        fullname varchar(255) NOT NULL COMMENT '用户姓名',
        mobile    varchar(11) DEFAULT NULL COMMENT '手机号',
        PRIMARY KEY (id) USING BTREE
    ) ENGINE = InnoDB
      DEFAULT CHARSET = utf8
      ROW_FORMAT = DYNAMIC;
    ```

-   加几条记录(口令的加密太长, 这里先不改, 之后记得改)

    ```mysql
    INSERT INTO security.t_user (id, username, password, full_name, mobile)
    VALUES (1, 'zhangsan', 'zhangsan', '张三', '17382938473');

    INSERT INTO security.t_user (id, username, password, full_name, mobile)
    VALUES (2, 'lisi', 'lisi', '李四', '13758304885');

    INSERT INTO security.t_user (id, username, password, full_name, mobile)
    VALUES (0, 'root', 'root', '新世界的神', '17592840293');
    ```

-   添加数据库依赖

    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
    </dependency>

    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
    ```

-   配置数据源application.yml

    ```yml
    spring:
      datasource:
        url: jdbc:mysql://localhost:3306/security
        username: root
        password: 123456
        driver-class-name: com.mysql.cj.jdbc.Driver
    ```

-   准备pojo.entity.UserDTO

-   准备UserDao

    ```java
    @Repository //Spring Boot的注解, 标记Dao
    public class UserDao {
        @Autowired
        private JdbcTemplate jdbcTemplate;// 好用的模板工具类

        public UserDTO getUserByUsername(String username){
            String sql = "select id," +
                    "       username," +
                    "       password," +
                    "       fullname as fullName," +
                    "       mobile" +
                    "from t_user" +
                    "where username = ?" ;
            // 连接数据库
            List<UserDTO> users = jdbcTemplate.query(sql, new Object[]{username},
                    new BeanPropertyRowMapper<>(UserDTO.class));
            if (users !=null && users.size()==1){
                return users.get(0);
            }
            return null;
        }
    }
    ```

-   在Service中注入UserDao

    ```java
    @Service
    public class MyUserDetailsService implements UserDetailsService {

        @Autowired
        private UserDao userDao;
        ...
    }
    ```

-   完成getUserByUsername()逻辑(先忽略授权)

    ```java
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO userDTO;
        // 数据库中依据用户名查找用户信息
        if((userDTO = userDao.getUserByUsername(username))==null){
            return null;// 由认证流程, 返回null将有provider抛出异常
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(username).password(userDTO.getPassword())
                .authorities("r1").build();
    }
    ```

