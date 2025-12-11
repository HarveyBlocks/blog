# IP地址

```bash
ifconfig
```

![image-20231231220821145](../assets/Day05-IP地址和主机名/image-20231231220821145.png)

# 主机名

查看主机名

```bash
hostname
```

更改主机名

```bash
hostnamectl set-hostname Harvey-CentOS
```

-   需要root权限
-   不支持中文(有中文就直接localhost)
-   大写全小写

![image-20231231205715564](../assets/Day05-IP地址和主机名/image-20231231205715564.png)



重新连接就出现啦

## 域名映射

www.baidu.com -> 百度服务器IP

![image-20231231205944579](../assets/Day05-IP地址和主机名/image-20231231205944579.png)

### 本地私人记事本

windows:`C:\Windows\System32\drivers\etc\hosts`

以管理员身份打开记事本, 然后:

![image-20231231220006332](../assets/Day05-IP地址和主机名/image-20231231220006332.png)

![image-20231231220154550](../assets/Day05-IP地址和主机名/image-20231231220154550.png)



![image-20231231220618527](../assets/Day05-IP地址和主机名/image-20231231220618527.png)

![image-20231231220647406](../assets/Day05-IP地址和主机名/image-20231231220647406.png)

Linux:`/etc/hosts`

以后做集群, 就可以不配置IP使用IP地址连接, 而是使用**本地私人记事本**里配置的域名映射

