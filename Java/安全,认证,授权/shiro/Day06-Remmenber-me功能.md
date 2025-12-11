-   敏感信息或付款操作是不能remenber me的
-   基于Cookie实现

# 实现

## 准备Cookie

```java
/**
 *  Remember me
 * @return cookie
 */
public static SimpleCookie getCookie(){
    SimpleCookie cookie = new SimpleCookie("remember-me");

    //设置跨域
    //cookie.setDomain("domain");
    cookie.setPath("/login");//只有login路径能用这个cookie
    cookie.setHttpOnly(true);
    cookie.setMaxAge(30*24*60*60);// 一个月
    return cookie;
}

```

![image-20231222223442306](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/安全,认证,授权/shiro/Day06-Remmenber-me功能/image-20231222223442306.png)

## 准备Manager

```java
/**
 * 创建Shiro的Cookie管理对象
 * @return manager
 */
public static CookieRememberMeManager getManager(){
    CookieRememberMeManager manager = new CookieRememberMeManager();
    manager.setCookie(getCookie());
    byte[] bytes = "the len of the key must 16/24/32".getBytes();
    System.out.println(bytes.length);//32
    manager.setCipherKey(bytes);//Invalid AES key length: 41 bytes
    return manager;
}
```

## 配置

```java
/**
 * 初始化获取SecurityManager
 *
 * @return securityManager
 */
@Bean
public DefaultWebSecurityManager defaultWebSecurityManager(){
    ...

    securityManager.setRememberMeManager(RememberMe.getManager());

    return securityManager;
}
```

## 修改View

```java
<input type="checkbox" name="rememberMe">记住我<br>
```

## 修改Controller

![image-20231222194130648](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/安全,认证,授权/shiro/Day06-Remmenber-me功能/image-20231222194130648.png)

## 注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意注意

一个下午一个晚上七个小时血的教训--------------

shiro认为remember是不够安全的, 不足以访问authc的权限的路径

```java
/**
 * 配置默认的Shiro内置过滤器拦截信息
 * @return 拦截器链
 */
@Bean
public DefaultShiroFilterChainDefinition defaultShiroFilterChainDefinition(){
    DefaultShiroFilterChainDefinition filterChainDefinition = new DefaultShiroFilterChainDefinition();
    // 不认证可以访问的资源
    filterChainDefinition.addPathDefinition("/","anon");
    filterChainDefinition.addPathDefinition("/index","anon");
    filterChainDefinition.addPathDefinition("/login","anon");
    filterChainDefinition.addPathDefinition("/login-judge","anon");
    // 需要进行登录的就拦截
    filterChainDefinition.addPathDefinition("/**","authc");

    return filterChainDefinition;
}
```

remember能访问user权限的

```java
filterChainDefinition.addPathDefinition("/**","user");
```

