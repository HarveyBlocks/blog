#Linux文件的上传和下载

##Final Shell在linux系统中上传下载

![image-20240101164719455](../shoot/Day06-Linux%E6%96%87%E4%BB%B6%E7%9A%84%E4%B8%8B%E8%BD%BD%E5%92%8C%E4%B8%8A%E4%BC%A0/image-20240101164719455.png)

一言以蔽之, 拖动即可, 双击, 右键, 随便玩

下面对文件操作的权限来源于

![image-20240101164904727](../shoot/Day06-Linux%E6%96%87%E4%BB%B6%E7%9A%84%E4%B8%8B%E8%BD%BD%E5%92%8C%E4%B8%8A%E4%BC%A0/image-20240101164904727.png)

##rz,sz 命令

安装这俩命令

```bash
yum -y install lrzsz
```



下载

```bash
sz ~/pi
```

![image-20240101165331922](../shoot/Day06-Linux%E6%96%87%E4%BB%B6%E7%9A%84%E4%B8%8B%E8%BD%BD%E5%92%8C%E4%B8%8A%E4%BC%A0/image-20240101165331922.png)

查看文件

![image-20240101165402097](../shoot/Day06-Linux%E6%96%87%E4%BB%B6%E7%9A%84%E4%B8%8B%E8%BD%BD%E5%92%8C%E4%B8%8A%E4%BC%A0/image-20240101165402097.png)

上传文件

先删除pi

```bash
rm -f ~/pi
```

上传

```bash
rz
```

弹出窗口

![image-20240101165643251](../shoot/Day06-Linux%E6%96%87%E4%BB%B6%E7%9A%84%E4%B8%8B%E8%BD%BD%E5%92%8C%E4%B8%8A%E4%BC%A0/image-20240101165643251.png)

`rz`上传命令速度是很慢的(悲), 比拖拽要慢很多