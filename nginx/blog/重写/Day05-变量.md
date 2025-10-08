# 变量

## 变量的声明与赋值

### 语法

```nginx
set $variable value ;
```

无默认值

-   `variable` 
    -   变量名
    -   不要于Nginx服务预设的全局变量重合

### 位置

`server`, `location`, `if`

## 全局变量

### Rewrite常用全局变量

| 变量               | 说明                                                         |
| ------------------ | ------------------------------------------------------------ |
| $args              | 请求URL中的请求指令。<br>功能和$query_string一样<br>比如http://192.168.200.133:8080?arg1=value1&args2=value2中的"arg1=value1&arg2=value2"<br>就是一大串的字符串, 内容就是`'arg1=value1&arg2=value2'` |
| $http_user_agent   | 变量存储的是用户访问服务的代理信息<br>如果通过浏览器访问，记录的是浏览器的相关版本信息 |
| $host              | 访问服务器的server_name值                                    |
| $document_uri      | 当前访问地址的URI。<br>功能和$uri一样<br>比如http://192.168.200.133/server?id=10&name=zhangsan中的"/server"， |
| $document_root     | 当前请求对应location的root值，如果未设置，默认指向Nginx自带html目录所在位置 |
| $content_length    | 请求头中的Content-Length的值                                 |
| $content_type      | 请求头中的Content-Type的值                                   |
| $http_cookie       | 客户端的cookie信息，可以通过add_header Set-Cookie 'cookieName=cookieValue'来添加cookie数据 |
| $limit_rate        | Nginx服务器对网络连接速率的限制，也就是Nginx配置中对limit_rate指令设置的值，默认是0，不限制。 |
| $remote_addr       | 客户端的IP地址                                               |
| $remote_port       | 客户端与服务端建立连接的端口号                               |
| $remote_user       | 客户端的用户名，需要有认证模块才能获取                       |
| $scheme            | 访问协议                                                     |
| $server_addr       | 服务端的地址                                                 |
| $server_name       | 客户端请求到达的服务器的名称                                 |
| $server_port       | 客户端请求到达服务器的端口号                                 |
| $server_protocol   | 客户端请求协议的版本，比如"HTTP/1.1"                         |
| $request_body_file | 发给后端服务器的本地文件资源的名称                           |
| $request_method    | 客户端的请求方式，比如"GET","POST"等                         |
| $request_filename  | 当前请求的资源文件的路径名                                   |
| $request_uri       | 当前请求的URI，并且携带请求参数<br>比如http://192.168.200.133/server?id=10&name=zhangsan中的"/server?id=10&name=zhangsan" |

### 使用案例

日志格式

```nginx
log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                  '$status $body_bytes_sent "$http_referer" '
                  '"$http_user_agent" "$http_x_forwarded_for"';
```




