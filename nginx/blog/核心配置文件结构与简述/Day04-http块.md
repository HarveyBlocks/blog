# http块

## MIME-TYPE

浏览器区分显示的内容有HTML, XML, GIF等

需要使用MIME Type

MiMe-Type是网络资源的媒体类型

Nginx作为web服务器, 也需要能够识别前端请求的资源类型

在Nginx的配置文件中, 默认配置有两行

```nginx
include mime.type
default_type application/octet-stream
```



### `include mime.type`

引入该文件, 之后就可以直接使用mime.type中的所有类型了

<img src="../../asset/Day04-http%E5%9D%97/image-20240420134254738.png" alt="image-20240420134254738" style="zoom:50%;" />





### `default_type`

返回值的默认性质

```nginx
default_type mime-type;
```

默认的`mime-type`是`text/plain`

可以位于http, server, location

## 服务日志

-   Nginx支持的日志类型有`access.log`和`error.log`
-   `access.log` 
    -   用来记录用户所有的访问请求
-   `error.log`
    -   用来记录nginx本身运行时的错误信息
    -   不会记录用户的访问请求

### `access_log`

用来设置用户访问日志的相关属性

```nginx
access_log  path[format [buffer=size]]  ;
```

默认值

```nginx
access_log logs/acess.log combined;
```

可于`server`, `http` , `location`







### `log_format`

```nginx
log_format name [escape=default|json|none] string....;
```

默认↓

`combined`与上文的日志格式相对应

```nginx
log_format combined "...."
```

位置在`http`

```nginx
log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                  '$status $body_bytes_sent "$http_referer" '
                  '"$http_user_agent" "$http_x_forwarded_for"';
```

## 文件传输

```nginx
sendfile on|off ;
```

**默认`off`, 建议打开**

可以配置在`http`, `server`, `location`

设置Nginx服务器是否可以使用`sendfile()`函数来传输文件

该设置可以大大提升Nginx处理静态资源的性能

## 长连接超时时间

```nginx
keepalive_timeout  time ;
```

http是一种无状态协议, 客户端可以向服务端发送一个TCP请求, 服务端响应完毕后断开连接

客户端向服务端发送多个请求, 每个请求都需要重新创建一次连接, 效率相对来说较低

使用`keepalive`, 可以告诉服务器处理完一个请求后需要保持这个TCP连接的打开状态

若接收到来自这个客户端的其他请求, 服务端就会利用这个未被关闭的连接, 而不需要重新创建一个新的连接, 提升效率

但是这个连接也不能一直保持, 连接过多, 服务端资源下降, 这个时候就需要我们进行设置的其他超时时间





默认值是`75s`





## Server块



```nginx
server {
    listen       80;
    server_name  localhost;

    #access_log  /var/log/nginx/host.access.log  main;

    location / {
        root   /usr/share/nginx/html;
        index  index.html index.htm;
    }

    #error_page  404              /404.html;

    # redirect server error pages to the static page /50x.html
    #
    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   /usr/share/nginx/html;
    }

    # proxy the PHP scripts to Apache listening on 127.0.0.1:80
    #
    #location ~ \.php$ {
    #    proxy_pass   http://127.0.0.1;
    #}

    # pass the PHP scripts to FastCGI server listening on 127.0.0.1:9000
    #
    #location ~ \.php$ {
    #    root           html;
    #    fastcgi_pass   127.0.0.1:9000;
    #    fastcgi_index  index.php;
    #    fastcgi_param  SCRIPT_FILENAME  /scripts$fastcgi_script_name;
    #    include        fastcgi_params;
    #}

    # deny access to .htaccess files, if Apache's document root
    # concurs with nginx's one
    #
    #location ~ /\.ht {
    #    deny  all;
    #}
}


```

### `listen`与`server_name`

监听端口

```nginx
listen 80;
```

监听host

```nginx
server_name  localhost;
```



### location块



```nginx
location content_path {
    root   静态文件目录,根路径;
    # index 多个值, 从前往后, 找到为止
    index  index.html index.htm; 
}
```



## 作用域

诸如`error_log` 在`http`, `server`和`location`都可以配置, 那么, 哪个配置生效?

有配置, 作用域小的配置优先(就近原则), 没有配置, 就往上一级寻找

