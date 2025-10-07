# Nginx安全隔离

通过代理分开客户端和服务端的连接

在反向代理之前设置防火墙, 仅留一个入口供代理服务器访问

## SSL/TLS流量加密

-   SSL
    -   Secure Sockets Layer
    -   安全套接层
-   TLS
    -   Transport Layer Security
    -   传输层及安全
-   流量劫持
    -   坏人中途把请求截下来, 发送到坏人的Web服务器, 响应坏人的信息





## Nginx添加SSL的支持

`http_ssl_module`

## 配置SSL

`http`, `server`

###开启SSL



```nginx
ssl on | off ;
```

默认`off`

或者



```nginx
server {
    listen 443 ssl; # https 默认监听443端口
}
```

### 配置SSL证书

为当前虚拟主机指定一个带有PEM格式的证书

```nginx
ssl_certificate filepath;
```

配置PEM secret key 文件的路径

```nginx
ssl_certificate_key filepath;
```

### 配置用于SSL会话的缓存

```nginx
ssl_session_cache off | none | [builtin[:size]][shared:name:size]
```

-   `off`
    -   禁止使用会话缓存
    -   客户端不得使用会话
-   `none`
    -   默认值
    -   禁止使用会话缓存
    -   客户端可以重复使用, 但是并不会在缓存中存储会话参数
-   `builtin`
    -   内置OpenSSL缓存
    -   仅在一个一个工作进程中使用
-   `shared`
    -   所有工作进程之间共享缓存
-   `name`
    -   当前缓存名称
-   `size`
    -   缓存大小

#### 会话超时

```nginx
ssl_session_timeout 5m;
```

-   默认5分钟

### 指出允许的密码

密码为OpenSSL支持的格式

```nginx
ssl_ciphers ciphers ;
```

默认: `HIGH:!aNULL:@MD5`



通过shell`openssl ciphers`查看支持的ssl格式

```shell
openssl ciphers
TLS_AES_256_GCM_SHA384:TLS_CHACHA20_POLY1305_SHA256:TLS_AES_128_GCM_SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384:ECDHE-ECDSA-CHACHA20-POLY1305:ECDHE-RSA-CHACHA20-POLY1305:DHE-RSA-CHACHA20-POLY1305:ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:DHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-SHA384:ECDHE-RSA-AES256-SHA384:DHE-RSA-AES256-SHA256:ECDHE-ECDSA-AES128-SHA256:ECDHE-RSA-AES128-SHA256:DHE-RSA-AES128-SHA256:ECDHE-ECDSA-AES256-SHA:ECDHE-RSA-AES256-SHA:DHE-RSA-AES256-SHA:ECDHE-ECDSA-AES128-SHA:ECDHE-RSA-AES128-SHA:DHE-RSA-AES128-SHA:AES256-GCM-SHA384:AES128-GCM-SHA256:AES256-SHA256:AES128-SHA256:AES256-SHA:AES128-SHA
```

### 指定服务器密码是否优先于客户端密码

```nginx
ssl_prefer_server_ciphers on|off
```

**默认关闭, 建议打开**



### 概览

```nginx
ssl_certificate      server.cert;
ssl_certificate_key  server.key;

ssl_session_cache    shared:SSL:1m;
ssl_session_timeout  5m;

ssl_ciphers  HIGH:!aNULL:!MD5;
ssl_prefer_server_ciphers  on;
```
##生成SSL证书

###从阿里云购买SSL证书文件

登录

![image-20240423170332315](..\..\asset\Day07-SSL安全\image-20240423170332315.png)

### 控制台生成

检查是否已经安装openssl

```shell
openssl version
```



```shell
mkdir 目标目录
cd 目标目录

# genrsa 加密算法
# -des3 采用方式(什么鬼话?)
# -out 文件名 指定文件名
# 1024 越长, 加密越细
openssl genrsa -des3 -out server.key 1024
# 输入文件密码
# 确认密码


# 根据.key文件获取.csr文件
openssl req -new -key server.key -out server.csr
# 依据提示往下做
# 也会叫你输入密码, 不必和上面相同, 相同也行

cp server.key server.key.org

# 用rsa对server.key.org文件进行加密
openssl rsa -in server.key.org -out server.key

# 创建密钥文件
openssl x509 -req -days 365 -in server.csr -signkey server.key -out server.crt
```

此方法自己生成的证书, 会有

![image-20240423194944361](..\..\asset\Day07-SSL安全\image-20240423194944361.png)

是因为证书没有第三方认证

对于浏览器来说 , 会自动加http, 而不是https, 所以要使用https协议, 要手动加一下http

要不然就

```nginx
server {
    listen 443 ssl;
    server_name www.harvey.com;
}
server {
	listen 80;
    server_name www.harvey.com;
    location / {
        rewrite ^(.*) https://www.harvey.com$1;
    }
}
```

