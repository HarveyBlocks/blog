# 下载

<img src="../../assets/Day02-%E5%8A%9F%E8%83%BD%E7%89%B9%E6%80%A7%E5%92%8C%E5%B8%B8%E7%94%A8%E5%8A%9F%E8%83%BD/image-20240415130550387.png" alt="image-20240415130550387" style="zoom:50%;" />

-   pgp
    -   判断从其他途径获取的Nginx是否被别人进行过更改
    -   使用相同的加密算法pgp和那个Nigix的对比, 一样表示是未被更改的

## Selinux

美国安全局巴拉巴拉

使服务器更安全, 但会增加Nginx的配置

关闭Selinux

```shell
vim /etc/selinux/config
```

```properties
SELINUX=disabled
```

查看状态

```shell
sestatus
```

重启生效

## yum安装

安装前置软件包, 用Yum安装就不用安装这些, 用源码安装要, 源码安装看官网

```shell
yum install -y gcc pcre-devel pcre zlib zlib-devel openssl openssl-devel
```

-   pcre 正则表达式解析
-   zlib 压缩
-   openssl 安全通信

查看安装

```shell
gcc --version
rpm -qa pcre-devel pcre zlib zlib-devel openssl openssl-devel
```

接下来是yum安装

[nginx: Linux packages](https://nginx.org/en/linux_packages.html#RHEL)

```shell
sudo yum install -y yum-utils
```

配置一个yum-util的配置文件

```shell
vim /etc/yum.repos.d/nginx.repo
```

```properties
[nginx-stable]
name=nginx stable repo
baseurl=http://nginx.org/packages/centos/$releasever/$basearch/
gpgcheck=1
enabled=1
gpgkey=https://nginx.org/keys/nginx_signing.key
module_hotfixes=true

[nginx-mainline]
name=nginx mainline repo
baseurl=http://nginx.org/packages/mainline/centos/$releasever/$basearch/
gpgcheck=1
enabled=0
gpgkey=https://nginx.org/keys/nginx_signing.key
module_hotfixes=true
```

```shell
sudo yum install -y yum-utils
```

文件放在

```shell
whereis nginx
```

配置文件在

`\etc\nginx`

 启动的二进制文件在

`\usr\sbin`

```shell
./nginx
```

启动

<img src="../../assets/Day02-%E4%B8%8B%E8%BD%BD%E5%AE%89%E8%A3%85/image-20240416125402251.png" alt="image-20240416125402251" style="zoom:50%;" />

启动成功

# docker

```
docker run -p 443:443 -p 80:80 --name nginx -v /home/nginx/conf/nginx.conf:/etc/nginx/nginx.conf -v /home/nginx/conf/conf.d:/etc/nginx/conf.d -v /home/nginx/log:/var/log/nginx -v /home/nginx/html:/usr/share/nginx/html -v /home/files:/etc/nginx/html/htmlstatic -d nginx:latest
```

