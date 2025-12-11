# 登出

## 自定义登出端点

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()// 取消对CSRF的保护
            ...
        .and()
            .logout().logoutUrl("/logout")//自定义登出处理的URL, 默认使Session无效,清理上下文
            .logoutSuccessUrl("/logout-view?logout")
        	//自定义登出成功的端点,GPT说这个logout的参数是可选的
            //.logoutSuccessHandler()
            .addLogoutHandler(LogoutHandler)
         ;
}
```

### logoutSuccessHandler
`.logoutSuccessHandler(LogoutSuccessHandler接口的实现类)`

自定义登出成功之后的清理工作,但是这些操作可以在logoutSuccessUrl做,
设置之后, logoutSuccessUrl的配置被认作无效

### addLogoutHandler

`.addLogoutHandler(LogoutHandler接口的实现类)`

 .addLogoutHandler(LogoutHandler接口的实现类)
 自定义登出之后的清理工作
 和.logoutSuccessHandler()的区别在于, 不论登出成功与否, 都会执行

不会让logoutSuccessUrl失效

#### LogoutHandler接口的实现类
一般来说， LogoutHandler 的实现类被用来执行必要的清理，因而他们不应该抛出异常。

下面是Spring Security提供的一些实现：

- PersistentTokenBasedRememberMeServices 

    基于持久化token的RememberMe功能的相关清理

- TokenBasedRememberMeService 

    基于token的RememberMe功能的相关清理

- CookieClearingLogoutHandler 

    退出时Cookie的相关清理

- CsrfLogoutHandler 

    负责在退出时移除csrfToken

- SecurityContextLogoutHandler 

    退出时SecurityContext的相关清理

链式API提供了调用相应的 LogoutHandler 实现的快捷方式，比如deleteCookies()。

