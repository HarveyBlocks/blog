#SpringMVC原理

##图解

![image-20231129200738394](../../typora-user-images/Day05-原理/image-20231129200738394.png)

1.  服务端请求

    ![image-20231129202230927](../../typora-user-images/Day05-原理/image-20231129202230927.png)

2.  SpringMVC对资源进行映射

    -   使用处理器映射器HandlerMapping

        

    1.  经过Interceptor

    2.  经过拦截器层层阻围,得到Controller

    3.  把Intercepter(**可能有多个**)和Controller(**只有一个**)封装成对象HandlerExcutionChain

        ![image-20231129202133183](../../typora-user-images/Day05-原理/image-20231129202133183.png)

        ![image-20231129202354427](../../typora-user-images/Day05-原理/image-20231129202354427.png)

    4.  经过HandlerMapping把对象HandlerExcutionChain返回

        ![image-20231129202211657](../../typora-user-images/Day05-原理/image-20231129202211657.png)

3.  运行HandlerExcutionChain的内容

4.  ...



## 拦截器

###前端控制器

![image-20231129202230927](../../typora-user-images/Day05-原理/image-20231129202230927.png)







![image-20231129202354427](../../typora-user-images/Day05-原理/image-20231129202354427.png)





#### doDispatch();



![image-20231129202517417](../../typora-user-images/Day05-原理/image-20231129202517417.png)



#### 

```java
mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
```

-    Actually invoke the handler.
-   执行资源(这里不赘述)



![image-20231129203101822](../../typora-user-images/Day05-原理/image-20231129203101822.png)



![image-20231129203424990](../../typora-user-images/Day05-原理/image-20231129203424990.png)





####applyPreHandle()

![image-20231129202646794](../../typora-user-images/Day05-原理/image-20231129202646794.png)

**你看这循环,从前到后**





####applyPreHandle()

![image-20231129203119815](../../typora-user-images/Day05-原理/image-20231129203119815.png)

-   **你看这循环, 从后到前**

#### processDispachResult

-   processDispatchResult()

    ![image-20231129203545598](../../typora-user-images/Day05-原理/image-20231129203545598.png)

-   triggerAfterCompletion()

    ![image-20231129203623992](../../typora-user-images/Day05-原理/image-20231129203623992.png)

    -   **你看这循环, 从后到前**