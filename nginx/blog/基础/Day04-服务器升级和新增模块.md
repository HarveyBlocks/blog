# 服务器升级和新增模块

1.  准备两个服务器, 不同版本
2.  对压缩包解压缩
3.  安装两个Nginx, 由于默认的文件目录是`/usr/sbin/Nginx`, 和版本无关, 所以还是指定一下

1.  老版本的Nginx进行备份

    ````shell
    mv /usr/sbin/nginx /usr/sbin/nginxold
    ````

2.  将新Nginx , 安装目录编译后的Nginx文件. 拷贝到原阿里的`/usr/sbin/`下

    ```shell
    cp /?????/????/nginx /usr/sbin/nginx
    ```

3.  发送信号USER2给Nginx老版本的master进程

    ```shell
     见笔记/01/Nginx_day01
    ```

4.  发送`quit`给老Nginx对应的Master进程

    ```shell
    /usr/sbin/nginx -s quit
    ```

### 使用Nginx的安装目录的make命令完成升级

在解压后的nginx下执行`make upgrade`

## load_module

在1.19.0?之后的版本, 好像不需要上面麻烦的步骤对Nginx重启

```nginx
load_module /usr/lib/nginx/modules/ngx_http_image_filter_module.so;
```

```

```

