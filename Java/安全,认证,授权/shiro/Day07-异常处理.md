# 异常处理

`@ControllerAdvice`, `@ExceptionHandler`

```java
package com.harvey.security.shiro.controller.exp;

import ...

@ControllerAdvice
public class ShiroExceptionController  {
    /**
     * 用户不存在
     * @return 异常信息
     */
    @ResponseBody
    @ExceptionHandler(UnknownAccountException.class)
    public String unknownAccountExceptionHandler(){
        return "UnknownAccountException";
    }

    /**
     * 用户名或密码错误
     * @return 异常信息
     */
    @ResponseBody
    @ExceptionHandler(IncorrectCredentialsException.class)
    public String incorrectCredentialsExceptionHandler(){
        return "IncorrectCredentialsException";
    }

    /**
     * 认证异常
     * @return 异常信息
     */
    @ResponseBody
    @ExceptionHandler(AuthenticationException.class)
    public String authenticationExceptionHandler(){
        return "AuthenticationException";
    }

    /**
     * 没有权限
     * @return 异常信息
     */
    @ResponseBody
    @ExceptionHandler(UnauthorizedException.class)
    public String unauthorizedExceptionHandler(){
        return "UnauthorizedException";
    }

    /**
     * 权限异常
     * @return 异常信息
     */
    @ResponseBody
    @ExceptionHandler(AuthorizationException.class)
    public String authorizationExceptionHandler(){
        return "AuthorizationException";
    }

}
```

## 没权限就不再前端显示(需要thymeleaf)

```xml
<artifactId>thymeleaf-extra-shiro</artifactId>
```

### 解析thymeleaf的shiro:标签的相关信息

```java
@Bean
public ShiroDialect shiroDialect(){
    return new ShiroDialect();
}
```

### shiro:标签

