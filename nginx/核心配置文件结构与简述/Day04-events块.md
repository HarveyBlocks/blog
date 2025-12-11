# `events`

```nginx
events {
    worker_connections  1024;
}
```

## `accept_mutex`

设置Nginx网络连接的序列化

```nginx
accept_mutex on|off ;
```

默认值`on`

常用于解决惊群问题

### 惊群问题

某一个时刻, 客户端发来一个连接请求

Nginx后台是以多进程的工作模式, 也就是说有多个worker进程会被同时唤醒

但是最终只会有一个进程可以获取到连接

如果每次唤醒的进程数目太多, 就会影响Nginx的整体性能

如果将上述值设置为`on`

将对多个Nginx进程接收连接进行序列号排序

一个个来唤醒接收, 就防止了多个进程对连接的争抢

## `multi_accept`

设置是否允许同时接收多个网络请求

```nginx
multi_accept on|off;
```

**默认`off`, 建议`on`**

如果一个进程只能同时接收一个新的连接

否则, 一个工作进程可以同时接收多个连接

## `worker_connect`

用来配置单个worker进程的最大连接数

```nginx
worker_connections number;
```

默认`512`

这里的连接数不仅仅包括和简短用户建立的连接数, 而是包括所有可能的连接数

number值不能大于操作系统支持打开的最大文件句柄数量

## `user`

上设置选择哪种时间驱动来处理网路信息

```nginx
use method;
```

Nginx效率高即是采用了多路复用

所选择时间处理模型有利于Nginx的优化

-   method的可选值
    -   `select`
    -   `poll`
    -   `epoll` 建议
    -   `kqueue`
    -   `等`

