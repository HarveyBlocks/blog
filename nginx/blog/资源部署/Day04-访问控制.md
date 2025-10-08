# 访问控制

## 跨域问题

### 同源策略

约定, 是浏览器最核心, 也是最基本的安全功能

同源: 协议(http和https不同源) ,域名(IP), 端口相同即为同源

### 跨域问题概念

两台服务器A, B, 如果从服务器A的页面发送异步请求到服务器B获取数据

如果服务器A, B不满足同源策略, 就会出现跨域问题



客户端访问一台端口在80的Nginx-Server,获取到静态资源, (不懂)

```html
<html>
  <head>
        <meta charset="utf-8">
        <title>跨域问题演示</title>
        <script src="jquery.js"></script>
        <script>
            $(function(){
                $("#btn").click(function(){
                        $.get('http://192.168.200.133:8080/getUser',function(data){
                                alert(JSON.stringify(data));
                        });
                });
            });
        </script>
  </head>
  <body>
        <input type="button" value="获取数据" id="btn"/>
  </body>
</html>

```



静态资源到此Nginx访问端口在8080的Server发起请求, 出现跨域问题

```nginx
server{
        listen  8080;
        server_name localhost;
        location /getUser{
                default_type application/json;
                return 200 '{"id":1,"name":"TOM","age":18}';
        }
}
server{
	listen 	80;
	server_name localhost;
	location /{
		root html;
		index index.html;
	}
}
```



![1588004913681](..\..\asset\Day04-访问控制\1588004913681.png)

没有一个`Control-Allow_Origin`的头信息在你的请求资源里



### 解决方案

在被跨域调用处(此处是8080端口的服务器)的头信息里添加

-   `Access-Control-Allow-Origin`
    -   允许跨域访问的源地址信息
    -   可以配置多个, 用逗号分割
    -   使用`*`表示所有
-   `Access-Control-Allow-Methods`
    -   允许跨域范围跟的请求方式
    -   `GET`, `PUT`, `POST`, `DELETE` ...
    -   可以配置多个, 用逗号分割

```nginx
add_header Acess-Control-Allow-Origin http://192.168.0.88:80 ;
add_header Acess-Control-Allow-Method GET,POST,PUT,DELETE ;
```



## 防盗链

对于本网站的静态资源, 是否开放给别的用户使用

```html
<img stc="网址"/>
```

直接去访问别人的图片, 百度的是访问不到的(Maybe), 京东是访问得到的

### 原理

Http的请求头信息`Referer`, 当浏览器向服务器发送请求的时候, 都会带上Referer, 来告诉浏览器该网页是从哪个页面连接过来的

防盗链就是获取到`Referer`, 看看这个网址是不是信任的网站地址, 就能获取资源, 否则就返回403

### 配置

`valid_referers`查看`Referer`和`valid_referers`后面的内容进行匹配

如果**匹配到了就将`$invalid_referer`变量置为0, 没匹配到就置为1**, 匹配过程中不区分大小写

```nginx
valid_referers none | blocked | server_names | string ...;
```

无默认值

可置于`server`, `location`

-   `none`
    -   `Referer`为空, 则被匹配
-   `blocked`
    -   `Referer`不为空, 但是该值被防火墙和代理进行伪装过
    -   如不带`http://`, `https://`等协议头的资源被匹配
-   `sterver_names`
    -   指定具体的域名或IP
-   `string`
    -   支持正则表达式和`*`的字符串
    -   如果是正则表达式, 就以`~`开头
-   以空格隔开的各个参数, 之间是或的关系

```nginx
location ~*\.(png|jpg|gif){
	valid_referers none blocked www.baidu.com 192.168.200.222 *.example.com example.*  www.example.org  ~\.google\.;
	if ($invalid_referer){
		# Nginx的if后需要加空格
        return 403; # 如果没匹配到, 就触发防盗链
    }
    # 匹配成功
    root /usr/local/nginx/html;
}
```

