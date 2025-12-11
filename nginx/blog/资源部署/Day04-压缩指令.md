# 压缩

gzip

`http`, `server`, `location`都支持

## 模块

-   `ngx_http_gzip_module` 默认携带
-   `ngx_http_gzip_static_module` 不自带 
-   `ngx_http_gunzip_module` 不自带

## 数据准备

我准备了一张[png图片](big.png), 二进制文件应该更好压缩吧?

![image-20240420212915296](..\..\assets\Day04-压缩指令\image-20240420212915296.png)

## 压缩配置

`ngx_http_gzip_module`

### `gzip`

```nginx
gzip on | off
```

**默认`off`**

### `gzip_types`

配置需要压缩的类型, 可在`html`,`server`, `location`

```nginx
gzip_types mime-type ... ;
```

默认

```nginx
gzip_types text/html ;
```

至于png图片的话, 是

```nginx
gzip_types image/png ;
```

或者是根据需要多配几个

```nginx
gzip_types image/png text/html  text/css text/xml  application/javascript ;
```

全部压缩

但是对于图片视频这种, 本身就是高度压缩, 再要Nginx压缩就是消耗CPU资源

所以不要配置全部压缩

```nginx
gzip_types * ;
```

<img src="..\..\assets\Day04-压缩指令\image-20240420215312717.png" alt="image-20240420215312717" style="zoom: 50%;" />





### `gzip_com_level`

压缩级别, 在`http`, `server`, `location`

```nginx
gzip_comp_level level ;
```

-   `level`
    -   [0-9]
    -   默认1
    -   1压缩最少, 9压缩最多
    -   烧CPU
-   677kb的文件(从[百度](www.baidu,com)上扣下来的文件), 传输76ms , level为1, 压缩到244, 6压缩到208, 传输236ms,  百度压缩到208



### `gzip_vary`

在Gzip压缩之后, 携带`Vary:Accept-Encoding`响应头信息, 主要告诉对方, 发送的数据经过了Gzip处理

```Nginx
gzip_vary on | off ;
```

**默认关闭**

<img src="..\..\assets\Day04-压缩指令\image-20240420221716843.png" alt="image-20240420221716843" style="zoom:50%;" />





打开之前↑





<img src="..\..\assets\Day04-压缩指令\image-20240420220807169.png" alt="image-20240420220807169" style="zoom:50%;" />

这是打开之后的效果↑

### `gzip_buffers`

用于处理请求压缩的缓冲区数量和大小, 依旧可以配置在`http`, `server`, `location`

```nginx
gzip_buffers numbers size ;
```

默认值

```nginx
gzip_buffers 32 4k | 16 8k;
```

推荐使用默认值

### `gzip_disable`

针对不同种类的客户端发起的请求可以选择性地开启和关闭gzip功能

依旧可以配置在`http`, `server`, `location`

```nginx
gzip_disable regex ...;
```

没有默认值

-   `regex`会和客户端的浏览器标志`user-agent`来设置

    -   Edge(Chrome内核)

        <img src="..\..\assets\Day04-压缩指令\image-20240420222822724.png" alt="image-20240420222822724" style="zoom:70%;" />

可以把一些版本低的浏览器过滤掉, 版本低的浏览器不一定支持gzip

### `gzip_http_version`

针对不同的http协议版本, 指定使用Gzip的HTTP最低版本, 以选择性开启Gzip

依旧可以配置在`http`, `server`, `location`



```nginx
gzip_http_version 1.0 | 1.1 ;
```



默认

```nginx
gzip_http_version 1.1 ;
```

建议使用默认



### `gzip_min_length`

过滤掉大小(该大小由响应头`Content-Length`)小于配置, 就不会进行gzip压缩

依旧可以配置在`http`, `server`, `location`

```nginx
gzip_min_length length ;
```

默认

```nginx
gzip_min_length 20 ;
```

-   length单位
    -   bytes(缺省)
    -   k|K
    -   m|M





### `gzip_proxied`

是否对服务端返回的结果进行Gzip压缩

依旧可以配置在`http`, `server`, `location`

是和反向代理相关

```nginx
gzip_proxied command;
```

-   `off` 默认, 不对服务器的响应数据做判断, 否则, 依据投中的信息决定是否开启
-   `expired` 
    -   头中包含`Expires`
-   `no-cache`
    -   下划线? 减号? 减号!
    -   头中包含`Cache-Control:no-cache`
-   `no-store`
    -   下划线? 减号? 减号!
    -   头中包含`Cache-Control:n-store`
-   `private`
    -   头中包含`Cache-Control:private`
-   `no_last_modified`
    -   头中不包含`Last-Modified`
-   `no_etag`
    -   头中不包含`ETag`
-   auth`
    -   头中包含`Authorization`
-   `any`
    -   无条件启用压缩

## gzip配置文件

```shell
torch gzip.conf
```

拷贝配置

```nginx
gzip on;
gzip_types *;
gzip_comp_level 6;
gzip_min_length 1024;
gzip_buffers 4 16K;
gzip_http_version 1.1;
gzip_vary  on;
gzip_disable "MSIE [1-6]\.";
gzip_proxied  off;
```

在主配置文件

```nginx
http {
    include gzip.conf ;
}
```



## 和sendfile的冲突及解决

gzip需要放到nginx上, 做了处理, 再还给操作系统

与sendfile天生的冲突



### 解决方法

在访问之前就把静态文件压缩

压缩成诸如`baidu.js.gz`的文件

使用`gzip_static`



如果要我设计: 

1.  先用sendfile()去找压缩后文件
2.  找不到, nginx就会知道
3.  然后Nginx就会去找压缩前的文件
4.  将未压缩的文件放到Nginx应用里面
5.  Nginx对文件进行压缩
6.  压缩完了之后, 将压缩后文件存到磁盘, 且发送给Socket缓冲区

### 添加`ngx_http_gzip_static`模块

#### 源码安装

1.  查询当前Nginx的配置参数, 保留原有的参数

    ```shell
    nginx -V
    ```

2.  将nginx安装目录下sbin目录中的nginx二进制文件进行备份

    ```shell
    cd /usr/local/nginx/sbin
    mv nginx nginxold
    ```

3.  进入Nginx的安装目录

    ```shell
    cd /root/nginx/core/nginx-1.16.1
    ```

4.  执行`make clean`清空之前编译的内容

    ```shell
    make clean
    ```

5.  使用configure来配置参数+原来用`-V`使用参数

    ```shell
    ./configure --with-http_gzip_static_module
    ```

6.  使用make命令进行编译

    ```shell
    make
    ```

7.  将objs目录下的nginx二进制执行文件移动到nginx安装目录下的sbin目录中

    ```shell
    mv objs/nginx /usr/local/nginx/sbin
    ```

8.  执行更新命令

    ```shell
    make upgrade
    ```



#### yum安装

yum默认安装了`gzip_static_module`

那么怎么增加, 安装其他yum没有安装的模块呢? 

建议重开

```shell
wget http://nginx.org/download/nginx-1.20.1.tar.gz 
tar xf nginx-1.20.1.tar.gz && cd nginx-1.20.1
```







### 压缩静态资源

咦? 那level怎么配置? 

```shell
gzip /usr/share/nginx/html/baidu.js
gzip /usr/share/nginx/html/*.html
```



```shell
[root@harvey-centos ~]# ls /usr/share/nginx/html/
50x.html  baidu.js  index.html
[root@harvey-centos ~]# gzip /usr/share/nginx/html/baidu.js
[root@harvey-centos ~]# ls /usr/share/nginx/html/
50x.html  baidu.js.gz  index.html

```

![image-20240421114751437](..\..\assets\Day04-压缩指令\image-20240421114751437.png)

### 指令语法

先关闭Nginx原有的`gzip`

```nginx
gzip off ;
```



```nginx
gzip_static on|off|always ;
```

依旧可以配置在`http`, `server`, `location`

默认关闭

-   `on` 客户端支持压缩文件就发送
-   `always` 不管客户端支不支持, 统统发送压缩文件

测试, 208KB, 37ms

