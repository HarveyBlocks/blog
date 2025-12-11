该死的DHCP

# VMware Workstation虚拟机配置固定IP

1.  在VMware Workstation中配置IP地址的网关或网段(IP地址的范围)

    ![image-20231231222240072](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Linux/网络/Day05-虚拟机配置固定IP/image-20231231222240072.png)

    ![image-20231231222728988](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Linux/网络/Day05-虚拟机配置固定IP/image-20231231222728988.png)

2.  在Linux系统中手动修改文件, 固定IP

vim编辑配置文件`/etc/sysconfig/network-scripts/ifcfg-ens33`文件

`Esc`+`:`+`set nu`查看行号

BOOTPROTO改成`static`

```properties
BOOTPROTO="static"
```

IP地址,随意

```properties
IPADDR="192.168.88.130"
```

子网掩码,固定

```properties
NETMASK="255.255.255.0"
```

网关, 和VMware一致

```properties
GATEWAY="192.168.88.2"
```

DANS1,设置成网关

```properties
DNS1="192.168.88.2"
```

```
启动 HTTP 代理和 Socks5 代理
vim /etc/profile
#末尾增加一下两行代码
export http_proxy=http://127.0.0.1:7890
export https_proxy=http://127.0.0.1:7890
#保存然后更新配置
source /etc/profile
```

重启网卡

```bash
systemctl restart network
```

```bash
ifconfig
```

![image-20231231224329030](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Linux/网络/Day05-虚拟机配置固定IP/image-20231231224329030.png)

## Mac的富哥就自求多福吧

