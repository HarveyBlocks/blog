# Keepalived

-   软件
-   C编写
-   最初为LVS负载均衡软件设计
-   通过VRRP协议实现高可用

## VRRP

>   Virtual Route Redundancy Protocal 虚拟路由冗余协议

将两台或多态路由器设备虚拟成一个设备, 搞一个虚拟的路由和IP地址

优先级高者, 为Master, 处理一切请求, 发布自己的健康状态

优先级低者, 为Backup(备份), 监听Master的健康状态, Master不健康, Backup竞争

健康=心跳检测



## 安装

1.  [keepalived](https://keepalived.org)

2.  `keepalived-2.0.20.tar.gz`

3.  ```shell
    mkdir keepalived;
    ```

4.  ```shell
    tar -zxf keepalived-2.0.20.tar.gz -C keepalived/
    ```

5.  ```shell
    cd keepalived/keepalived-2.0.20
    ./configure --syconf=/etc --prefix=/usr/local
    make && make install
    ```



yum也有, 不过版本很低, 17年的版本

yum的keepalived有关文件目录

```shell
/etc/selinux/targeted/active/modules/100/keepalived

/etc/sysconfig/keepalived

/etc/keepalived
	keepalived.conf # configuration

/var/lib/yum/yumdb/k/968518e3d5082f089e4eb2dd2f9690546e1b2a84-keepalived-1.3.5-19.el7-x86_64

/usr/sbin
	keepalived # exec

/usr/lib
	systemd/system/
		keepalived.service
	python2.7/site-packages/sos/plugins/
		keepalived.py
		keepalived.pyc
		keepalived.pyo

/usr/share/
	doc/keepalived-1.3.5
		keepalived.conf.SYNOPSIS
		/samples
			keepalived.conf.HTTP_GET.port
			keepalived.conf.IPv6
			keepalived.conf.SMTP_CHECK
			....
	man/man5/
		keepalived.conf.5.gz
		keepalived.8.gz
	augeas/lenses/dist/
		keepalived.aug

/usr/libexec/keepalived

```

## 使用

```shell
cd /etc/keepalived/keepalived.conf # 配置文件
cd /usr/sbin/keepalived # 二进制指向文件
```

## 配置

改文件的时候记得备份

```shell
# global全局部分：
global_defs {
   # 通知邮件，当keepalived发送切换时需要发email(真人)给具体的邮箱地址
   notification_email {
     Aaa@email.com											# 需改, 真人
     Bbb@email.com											# 需改, 真人
   }
   # 设置发件人的邮箱信息
   notification_email_from Ccc@email.com 					# 需改, 真人
   # 指定smpt服务地址
   smtp_server 192.168.200.1
   # 指定smpt服务连接超时时间
   smtp_connect_timeout 30
   # 运行keepalived服务器的一个标识，可以用作发送邮件的主题信息, 随意, 不重复
   router_id LVS_DEVEL										# 需改, 唯一
   
   # 默认是不跳过检查。检查收到的VRRP通告中的所有地址可能会比较耗时，设置此命令的意思是，如果通告与接收的上一个通告来自相同的master路由器，则不执行检查(跳过检查)
   vrrp_skip_check_adv_addr
   # 严格遵守VRRP协议。
   vrrp_strict
   # 在一个接口发送的两个免费ARP之间的延迟。可以精确到毫秒级。默认是0
   vrrp_garp_interval 0
   # 在一个网卡上每组na消息之间的延迟时间，默认为0
   vrrp_gna_interval 0
}
```

```yml
# VRRP部分，该部分可以包含以下四个子模块
# 1. vrrp_script
# 2. vrrp_sync_group
# 3. garp_group
# 4. vrrp_instance
# 我们会用到第一个和第四个，
# 设置keepalived实例的相关信息，VI_1为VRRP实例名称, 			可改
vrrp_instance VI_1 {
	# 有两个值可选MASTER主 BACKUP备????既然有优先级的配置, 为什么又要配置MASTER角色?
    state MASTER  										# 需改, 看节点
    # vrrp实例绑定的接口，用于发送VRRP包[当前服务器使用的网卡名称]
    interface ens33
    # 指定VRRP实例ID，范围是0-255
    virtual_router_id 51
    # 指定优先级，优先级高的将成为MASTER
    priority 100										# 需改, 看节点
    # 指定发送VRRP通告的间隔，单位是秒
    advert_int 1		
    # vrrp之间通信的认证信息
    authentication {
    	# 指定认证方式。PASS简单密码认证(推荐)
        auth_type PASS	
        # 指定认证使用的密码，最多8位
        auth_pass 1111	
    }
    # 虚拟IP地址设置虚拟IP地址，供用户访问使用，可设置多个，一行一个
    virtual_ipaddress { 
        192.168.200.18 # 互相有关系的keepalived, 虚拟IP应该一样
    }
}
```





## 启动

两台服务器都要装keepalived,都要配置, 启动后 Master上会有一个虚拟IP, 然后两边都启动

和启动顺序无关, Master的选择仅与优先级有关

```shell
keepalived
```

## 自动切换脚本

Nginx挂机了, 告诉Keepalive, 让Keepalive也挂机, 才能唤醒Backup

```shell
# 配置在`vrrp_instance`之前
vrrp_script 脚本名称 
{
	stript "脚本文件位置"
	# 间歇指向脚本的事件, 单位s
	interval 3
	# 动态调整vrrp_instance的优先级
	weigth -20
	# 减二十 √
	# 负二十 ×
	# 就算修好了这台服务器, 也要防止启动之后抢占Master, 导致VIP的不断切换, 造成资源的浪费
	# 降低起优先级
}
```



### 编写shell脚本

```shell
#!/bin/bash

# ps 进程
# -C nginx 软件名
# --no=header 没有column名
# wc -l 按列计数
num=`ps -C nginx --no=header | wc -l`
# nginx是否正常
if [ $num -eq 0 ]; then
	# 不正常
	# 再启动一遍
	/usr/sbin/nginx
	sleep 2
	num=`ps -C nginx --no=header | wc -l`
	if [ $num -eq 0 ]; then
		# 还不正常?
		# 宕机
		killall keepalived
	fi
fi
```



