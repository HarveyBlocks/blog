# 用户认证

`ngx_http_auth_basic_module`自带

使用HTTP基本身份验证用户名-密码

## 打开

```nginx
auth_basic string | off;
```

`http`, `server`, `location` ,`limit_except`

开启后服务端返回401

指定字符串显示在界面做提示信息

不同浏览器对内容的展示可能不一样

Edge: 

<img src="../../asset/Day08-%E7%94%A8%E6%88%B7%E8%AE%A4%E8%AF%81/image-20240424235652621.png" alt="image-20240424235652621" style="zoom:80%;" />

这种区别?

## 数据库?文件



```nginx
auth_basic_user_file file;
```

用户, 用户名所在文件

`http`, `server`, `location` ,`limit_except`

密码需要加密, 可以用工具自行生成

```shell
yum install -y httpd-tools
htpasswd -c /usr/local/nginx/conf/htpasswd username # 创建一个新文件记录用户名和密码
htpasswd -b /usr/local/nginx/conf/htpasswd username password # 在指定文件新增一个用户名和密码
htpasswd -D /usr/local/nginx/conf/htpasswd username # 从指定文件删除一个用户信息
htpasswd -v /usr/local/nginx/conf/htpasswd username # 验证用户名和密码是否正确
```

这个要确保username唯一 , 要联系数据库的话, 估计就是UserId做username了

然后用户量大了之后效率不能保证? 

说不定适合面向企业内部员工或管理员
