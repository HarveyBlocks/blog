# 使用

二进制文件在 `\usr\sbin\`

-   启动

    ```shell
    ./nginx
    ```

-   查看帮助

    ```shell
    ./nginx -h
    ```

    

    ```
    [root@harvey-centos sbin]# ./nginx -h
    nginx version: nginx/1.24.0
    Usage: nginx [-?hvVtTq] [-s signal] [-p prefix]
                 [-e filename] [-c filename] [-g directives]
    
    Options:
      -?,-h         : this help
      # 查看版本并退出
      -v            : show version and exit
      # 查看版本和配置并退出
      -V            : show version and configure options then exit
      # 测试配置并退出
      -t            : test configuration and exit
      # 测试配置, 若成功, 则输出配置文件内容, 并退出
      -T            : test configuration, dump it and exit
      # 在测试配置期间将日志信息级别提高到ERROR, 一般是-tq
      -q            : suppress non-error messages during configuration testing
      
      # 向主进程发送信号：`stop`停止、`quit`退出、`reopen`重新打开、`reload`重新加载
      -s signal     : send signal to a master process: stop, quit, reopen, reload
      					- `stop` 无论请求有无, 直接强制关闭
      					- `quit` Worker进程停止接收请求, 处理完请求后关闭
      					- `reopen` 重新打开日志
                        - `reload`  重新加载配置文件
      
      # 设置nginx文件位置
      -p prefix     : set prefix path (default: /etc/nginx/) 
      # 指定错误日志位置
      -e filename   : set error log file (default: /var/log/nginx/error.log)
      # 指定配置文件位置, -tc 测试指定位置的配置文件
      -c filename   : set configuration file (default: /etc/nginx/nginx.conf)
      
      
      # 设置配置文件之外的全局指令
      -g directives : set global directives out of configuration file
    
    
    ```

    

-   查看各种信息

    ```shell
    ./nginx -V
    ```

    小写`v`只有版本信息

    大写`V`有更多信息

-   测试配置

    ```
    [root@harvey-centos sbin]# ./nginx -t
    nginx: the configuration file /etc/nginx/nginx.conf syntax is ok
    nginx: configuration file /etc/nginx/nginx.conf test is successful
    
    ```

## 设为服务

1.  在`/usr/lib/systemed/system`下添加文件`nginx.service`

    ```shell
    vim /usr/lib/systemd/system/nginx.service
    ```

    

2.  在文件内添加以下内容

    ```ini
    [Unit]
    Description=nginx web service
    Documentation=http://nginx.org/en/docs/
    After=network.target
    
    [Service]
    Type=forking
    PIDFile=/usr/local/nginx/logs/nginx.pid
    ExecStartPre=/usr/local/nginx/sbin/nginx -t -c /usr/local/nginx/conf/nginx.conf
    ExecStart=/usr/local/nginx/sbin/nginx
    ExecReload=/usr/local/nginx/sbin/nginx -s reload
    ExecStop=/usr/local/nginx/sbin/nginx -s stop
    PrivateTmp=true
    
    [Install]
    WantedBy=default.target
    ```

3.  进行权限设置

    ```shell
    chmod 755 /usr/lib/systemd/system nginx.server
    ```

4.  yum安装就不用配置这些, 我在这里拷贝一下yum的默认配置

    ```ini
    [Unit]
    Description=nginx - high performance web server
    Documentation=http://nginx.org/en/docs/
    After=network-online.target remote-fs.target nss-lookup.target
    Wants=network-online.target
    
    [Service]
    Type=forking
    PIDFile=/var/run/nginx.pid
    ExecStart=/usr/sbin/nginx -c /etc/nginx/nginx.conf
    ExecReload=/bin/sh -c "/bin/kill -s HUP $(/bin/cat /var/run/nginx.pid)"
    ExecStop=/bin/sh -c "/bin/kill -s TERM $(/bin/cat /var/run/nginx.pid)"
    
    [Install]
    WantedBy=multi-user.target
    
    ```

    

## 设为服务

启动

```shell
systemctl start|stop|restart|reload|status|enable nginx
```

enable开机自启, disable 关闭开机自启

## 启动冲突

yum自动把nginx配置为了服务, 还配置给了环境变量

所有就有了多种启动关闭方式

服务式

```shell
systemctl start nginx
systemctl status nginx
systemctl stop nginx
```

环境变量式(因为二进制文件放在了`/usr/sbin`下)

```shell
nginx start
nginx stop
```

二进制文件式

```shell
/usr/sbin/nignx start
/usr/sbin/nignx stop
```

虽然后两种应该是一模一样, 不冲突的

~~但是, 由于这两个在配置的时候, 默认启动的端口都是80, 而可能是存放pid 的文件地址不同, 或者权限不同的原因~~

~~这就导致了用服务关闭nginx时, 找不到由二进制文件启动nginx时创建的pid 导致无法关闭~~

所以何不直接使用`nginx`呢? 



再次测试就没有这种情况了

