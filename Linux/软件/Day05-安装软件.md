# 软件安装

- Linux-CentOS常见软件安装包.rpm

## yum命令

- 用于安装配置Linux软件,并可以自动解决依赖问题

- 需要root权限
- 需要联网

### 语法

```bash
yum [-y] [install/remove/search] 软件名称
```

- -y 自动确认,无需手动确认安装或卸载过程
- install 安装
- remove 卸载
- search 搜索

### 安装wget

## wget安装

```text
enp0s3: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500
        inet 10.134.38.130  netmask 255.255.224.0  broadcast 10.134.63.255
        inet6 fe80::f1e5:e932:c97f:88f8  prefixlen 64  scopeid 0x20<link>
        ether 08:00:27:de:25:c6  txqueuelen 1000  (Ethernet)
        RX packets 44258  bytes 4074727 (3.8 MiB)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 49945  bytes 16327713 (15.5 MiB)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0
```

```properties
TYPE="Ethernet"
PROXY_METHOD="none"
BROWSER_ONLY="no"
#BOOTPROTO="dhcp"
DEFROUTE="yes"
IPV4_FAILURE_FATAL="no"
IPV6INIT="yes"
IPV6_AUTOCONF="yes"
IPV6_DEFROUTE="yes"
#IPV6_FAILURE_FATAL="no"
IPV6_ADDR_GEN_MODE="stable-privacy"
NAME="enp0s3"
UUID="8d070a77-02e6-48fa-9dc1-4841f2ca9cf5"
DEVICE="enp0s3"
#ONBOOT="yes"
BOOTPROTO="static"
IPADDR="10.134.38.130"
NETMASK="255.255.224.0"
GATEWAY="10.134.38.2"
ONBOOT=yes
DNS1=114.114.114.114
```

##apt命令为Ubuntu安装软件



### 在windows上安装Ubuntu环境

```bash
wsl --install
```

等待

install successful之后重启计算机

win界面搜Ubuntu,点击图标

等待

提示输入用户名密码

等待

完成

```bash
sudo passwd
```

改一下root的密码,否则虽然普通用户有权限(windows给你搞的), 但都要有sudo

也可以

```bash
sudo su - root
```

输入用户密码就行,不需要root密码



###使用apt

Ubuntu的软件安装包时`.deb`的文件, 与centos的不同



```bash
apt [-y] [install/remove/search] 软件名称
```



