# 浏览器缓存

成本最低的缓存方式

降低服务器压力

减少网络延迟, 加快响应速度

## HTTP协议中页面缓存相关字段

-   `Expire` 
    -   缓存过期的日期和时间, 基于服务端的时间
-   `Cache-Control`
    -   设置缓存相关的配置信息
    -   `no-cache` 无论有没有缓存, 都要去服务端确认缓存有没有变化(弱缓存)
    -   `max-age` 缓存存在的时间
-   `Last-Modified`
    -   请求资源最后修改时间
-   `ETag`
    -   请求变量的实体标签的当前值, 比如文件的MD5值

## 执行流程

![image-20240421115857529](..\..\assets\Day04-缓存处理\image-20240421115857529.png)

## 相关指令



### `expires`

控制响应头中的`Expires`和`Cache-Control`

在`http`,`server`,`location`

``` nginx
expires [modified] time;
expires epoch | max | off;
```

-   默认`off`
-   `time`
    -   指定过期时间
    -   单位为s
    -   可以为负, 为负则`Cache-Control` 为`no-cache`
    -   如果为整数或0 , `Cache-Control` 为`max-age=time`
    -   不用`Expires`而用`Cache-Control` 为`max-age=time`是为了防止服务端和客户端时间不一致
-   `epoch`
    -   指定`Expires` 的值为`1, January, 1970,00:00:01 GMT`
    -   指定`Cache-Control`的值为`no-cache`
-   `max`
    -   指定`Expire` 的值为`31 December2037 23:59:59GMT`
    -   `Cache-Contro`l的值为10年
-   `off`默认不缓存





### `add_header`

增加头信息

在`http`,`server`,`location`

```nginx
add_header name value [always]
```

-   `always`
    -   无论支不支持头信息都会被加入

