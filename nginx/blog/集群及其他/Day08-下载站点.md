# 下载站点

使用自带模块`ngx_http_autoindex_module`

##配置

### 开启

```nginx
autoindex on|off;
```

默认关闭

`http`, `server`, `location`

###显示大小

要不要展示对应文件的详细大小

```nginx
autoindex_exact_size on|off;
```

默认开启 , 关闭单位KB

`http`, `server`, `location`



###显示格式

设置目录列表(显示给用户的)的格式(1.7.9之后出现)

```nginx
autoindex_format html|xml|json|jsonp;
```

默认`html`

`http`, `server`, `location`



###显示时间

在目录列表上显示的时间

```nginx
autoindex_localtime on | off
```

默认关闭, 显示JMT时间, on , 显示服务器上的时间

`http`, `server`, `location`





## 使用

```nginx
location /download {
    root /usr/share/nginx ; # /usr/share/nginx/download/各种文件
    autoindex on;
    autoindex_exact_size on;
    autoindex_format html;
    autoindex_localtime on;
}
```

