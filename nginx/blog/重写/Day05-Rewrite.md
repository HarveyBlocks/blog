# Rewrite

对URL进行重写

依赖于PCRE的支持

[ngx_http_rewrite_module](https://nginx.org/en/docs/http/ngx_http_rewrite_module.html)

## rewirte指令

用正则匹配用户的URI

成功后, 用于替换URI中被截取内容的字符串

可以匹配多个, 按照顺序依次向下处理

如果被截取的字符串是`http://`或`http://`开头的, 则不会继续向下对URI 进行其他处理, 而是直接返回重写后的URI给客户端

### 语法

```nginx
location /rewrite {
	rewrite ^/rewrite/2baidu\w*$ https://www.baidu.com # /rewrite/2baiduAAA -重定向> 百度
	rewrite ^/rewrite/(test)\w*$ /$1; # /rewrite/test/A -请求转发> /test , [(test)\w* ,\w*被丢弃]
	rewrite ^/rewrite/(demo)\w*$ /$1; # /rewrite/demo/A -请求转发> /demo
}
location /test{
	default_type text/plain;
	return 200 test_success;
}
location /demo{
	default_type text/plain;
	return 200 demo_success;
}
```

### flag

```nginx
rewrite regix $param flag ;
```

flag的可选项: 

-   `last`

    -   得到新URI后, 找同级的`location`

    ```nginx
    location rewrite {
    	rewrite ^/rewrite/(test)\w*$ /$1 last; 
    	rewrite ^/rewrite/(demo)\w*$ /$1 last;
    }
    location /test{
    	default_type text/plain;
    	return 200 test_success;
    }
    location /demo{
    	default_type text/plain;
    	return 200 demo_success;
    }
    
    ```

    访问 `http://192.168.200.133:8081/rewrite/testabc`,能正确访问

-   `break`

    -   得到新URI后, 找URI对应的静态资源(`?/html/test`)

    ```nginx
    location rewrite {
        #/test   /usr/local/nginx/html/test/index.html
    	rewrite ^/rewrite/(test)\w*$ /$1 break;
    	rewrite ^/rewrite/(demo)\w*$ /$1 break;
    }
    location /test{
    	default_type text/plain;
    	return 200 test_success;
    }
    location /demo{
    	default_type text/plain;
    	return 200 demo_success;
    }
    ```

    访问 `http://192.168.200.133:8081/rewrite/demoabc`,页面报404错误

-   `redirect`

    -   重写后的URI找`location` , 状态码为302, 走重定向

    ```nginx
    location rewrite {
    	rewrite ^/rewrite/(test)\w*$ /$1 redirect;
    	rewrite ^/rewrite/(demo)\w*$ /$1 redirect;
    }
    location /test{
    	default_type text/plain;
    	return 200 test_success;
    }
    location /demo{
    	default_type text/plain;
    	return 200 demo_success;
    }
    ```

    访问`http://192.168.200.133:8081/rewrite/testabc`请求会被临时重定向，浏览器地址也会发生改变

-   `permanent`

    -   重写后的URI找`location`, 状态码为301，走重定向

    ```nginx
    location rewrite {
    	rewrite ^/rewrite/(test)\w*$ /$1 permanent;
    	rewrite ^/rewrite/(demo)\w*$ /$1 permanent;
    }
    location /test{
    	default_type text/plain;
    	return 200 test_success;
    }
    location /demo{
    	default_type text/plain;
    	return 200 demo_success;
    }
    ```

    访问`http://192.168.200.133:8081/rewrite/testabc`请求会被永久重定向，浏览器地址也会发生改变



301和302和"搜索引擎优化SEO"有关





## rewrite_log

开启后将以`notice`级别输出到`error_log`

### 位置

`http`

`serer`

`location`

`if`

### 语法

```nginx
rewrite_log on | off ;
```

默认`off`

```nginx
rewrite_log on;
# 小作用域更改配置, 不用担心全局的配置
error_log logs/rewrite.log notice ; 
```

## 域名跳转

重定向吗这不是

```nginx
# 自己server_name后面输入的所有内容都会映射给baidu
rewrite ^(.*) http://www.baidu.com$1;
```

## 域名镜像与镜像网站

### 镜像网站

为了高可用性, 镜像网站用来备份主站的信息, 部署在不同的地区加快响应速度

如果一个域名被限制, 还有镜像网站的域名可以用

### 域名镜像

多个不同的域名跳到同一个域名

不想把所有的请求都转发给同一个Server, 只要一个目录下的资源做一个跳转即可

```nginx
# 自己server_name后面输入的所有内容都会映射给baidu
rewrite ^/user(.*) http://www.baidu.com/user$1;
```

## 独立域名

一个模块享有一个域名

不就是买吗

 ```nginx
server{
	listen 80;
	server_name search.api.cn;
    location /search {
        
    }
    location /item {
        
    }
    location /cart {
        
    }
}
server{
	listen 81;
	server_name search.api.com;
	rewrite ^(.*) http://www.api.cn/search$1;
}
server{
	listen 82;
	server_name item.api.com;
	rewrite ^(.*) http://www.api.cn/item$1;
}
server{
	listen 83;
	server_name cart.api.com;
	rewrite ^(.*) http://www.api.cn/cart$1;
}
 ```



## 访问目录自动加斜杠

访问路径`http://localhost/main`, 而index.html`放在了`?/html/main/index.html`

`main`是个目录, 而不是文件夹下的文件集合(?)

如果访问了路径`/main`没有找到资源, **Nginx会重定向到`/main/`**





```nginx
server_name_in_redirect on | off ;
```





0.8.48之前是`on`, 0.8.48之后改成了`off`

`on`, 自动将请求的IP转化为域名, 

例如配置了`192.168.0.88`, 转成`localhost`

如果没有`server_name`为`localhost`的server, 就会404



`off`不对请求的IP做动作

判断是文件

```nginx
localhost /path {
    root ?/html;
    index index.html;
    if (-d $request_filename){
        rewrite ^(.*)([^/])$ http://$host:$server_port/$1$2/ ;
    }
}
```

## 合并目录

### 搜索引擎优化

>   SEO

利用搜索引擎的搜索规则来提高自己的网站在有关搜索引擎的排名的方式

1.  URL的目录层级不超过三层
    -   有利于搜索引起的搜索和客户端的输入
    -   所有文件在同一目录下造成资源的混乱





要求: 

文件目录: `server/11/22/33/44/55/source.html`

URL路径: `server/11-22-33-44-55/source.html`

```nginx
location /server {
    root ?/html;
    index index.html;
    rewrite ^/server/((\w+)-){4}(\w+)/(\w+)\.(\w+)$ /server/$1/$2/$3/$5/%6.$7 last
	# 问: Nginx对嵌套括号的匹配是怎么样的? 
    # 好的, Nginx的正则表达式不支持{}的匹配, 解决问题
}
```

## 防盗链

触发防盗链之后, 就能返回一个准备好的页面

```nginx
location /images {
    root ?/html;
    valid_referers none blocked;
    if ($invalid_referer){
        #return 403;
        rewrite ^/    ?/images/forbidden.png break;
    }
}

```

