# Linux文件的上传和下载

## Final Shell在linux系统中上传下载

![image-20240101164719455](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Linux/系统/Day06-Linux文件的下载和上传/image-20240101164719455.png)

一言以蔽之, 拖动即可, 双击, 右键, 随便玩

下面对文件操作的权限来源于

![image-20240101164904727](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Linux/系统/Day06-Linux文件的下载和上传/image-20240101164904727.png)

## rz,sz 命令

安装这俩命令

```bash
yum -y install lrzsz
```

下载

```bash
sz ~/pi
```

![image-20240101165331922](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Linux/系统/Day06-Linux文件的下载和上传/image-20240101165331922.png)

查看文件

![image-20240101165402097](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Linux/系统/Day06-Linux文件的下载和上传/image-20240101165402097.png)

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

![image-20240101165643251](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Linux/系统/Day06-Linux文件的下载和上传/image-20240101165643251.png)

`rz`上传命令速度是很慢的(悲), 比拖拽要慢很多

