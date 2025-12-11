# 核心配置文件

`nginx.conf`

## 默认文件

```nginx
# 注释

# 全局块
# 指令名 指令值
user  nginx;
worker_processes  auto;

error_log  /var/log/nginx/error.log notice;
pid        /var/run/nginx.pid;

# events 块
# 用来配置与用户的网络连接的相关内容
# 对Nginx的性能影响较大
events {
    worker_connections  1024;
}


# http块
http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    keepalive_timeout  65;

    #gzip  on;

    include /etc/nginx/conf.d/*.conf;
}

```

## Server块简述

```Nginx
http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;

    server {
        listen       80; # 监听端口
        server_name  localhost; # 监听host
        location / { # / 指定了URI路径
            # location资源
            root   html;
            # 默认站点文件位置 `/usr/share/nginx/html/`
            index  index.html /usr/share/nginx/html/index.htm;
        }
        # 当响应状态码是500,502,503,504的时候, 会跳转到该页面
        error_page   500 502 503 504  /usr/share/nginx/html/50x.html;
        location = /usr/share/nginx/html/50x.html {
            root   html;
        }
    }

}
```

## 引号

似乎, 单引号不解析变量(且更快), 双引号会解析变量

