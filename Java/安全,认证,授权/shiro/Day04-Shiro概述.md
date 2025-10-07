# Apache Shiro

>   全面, 强大, 简单, 灵活,兼容型,社区

[Apache Shiro](https://shiro.apache.org/reference.html)

##功能

![Apache Shiro Features](https://shiro.apache.org/images/ShiroFeatures.png)

- 认证
- 授权
- 加密

- 会话管理



- 与Web集成

- 缓存

- 并发验证

- 测试

- "Run As"

    一个用户以另一个用户的身份登录

- Remenber Me



##架构原理

![Shiro Basic Architecture Diagram](https://shiro.apache.org/images/ShiroBasicArchitecture.png)

应用程序用Subject对象(用户, 对象, 网络爬虫)和Shiro交互

Shiro Security Manager对Subject对象进行安全校验  

 Realm获取安全数据, 可以看作数据源

### Shiro Security Manager

![Shiro Architecture Diagram](https://shiro.apache.org/images/ShiroArchitecture.png)



##依赖

```xml
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-core</artifactId>
    <version>1.9.0</version>
</dependency>
```

### 



## 搭建简单的Shiro的登录认证

### Shiro中的登录认证

Shiro中用户需要提供

-   principals(身份)
    -   主体标识(邮箱,用户名,手机号)
    -   唯一
    -   一个主体可以有多个principal, 但全域的principal只能由一个
-   credentials(证明)
    -   只有主体知道的安全值(口令, 密码,数字证书,生物信息?)



-   Shiro的认证流程

![authentication flow diagram](https://shiro.apache.org/images/ShiroAuthenticationSequence.png)

-   使用Subject.login()登录
    -   失败抛出异常**AuthenticationException**
-   可创建自定义 的Realm类, 继承AuthenticatingRealm类实现doGetAuthenticationInfo()方法

### 数据准备(ini文件)

```ini
[users]
;这是一条注释
zhangsan=zhangsan
lisi=lisi
;键值对的形式
```

### 实现

```java
package com.harvey.security.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;

/**
 * Shiro的登录认证
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2023-12-20 23:02
 */
public class ShiroRun {
    public static void main(String[] args) {
        // 1. 初始化获取SecurityManager
        IniSecurityManagerFactory factory =
                new IniSecurityManagerFactory("classpath:users.ini");
        SecurityManager securityManager = factory.getInstance();

        // 2. 获取Subject对象
        //      依据工具获取Subject对象, 就先要把securityManager放入工具中
        SecurityUtils.setSecurityManager(securityManager);

        //      从工具获取Subject工具(已封装)
        Subject subject = SecurityUtils.getSubject();

        // 3. 模拟创建token对象, 文本应用用户名密码从页面传递
        AuthenticationToken token =
                new UsernamePasswordToken("zhangsan", "zhangsan");

        // 4. 完成登录
        try {
            subject.login(token);
            // subject来源于securityManager的封装, 它将token传入securityManager
            // securityManager的创建基于模拟的数据源users.ini, 将解析ini并从中获取数据,比对校验token
            System.out.println("成功");
        }catch (UnknownAccountException e) {
            System.out.println("用户不存在");
        }catch (IncorrectCredentialsException e) {
            System.out.println("密码错误");
        }catch (Exception e) {
            System.out.println  ("登录失败");
        }

    }
}
```

## 角色授权

-   授权

-   主体Subject

-   资源Resource

    可访问的URL

-   权限Permition

    -   粒度

-   角色role

### Shiro支持的授权方式

-   if-else判断

    `subject.hasRole("admin")`

-   注解方式

    ```java
    @RequiresRoes("admin")
    public void hello(){
        // 需要权限admian. 没有就抛异常
    }
    ```

-   JSP标签

    ```jsp
    <shiro:hasRole name="admian">
    <!--有权限-->
    </shiro:hasRole>
    ```


### Shiro授权流程

![Shiro authorization sequence graphic](https://shiro.apache.org/images/ShiroAuthorizationSequence.png)

### 数据准备



```ini
[users]
zhangsan=zhangsan,role1,role2
lisi=lisi,role2

[roles]
role1=user:insert,user:query
role2=user:query
```

### 实现



#### if-else授权

-   判断角色

    ```java
    // 5. 判断角色
    if(subject.hasRole("role1")){
        System.out.println(subject.getPrincipal()+"可访问资源一");
    }else {
        System.out.println(subject.getPrincipal()+"没有role1权限");
    }
    ```

-   判断权限

    ```java
    // 5. 判断权限
    if(subject.isPermitted("user:insert")){
        System.out.println(subject.getPrincipal()+"可insert");
    } else {
        System.out.println(subject.getPrincipal()+"没有insert权限");
    }
    ```

-   使用check(无返回值, 没有权限就抛出异常)

    ```java
    // 5. 判断权限
    try {
        subject.checkPermission("user:insert");
        System.out.println(subject.getPrincipal() + "可insert");
    } catch (UnauthorizedException e) {
        System.out.println(subject.getPrincipal() + "没有insert权限");
    }
    ```
    
-   方法上注解判断权限

    `@RequestAuthentication`用户是否登录

    `@RequestUser`用户是否被记忆(RememberMe)

    `@RequestGuest`用户是否是一个游客的请求

    `@RequestRoles("role")`

    `@RequestPermissions("AA")`



## 加密

```java
private static void cryptography() {
    String password = "1H0a2r3v0e0y3";

    // 使用MD5加密
    Md5Hash md5Hash = new Md5Hash(password);
    System.out.println(md5Hash.toString());
    System.out.println(md5Hash.toHex());

    // 加点盐(代盐加密)
    Md5Hash md5HashWithSalt = new Md5Hash(password,"salt");
    System.out.println(md5HashWithSalt.toHex());

    // 二次加密,多次迭代加密
    Sha256Hash sha256Hash = new Sha256Hash(md5HashWithSalt.toHex(),"salt = md5HashWithSalt");
    System.out.println(sha256Hash.toHex());

    // 父类加密
    // public class Md5Hash extends SimpleHash
    SimpleHash simpleHash = new SimpleHash("MD5",sha256Hash.toHex(),"salt = simpleHash");
    System.out.println(simpleHash.toHex());

}
```