# SpringBoot整合Shiro

## 依赖

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.14</version>
</parent>

<dependencies>
    <dependency>
        <groupId>org.apache.shiro</groupId>
        <artifactId>shiro-spring-boot-web-starter</artifactId>
        <version>1.9.0</version>
    </dependency>
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <version>3.5.3.2</version>
    </dependency>

    <!--mysql-->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.31</version>
    </dependency>

    <dependency>
        <groupId>javax.servlet</groupId>
        <artifactId>javax.servlet-api</artifactId>
        <scope>provided</scope>
    </dependency>

    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.13.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 配置

```yml
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/security?characterEncoding=utf-8&useSSL=false
    username: root
    password: 123456
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8

shiro:
  loginUrl: /login

server:
  port: 8080
  servlet:
    context-path: /security
    encoding: # 没用
      force: true # 没用
      charset: utf-8 #没用 
```

## 数据库

```java
-- auto-generated definition
create table user
(
    id       int          null,
    name     varchar(30)  null comment '姓名',
    fullname varchar(255) null,
    pwd      varchar(50)  null,
    rid      bigint       null comment '角色id'
)
    comment 'Shiro的用户表';
```

## pojo

## Dao

## Service

```java
package com.harvey.security.shiro.service.realm;

import ...

@Service
public class MyRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    /**
     *
     * @param token the authentication token containing the user's principal and credentials.
     * @return 对比信息
     * @throws AuthenticationException 认证异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // 1. 获取身份信息
        Object principal = token.getPrincipal();
        System.out.println("输入的身份用户名信息: " + principal);

        System.out.println(userService==null);
        // 2. 获取数据库中存储的用户信息
        UserDto userDto = userService.getUserInfo(principal.toString());

        // 验证密码
        String salt = "salt";
        AuthenticationInfo info = null;
        String pwd;

        if (userDto!=null&&(pwd = userDto.getPwd()/*从数据库中取信息*/) != null) {

            // 3. 创建封装校验逻辑的对象,封装数据返回
            info = new SimpleAuthenticationInfo(
                    principal,
                    pwd,
                    ByteSource.Util.bytes(salt),
                    "这是一个Realm"
            );

        }

        return info;
    }

    /**
     * 赋予权限的方法, 在checkRole(),hasRole(),
     * 触发权限判断, 要么在页面中shiro:***属性判断
     * 要么注解@Request**
     * checkPermission()等方法被调用的时候调用
     * @param principals 用户名
     * @return 封装的授权信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println(principals.getRealmNames());//用来观察测试

        // 伪造数据库
        Map<String, String> rolesMap = new HashMap<>();
        rolesMap.put("zhangsan", "role1");
        rolesMap.put("lisi", "role2,role1");
        Map<String, String> permissionsMap = new HashMap<>();
        permissionsMap.put("zhangsan", "user:insert,user:query");
        permissionsMap.put("lisi", "user:query");
        // 授权
        String primaryPrincipal =(String) principals.getPrimaryPrincipal();
        HashSet<String> roles = new HashSet<>(
                Arrays.asList(rolesMap.get(primaryPrincipal).split(","))
        );
        HashSet<String> permissions = new HashSet<>(
                Arrays.asList(permissionsMap.get(primaryPrincipal).split(","))
        );
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRoles(roles);
        info.addStringPermissions(permissions);
        return info;
    }
}
```

## Configuration

```java
@Configuration
public class ShiroConfig {

    @Autowired
    private MyRealm myRealm;// myRealm=com.harvey.security.shiro.service.realm.MyRealm

    /**
     * 初始化获取SecurityManager
     *
     * @return securityManager
     */
    @Bean
    public DefaultWebSecurityManager defaultWebSecurityManager(){
        // 1. 创建defaultWebSecurityManager对象

        /*
         * IniSecurityManagerFactory factory =
         *                 new IniSecurityManagerFactory("classpath:users.ini");
         * SecurityManager securityManager = factory.getInstance();
         */
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        // 2. 创建加密对象, 设置相关属性

        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        // 使用md5加密,盐是salt(在myRealm中配置),迭代加密 1 次
        matcher.setHashAlgorithmName("MD5");//md5Matcher=org.apache.shiro.authc.credential.Md5CredentialsMatcher
        matcher.setHashIterations(1);//md5Matcher.hashIterations=1

        // 3. 将加密对象存储到myRealm
        myRealm.setCredentialsMatcher(matcher);//myRealm.credentialsMatcher=$md5Matcher

        // 4. 将myRealm存入securityManager
        securityManager.setRealm(myRealm);//securityManager.realms=$myRealm

        return securityManager;
    }

    /**
     * 配置默认的Shiro内置过滤器拦截信息
     * @return 拦截器链
     */
    @Bean
    public DefaultShiroFilterChainDefinition defaultShiroFilterChainDefinition(){
        DefaultShiroFilterChainDefinition filterChainDefinition = new DefaultShiroFilterChainDefinition();
        // 不认证可以访问的资源
        filterChainDefinition.addPathDefinition("/","anon");
        filterChainDefinition.addPathDefinition("/login","anon");
        filterChainDefinition.addPathDefinition("/login1","anon");
        // 需要进行登录仍旧的拦截
        filterChainDefinition.addPathDefinition("/**","authc");
        return filterChainDefinition;
    }

}
```

## Controller

```java
//http://localhost:8080/security/login1?name=zhangsan&pwd=zhangsan
@GetMapping(value = "/login1",produces = Constant.TEXT_PRODUCES)
@ResponseBody
public String login1(String name ,String pwd){
    if(name==null||pwd==null) {
        return "请先输入用户名密码登录";
    }
    StringBuilder result= new StringBuilder();

    // 1. 获取Subject对象
    //      依据工具获取Subject对象, 就先要把securityManager放入工具中
    SecurityUtils.setSecurityManager(defaultWebSecurityManager);

    //      从工具获取Subject工具(已封装)
    Subject subject = SecurityUtils.getSubject();

    // 2. 模拟创建token对象, 文本应用用户名密码从页面传递
    AuthenticationToken token =
            new UsernamePasswordToken(name, pwd);

    // 3. 完成登录
    try {
        subject.login(token);
        // subject来源于securityManager的封装, 它将token传入securityManager
        // securityManager的创建基于模拟的数据源users.ini, 将解析ini并从中获取数据,比对校验token
        result.append("登录成功\n");
    } catch (UnknownAccountException e) {
        result.append("用户不存在\n");
    } catch (IncorrectCredentialsException e) {
        result.append("密码错误\n");
    } catch (Exception e) {
        result.append("登录失败\n");
        result.append(e).append(e.getMessage()).append('\n');
    }

    System.out.println(result);
    subject.logout();// 只是为了方便测试而设置的
    return String.valueOf(result);
}
```

