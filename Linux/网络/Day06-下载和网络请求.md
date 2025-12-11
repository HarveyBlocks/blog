# 下载和网络请求

## ping命令

>检查服务器是否可以联通

```bash
ping [-c 次数] ip/域名(主机名)
```

-   不指定次数会一直ping下去

```bash
ping www.baidu.com
```

看看自己有没有联通

![image-20240101124240790](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Linux/网络/Day06-下载和网络请求/image-20240101124240790.png)

```bash
ping IP/域名
```

看看这个IP/域名能被自己联通

## wget命令

>   下载文件

```bash
wget [-b] url
```

-   `-b`->后台backend下载, 会将日志写入到当前工作目录wget-log

跟踪下载进度

```bash
tail wget-log
```

持续跟踪下载进度

```bash
tail -f wget-log
```

## curl命令

>   发起网络请求

```bash
curl [-O] url
```

-   `-O` 下载url连接的文件

-   不加`-o`就会返回相关信息

    ![image-20240101125856350](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Linux/网络/Day06-下载和网络请求/image-20240101125856350.png)

    或返回html的源码

