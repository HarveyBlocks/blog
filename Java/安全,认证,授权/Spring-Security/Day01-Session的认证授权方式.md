[基于原生Spring-MVC-Session的认证授权实现](..\基于原生Spring-MVC-Session的认证授权实现.zip)

# Session认证流程

1.  用户登录成功
2.  服务端创建一个Session  
3.  Session中存储用户的信息(一般是在内存中)
4.  服务端向客户端响应Session的ID
5.  这个ID被客户端存在了Cookie
6.  下一次用户就可以带着这个Session ID的值来访问我们的Web服务器
7.  此时我们的Web服务端就校验, 用户已经存在这个Session了,就不再使用认证了



-   Session规范是由Servlet规范定制的, Servlet容器已经实现

# SpringSecurity实现

## 准备依赖

```xml
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>4.0.1</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>5.3.29</version>
</dependency>
```

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-web</artifactId>
    <version>5.7.5</version>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-config</artifactId>
    <version>5.7.5</version>
</dependency>
```

## 配置

```yaml
server:
  port: 8080
  servlet:
    encoding:
      force: true
    context-path: /security

spring:
  application:
    name: security #日志等中的名字
  datasource:
    url: jdbc:mysql://localhost:3306/security
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
```

在WebConfig.java中

Spring Security会提供拦截器, 所以可以把自定义的拦截器去掉

其余保留

-   ApplicationConfig.java

    Spring容器的配置类

    ```java
    @Configuration
    @ComponentScan(basePackages = {"com.harvey.security.session"}
            /*excludeFilters = {
                    @ComponentSc-an.Filter(type = FilterType.ANNOTATION, value = Controller.class)
            }  *//*排除*/)
    public class ApplicationConfig {}
    ```

-   WebConfig.java

    SpringMVC项目的核心配置类

    ```java
    @Configuration
    @EnableWebMvc
    @ComponentScan(basePackages = {"com.harvey.security.session"}
            /*excludeFilters = {
                    @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class)
            }  *//*排除*/)
    public class WebConfig implements WebMvcConfigurer {
        /**
         * 视图解析器
         *
         * @return 视图解析器的Bean
         */
        @Bean
        public InternalResourceViewResolver viewResolver() {
            InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
            //viewResolver.setSuffix(".jsp");// 以.jsp结尾
            return viewResolver;
        }
    
        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/login").setViewName("login.jsp");
            // 这里ViewName写login, 联系上面的以".jsp"结尾, 就可以匹配login.jsp
            // 
            registry.addViewController("/index").setViewName("index.jsp");
        }
    }
    ```

-   项目的初始化类

    ```java
    public class SpringApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    
        /**
         * Spring容器
         * 加载Spring的Configuration
         * @return Spring的Configuration的类对象
         */
        @Override
        protected Class<?>[] getRootConfigClasses() {
            return new Class[]{ApplicationConfig.class};
        }
    
        /**
         * servletContext
         * 加载SpringMVC的Configuration
         * @return SpringMVC的Configuration的类对象
         */
        @Override
        protected Class<?>[] getServletConfigClasses() {
            return new Class[]{WebConfig.class};
        }
    
        /**
         * 映射器路径,ContextPath
         *
         * @return 根路径
         */
        @Override
        protected String[] getServletMappings() {
            return new String[]{"/"};
        }
    }
    ```

## 认证

创建一个**Security**的Config**核心配置类**, 需要继承**WebSecurityConfigurerAdapter**

```java
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {}
```

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({WebSecurityConfiguration.class, SpringWebMvcImportSelector.class, OAuth2ImportSelector.class, HttpSecurityConfiguration.class})
@EnableGlobalAuthentication
@Configuration
public @interface EnableWebSecurity {
    boolean debug() default false;
}
```

### 认证页面

SpringSecurity默认提供一些页面

-   WebConfig.xml

```java
@Override
public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/").setViewName("redirect:/login");
}
```

### 认证功能

>   Spring-Security提供了用户名密码登录, 退出, 会话管理等认证功能

```java
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
    /**
     * 定义用户信息服务(查询用户信息)<br>
     * @return 用户信息服务的Bean对象
     */
    @Bean
    public UserDetailsService userDetailsService(){...}


    /**
     * 密码编码器, 比对密码的方法<br>
     * PasswordEncoder的各种实现类, 都是一种密码的编码方式<br>
     * @return 密码编码器的Bean对象
     */
    @Bean
    public PasswordEncoder passwordEncoder(){...}



    /**
     * 安全拦截机制(怎么拦截, 怎么授权)
     * @param http en
     * @throws Exception dio
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {...}

}
```

####用户信息服务

需要我们提供UserDetailsService

-   根据内存的方式

    ```java
    /**
     * 定义用户信息服务(查询用户信息)<br>
     * 从内存中查询已有的用户信息
     * @return 用户信息服务的Bean对象
     */
    @Bean
    public UserDetailsService userDetailsService(){
        // 从内存中查询已有的用户信息
        InMemoryUserDetailsManager userManager = new InMemoryUserDetailsManager();
        // org.springframework.security.core.userdetails.User
        userManager.createUser(
            User.withUsername("root").password("root")
            .authorities("r1").build());//赋予权限
        return userManager;
    }
    ```

-   根据连接数据库的方式

    a

####密码编码器

```java
/**
 * 密码编码器, 比对密码的方法<br>
 * PasswordEncoder的各种实现类, 都是一种密码的编码方式<br>
 * NoOpPasswordEncoder就是依据字符串比较密码;
 * @return 密码编码器的Bean对象
 */
@Bean
public PasswordEncoder passwordEncoder(){
    return NoOpPasswordEncoder.getInstance();
}
```





#### 安全拦截机制



```java
/**
 * 安全拦截机制(怎么拦截, 怎么授权)
 * @param http 设置拦截机制
 * @throws Exception 请求时可能出现的异常
 */
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
            .antMatchers("/resource/**").authenticated()// 这个目录下的需要验证
            .anyRequest().permitAll()// 其余的可以通过
            .and()
            .formLogin()//允许表单登录
            .successForwardUrl("/login-success");// 自定义登录成功的页面地址
}
```

-   自定义的登录成功页面

```java
@RequestMapping(value = "/login-success", produces = Constant.HTML_PRODUCES)
public String show() {
    return " 登录成功<br>" + "<a href=\"logout\">登出</a><br>";
}
@GetMapping(value = "/logout", produces = Constant.HTML_PRODUCES)
public String logout(HttpSession session) {
    session.invalidate();//让Session失效
    return "已登出<br>" + "<a href=\"index\">返回主页面</a><br>";
}
```



#### 初始化/注册认证配置类

-   SpringApplicationInitializer

```java
@Override
protected Class<?>[] getRootConfigClasses() {
    return new Class[]{ApplicationConfig.class, WebSecurityConfig.class};
}
```

###SpringSecurity环境的初始化

>   创建一个属于SpringSecurity的Initializer

```java
public class SpringSecurityApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
    public SpringSecurityApplicationInitializer() {
        /*
            如果没有使用Spring(或SpringMVC)环境,可以用这个super(Initializer)来实现注册SecurityConfig
            super(WebSecurityConfig.class);
            如果使用了, 也可以再SpringApplicationConfig的getRootConfigClasses()里去注册SecurityConfig
        */
    }
}
```

-   但是这个Initializer的类还是有必要的, 是需要的, 否则Security会不管用

## 授权

###对端点进行权限限制

```java
/**
 * 安全拦截机制(怎么拦截, 怎么授权)
 * @param http 设置拦截机制
 * @throws Exception 请求时可能出现的异常
 */
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
            .antMatchers("/resource/**").authenticated()// 这个目录下的需要验证
            .anyRequest().permitAll()// 其余的可以通过
            .and()
            .formLogin()//允许表单登录
            .successForwardUrl("/login-success");// 自定义登录成功的页面地址
    throw new Exception();
}
```

对其作改进

```java
http.authorizeRequests()
        .antMatchers("/resource/r0").hasAnyAuthority("r0")
        .antMatchers("/resource/r1").hasAnyAuthority("r0","r1")//r0或r1
        //.antMatchers("/resource/**").hasAnyAuthority("all")不行呜呜呜
    	.antMatchers("/resource/r1","/resource/r0").hasAnyAuthority("all")//行了
        .antMatchers("/resource/**")
        .authenticated()// 这个目录下的需要验证
        .anyRequest().permitAll()// 其余的可以通过
        .and()
        .formLogin()//允许表单登录
        .successForwardUrl("/login-success");// 自定义登录成功的页面地址
```



\整理以下问题:

资源:

a1\b1\c1

a1\b1\c2

a1\b2\c1

a2\b1

权限:

A,B,C

c1 只有A

c2 只有B

b1 ABC都能访问







### 授予权限

```java
@Bean
public UserDetailsService userDetailsService(){
    // 从内存中查询已有的用户信息
    InMemoryUserDetailsManager userManager = new InMemoryUserDetailsManager();
    // org.springframework.security.core.userdetails.User
    userManager.createUser(
        User.withUsername("root").password("root")
        .authorities("r1").build());//赋予权限
    return userManager;
}
```



```java
@Bean
public UserDetailsService userDetailsService(){
    // 从内存中查询已有的用户信息
    InMemoryUserDetailsManager userManager = new InMemoryUserDetailsManager();
    // org.springframework.security.core.userdetails.User
    userManager.createUser(
        User.withUsername("root").password("root").authorities("all").build());
    userManager.createUser(
        User.withUsername("zhangsan").password("zhangsan").authorities("r0").build());
    userManager.createUser(
        User.withUsername("wangwu").password("wangwu").authorities("r1").build());
    return userManager;
}
```

