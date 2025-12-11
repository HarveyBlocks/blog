# 拦截器执行顺序

## 多个拦截器的执行顺序

### 康康两个的

>   两个拦截器拦截同一个资源,执行顺序是怎么样的?

```xml
<mvc:interceptors>
    <!--先0再1-->
    <mvc:interceptor>
        <mvc:mapping path="/**"/>
        <bean class="com.harvey.interceptor.MyInterceptor0"/>
    </mvc:interceptor>
    <mvc:interceptor>
        <mvc:mapping path="/**"/>
        <bean class="com.harvey.interceptor.MyInterceptor1"/>
    </mvc:interceptor>
</mvc:interceptors>
```

```txt
MyInterceptor0::preHandle
MyInterceptor1::preHandle
-------------/body2-----------
User{username='张三', age=18, hobby=[足球,  篮球, java], birthday=Sun Nov 11 08:00:00 CST 2018, address=Address{city='霓虹', area='Tokyo'}}
MyInterceptor1::postHandle
MyInterceptor0::postHandle
MyInterceptor1::afterCompletion
MyInterceptor0::afterCompletion
```

-   **谁先配置.谁先执行preHandle()**
-   剩下俩倒着来

### 康康三个的

```xml
<mvc:interceptors>
    <mvc:interceptor>
        <!--对请求的路径进行拦截-->
        <mvc:mapping path="/**"/>
        <bean class="com.harvey.interceptor.MyInterceptor0"/>
    </mvc:interceptor>

    <mvc:interceptor>
        <!--对请求的路径进行拦截-->
        <mvc:mapping path="/**"/>
        <bean class="com.harvey.interceptor.MyInterceptor1"/>
    </mvc:interceptor>
    <mvc:interceptor>
        <!--对请求的路径进行拦截-->
        <mvc:mapping path="/**"/>
        <bean class="com.harvey.interceptor.MyInterceptor2"/>
    </mvc:interceptor>
</mvc:interceptors>
```

```txt
MyInterceptor0::preHandle
MyInterceptor1::preHandle
MyInterceptor2::preHandle
-------------/body2-----------
User{username='张三', age=18, hobby=[足球,  篮球, java], birthday=Sun Nov 11 08:00:00 CST 2018, address=Address{city='霓虹', area='Tokyo'}}
MyInterceptor2::postHandle
MyInterceptor1::postHandle
MyInterceptor0::postHandle
MyInterceptor2::afterCompletion
MyInterceptor1::afterCompletion
MyInterceptor0::afterCompletion
```

-   规律逐渐明了

### 看看图解

![image-20231129155637307](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-mvc/SpringMVC拦截器/Day05-执行顺序/image-20231129155637307.png)

 ![image-20231129200410668](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-mvc/SpringMVC拦截器/Day05-执行顺序/image-20231129200410668.png)

