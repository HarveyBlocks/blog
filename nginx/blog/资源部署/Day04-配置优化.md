# 配置优化

## `sendfile`

开启高效文件传输模式, 在`http`, `server`, `location`, 多在`http`

```nginx
sendfile on | off
```

**默认关闭, 建议打开**

### 请求静态资源的过程

1.  客户端通过网络接口向服务端发送请求
2.  操作系统将这些客户端的请求传输给服务端应用程序
3.  服务端应用程序会处理这些请求
4.  请求处理完成以后, 操作系统还需要将得到的结果通过网络适配器传递回去

### sendfile的优势

原来, 把服务器磁盘上的文件读(拷贝)内核缓冲区, 再(拷贝)到Nginx, Nginx再把文件拷贝到Socket缓冲区, Socket缓冲区再拷贝到网卡, 多次拷贝, 线程切换

`sendfile`就是操作系统的指令, 既然文件不再Nginx做处理, 那就直接向操作系统发送指令, 文件从**内核缓冲区直接到Socket缓冲区**

### 使用示例

```nginx
http {
    sendfile on ;
    server {
        listen 80;
        server_name localhost；
        location / {
            root html;
            index index.html;
        }
    }
}
```



## 发送数据的两种策略

-   `nodelay`
    -   接收到数据就发送出去
    -   从用户体验方面
-   `nopush`
    -   数据在缓冲区存满之后再发送
    -   从服务器资源方面



在Linux2.5.9之后, 两者是可以兼容的. 故, ==建议都打开==

Linux认为, 一般情况下, 都是能把缓冲区转满的(特别是像视频这种大文件, 还要分好几次发), 小部分情况, 装不满了, 直接发送

毕竟时代变了, 数据量变大了, 网速变快了, 宽带变大了



### `tcp_nopush`

可配置在`http`, `server`, `location`

**必须在`sendfile`打开的情况下生效**

```nginx
tcp_nopush on |off ;
```

**默认off**





### `tcp_nodeplay`

>   delay 延迟

可配置在`http`, `server`, `location`

**必须在`keepalive`打开的情况下生效**

```nginx
tcp_nodelay on | off ; 
```

默认打开

