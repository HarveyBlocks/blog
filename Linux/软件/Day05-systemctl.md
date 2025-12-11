# systemctl命令启动关闭软件

- 能被systemctl控制的软件被称为**服务**

## 语法

```Linux
systemctl start/stop/status/enable/disable 服务名
```



- start启动
- stop关闭
- status查看状态
- enable开启开机自启
- disable关闭开机自启

## 服务

- NetworkManager 主网络服务
- network 副网络
- firewalld 防火墙

## 第三放软件服务

- 有的第三方软件自动打包systemctl(**自动集成到systemctl中**)
- 有的第三方软件没有打包,我们可以手动打包

装下npt,时间同步软件, httpd, apache的服务器软件

ntp会自动把自己注册为系统的服务(**服务名叫ntpd**)

```bash
systemctl status ntpd
```

查看状态

httpd也会把自己注册为系统服务

