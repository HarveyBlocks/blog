# 配置静态资源

[官方文档](https://nginx.org/en/docs/http/ngx_http_core_module.html)

## `listen`

`server`下

### 语法

配置监听端口

```nginx
listen address[:port] [default_server]...;
listen port [defau_server]...;
```

默认:

```nginx
listen *80 | *:8000
```

### 示例

```nginx
# 监听指定IP和端口
listen 127.0.0.1:80;
# 监听指定IP和端口
listen localhost:80;
# 监听指定IP的所有端口
listen 127.0.0.1;
# 监听指定端口的连接, 最常用
listen 80;
# 监听指定端口的连接
listen *:80;
```

### `default_server`

```nginx
server{
    listen localhost:8000;
}
server{
    listen 127.0.0.1:80 default_server;
}
```

默认主机, 即在没有匹配到对应的`host:port`, 就会采用的server, 

如果不指定, 采用的是第一个server

## `server_name`

用来设置虚拟主机的服务名, 可以是IP或域名

域名-IP需要配置到`/etc/hosts`

设置主机服务名, 支持多个服务名配置, 用空格分割

默认

```nginx
server_name ""
```

### 精确匹配

```nginx
server_name localhost ;
```

### 通配符

`*`表0个或多个, 不能放在中段, 只可以放在`*.`或者`.*`

```nginx
# 瞎编的, 可
server_name *.baidu.com;
# 不可
server_name www.*.com;
# 不可
server_name *w.baidu.com;
# 不可
server_name www.baidu.c*;
# 可
server_name www.baidu.*; 
```

### server_name支持正则表达式

**需要用`~`作为正则表达式开始的标记,`~`之后不能加空格**

```nginx
server_name ~^www\.(\w+)\.com$;
```

这里用`()`包围的部分, 可以被Nginx获取, 以`$num`获取

```nginx
server_name ~^www\.(\w+)\.com$ ;
# www.baidu.com
location / {
	# $1获取到baidu
    default_type text/plain ;
    return '$1';
}
```

```nginx
server_name ~^www\.(\w+)\.(\w+)\.com$ ;
# www.baidu.trash.com
location / {
	# $1获取到baidu, $2获取trash
    default_type text/plain ;
    return "$1 is $2";
}
```

### 匹配的优先级

当输入的host符合多个`server_name`时, 会优先采用哪种匹配方式? 

精确匹配 > 前置通配符 > 后置通配符 > 正则表达式 > default_server

## location

```nginx
server {
    default_type text/plain ;
    location [ 缺省 | = | ~ | ~* | ^~ | @ ]context_path {
        # ...
    }
}
```

-   无默认值

-   ` [ 缺省 | = | ~ | ~* | ^~ | @ ] ` 即匹配机制, URI种`view?name=Mike`的参数`?name=Mike`不参与匹配, 后**不留空格**

-   缺省

    -   必须以指定模式**开始**都能进入该location

    -   例如:

        ```nginx 
        location /abc {
        	return 200 "this is location /abc" ;
        }
        ```

        ```mermaid
        graph LR
        centos:80/abc --> /abc 
        centos:80/abc/def --> /abc 
        centos:80/abcdef --> /abc 
        ```

-   `=`

    -   用于不包含正则表达式的URI, 必须与指定的模式精确匹配

    -   例如: 

        ```nginx
        location =/abc {
        	return 200 "this is location =/abc" ;
        }
        ```

        ```mermaid
        graph LR
        centos:80/abc --> /abc 
        centos:80/abc/def --> NF
        centos:80/abcdef --> NF
        NF(404 Not Found)
        ```

-   `~`

    -   正则表达式URI

    -   区分大小写匹配

    -   例如: 

        ```nginx
        location ~^/abc/(\d)+$ {
            return 200 "this is location ~^/abc/(\d)+\$ , and zhe num is $1"  ;
        }
        ```

        ```mermaid
        graph LR
        centos:80/abc/1 --> /abc 
        centos:80/abc/112 --> /abc
        centos:80/abc --> NF
        centos:80/abc/ --> NF
        centos:80/Abc/112 --> NF
        NF(404 Not Found)
        ```

-   `~*`

    -   正则表达式URI
    -   不区分大小写进行匹配

-   `^~`

    -   不包含正则表达式的URI
    -   只要在这个模式下匹配到, 就不会再向下搜索

-   `@`

    -   `error_page`

### 修改访问资源路径

#### `root`

设置请求的根目录, 可在`http` , `server` , `location`

```nginx
root path ;
```

默认

```nginx
# 因为配置文件和html文件的目录级别就是nginx和html文件夹本身同级, 需要的资源是html下的
root html ;
```

path为Nginx服务器接收到请求后朝朝资源的根目录路径

```nginx
location /source {
    #  /usr/share/nginx/html 是yum默认配置的资源路径
    #  已知/usr/share/nginx/html/source 下存放了一张hi.png
    root  /usr/share/nginx/html ;
}
```

```mermaid
graph LR
centos:80/source/hi.png --> 在服务器上查找文件`/usr/share/nginx/html/source/hi.png`
```

#### `alias`

更改的location的URI, 在`location`

```nginx
alias path ;
```

无默认值

path为修改后的路径

```nginx
location /source {
    #  已知/usr/share/nginx/html/image 下存放了一张hello.png
    alias  /usr/share/nginx/html/image ;
}
```

```mermaid
graph LR
centos:80/source/hello.png --> 在服务器上查找文件`/usr/share/nginx/html/image/hello.png`
```

注意

```nginx
location /source/ {
    #  已知/usr/share/nginx/html/image 下存放了一张hello.png
    alias  /usr/share/nginx/html/image  ;
}
```

```mermaid
graph LR
centos:80/source/hello.png --> 在服务器上查找无法文件`/usr/share/nginx/html/imagehello.png`
```

```nginx
location /source/ {
    #  这样就能解决上面的问题, root没有这个要求
    alias  /usr/share/nginx/html/image/ ;
}
```

#### 区别

`alias`和`root`都可以用来指定访问资源的路径, 那么两者之间的区别是什么?

### `index`

设置网页的默认首页, 在 `http`, `server`, `location`

该URI不需要附带文件的后缀名之类了, 或者说, 不需要URI路径和文件名匹配了

```nginx
location view {
    index /usr/share/nginx/html/view/view_index.html ;
}
```

默认`index index.html`

### `error_page`

设置网站错误页面, 可在`http` , `server`, `location`

```nginx
error_page code ...[=[response]] uri ;
```

当出现响应码code之后, 转向处理

在合适的文件夹下存放错误页面

#### 跳转到具体的URL地址

```nginx
error_page 404 https://www.baidu.com ;
```

### 跳转到指定location

```nginx
error_page 500 501 502 503 504 /50x.html ;
location =/50x.html {
    root /usr/share/nginx/html/errors
}
```

#### `@`完成错误信息展示

```nginx
error_page 404 @2error ;
location @2error {
    default_type text/plain;
    return 404 'Not Found' ;
}
```

和"跳转到指定location"的区别在于, 不会进行重定向改变客户端显示的URI, 用户也无法通过URI直接访问错误界面

#### 可选项`=response`

将响应代码更换成另外一个

```nginx
error_page 404 =403 /50x.html ;
location =/50x.html {
    root /usr/share/nginx/html/errors;
}
```

