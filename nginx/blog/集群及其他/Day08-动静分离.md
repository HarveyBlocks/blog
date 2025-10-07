# 动静分离

动静分离及动态资源和静态资源的分离

Nginx特别擅长静态资源的部署发布, Tomcat擅长动态资源的发布



## 流程

1.  获取index
2.  从index的`<src>`, `<script> ` 等获取静态资源
3.  用 JS 再次向服务器发送请求, 获取动态资源

## 实现

`html/web/index.html`

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <script src="js/jquery.min.js"></script> <!--js/jquery.min.js=>html/web/jquery.min.js -->
    <script>
        $(function(){
            // 获取资源
           $.get('http://192.168.200.133/demo/getAddress',function(data){
               $("#msg").html(data);
           });
        });
    </script>
</head>
<body>
    <!--静态资源获取方式-->
    <img src="images/logo.png"/>
    <!--动态资源获取-->
    <h3 id="msg"></h3>
    <img src="images/mv.png"/>
</body>
</html>

```



```nginx
upstream webservice{
   server tomcat:8080; # tomcat的host, 各不相同
}
server {
    listen       80;
    server_name  localhost;

	# 动态资源
	location /demo {
		# 看来是不用在路径上准备参数的, 是我误解了, 悲
        proxy_pass http://webservice;
   	}

	# 静态资源
   	location ~/.*\.(png|jpg|gif|js){
        root html/web; # 自己的目录
		# <img src="images/mv.png"/> -> html/web/images/mv.png
        gzip on;
   	}
    location / {
		root   html/web; # 自己的目录, 自己的index
        index  index.html index.htm;
    }
}
```







