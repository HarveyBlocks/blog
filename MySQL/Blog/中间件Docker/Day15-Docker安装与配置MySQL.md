# Docker

1.  删除Docker

    ```Bash
    yum remove docker \
     docker-client\
     docker-client-latest\
     docker-common\
     docker-latest\
     docker-latest-logrotate\
     docker-logrotate\
     docker-engine
    ```

2.  下载yum-utils源

    ```bash
    yum install -y yum-utils
    ```

3.  配置Docker的yum源

    ```Bash
    yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
    ```

    ![image-20231212204154850](../../../Java/spring-data/assert/Day15-Docker%E5%AE%89%E8%A3%85%E4%B8%8E%E9%85%8D%E7%BD%AEMySQL/image-20231212204154850.png)

4.  安装Docker

    ```bash
    yum install -y docker-ce-cli contain.io docker-buildx-plugin docker-compose-plugin
    ```

    ![image-20231212204552663](../../../Java/spring-data/assert/Day15-Docker%E5%AE%89%E8%A3%85%E4%B8%8E%E9%85%8D%E7%BD%AEMySQL/image-20231212204552663.png)

    莫慌, 不重要这个

5.  验证是否安装成功

    ```bash
    docker -v
    ```

    ![image-20231212204637270](../../../Java/spring-data/assert/Day15-Docker%E5%AE%89%E8%A3%85%E4%B8%8E%E9%85%8D%E7%BD%AEMySQL/image-20231212204637270.png)

## 启动Docker

-   启动Docker

    ```bash
    systemctl start docker
    ```

-   停止Docker

    ```bash
    systemctl stop docker
    ```

-   重启Docker

    ```bash
    systemctl restart docker
    ```

-   开机自启Docker

    ```bash
    systemctl enable docker
    ```

-   执行docker ps命令,如果不报错,说明安装启动成功

    ```bash
    docker ps
    ```

    

如果启动失败了

![image-20231212212521247](../../../Java/spring-data/assert/Day15-Docker%E5%AE%89%E8%A3%85%E4%B8%8E%E9%85%8D%E7%BD%AEMySQL/image-20231212212521247.png)

就再用下面的命令再下载一次

```bash
yum-config-manager \
    --add-repo \
    http://mirros.aliyun.com/docker-ce/linux/centos/docker-ce.repo
```

```bash
yum install docker-ce docker-ce-cli containerd.io
```

-   验证启动成功

    ```bash
    docker images
    ```



## 配置镜像加速

因为原来默认是国外的地址, 就会很慢

![image-20231224172658595](../../../ES/blog/typora-user-images/Day15-Docker%E5%AE%89%E8%A3%85%E4%B8%8E%E9%85%8D%E7%BD%AEMySQL/image-20231224172658595.png)

![image-20231212214452778](../../../Java/spring-data/assert/Day15-Docker%E5%AE%89%E8%A3%85%E4%B8%8E%E9%85%8D%E7%BD%AEMySQL/image-20231212214452778.png)

把加速器地址配置到Docker

```bash
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://地址.mirror.aliyuncs.com"]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker
```

```
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": [
        "https:/doublezonline.cloud"
  ]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker

```

```conf
nameserver 192.168.2.1
nameserver 114.114.114.114
nameserver 210.34.48.34
```



## Docker配置MySQl

```bash
docker run -d \
 --name mysql \
 --privileged \
 -p 3306:3306 \
 -v $PWD/conf:/etc/mysql/conf.d \
 -v $PWD/logs:/logs \
 -v $PWD/data:/var/lib/mysql \
 -e TZ=Asia/Shanghai \
 -e MYSQL_ROOT_PASSWORD=123456 \
 mysql
```



```
docker run -d \
 --name mysql \
 --privileged \
 -p 3306:3306 \
 -v $PWD/conf:/etc/mysql/conf.d \
 -v $PWD/logs:/logs \
 -v $PWD/data:/var/lib/mysql \
 -e TZ=Asia/Shanghai \
 -e MYSQL_ROOT_PASSWORD=EmiyaShirou3.14159265358979 \
 mysql
```



-   run:

    创建容器并运行

-   -d

    让容器在后台运行

    不加-d会在哪里停止,动不了了,只能输出日志了

-   --name

    给**容器**取名字,唯一

-   -p

    **宿主机端口:容器内端口** 

    给端口做映射

    docker会分配ip地址,外面无法访问

    将端口作为一个接口,通过端口,就可以**间接**访问到

    **容器内端口**是由进程决定的,永远都是3306

    **宿主机端口**,自己选择

-   -e

    环境变量

    `KEY=VALUE`

    环境变量的键和值由镜像的开发者决定

-   mysql

    运行的进程的名字

    docker搜索镜像的依据

    完整写法:

    -   `[repository]:[tag]`
    -   `[镜像名]:[镜像版本]`
    -   不写版本装最新版的

完了,使用123做密码了,不统一了

**然后就可以再图形化界面启动MySQL就行了**

## Docker简单原理

-   当我们利用Docker安装应用时, Docker会自动搜索并下载应用**镜像( image )**.

-   镜像不仅包含应用本身, 还包含应用运行所需的**环境**,**配置**, **系统函数库**.
  
-   因为打包了环境, 所以应用的使用**无关操作系统**, 全部都可以运行*!*
  
-   Docker会再运行镜像时创建一个**隔离环境**, 称为**容器( container )**

    -   Docker部署的应用和其他进程分开, 互不干扰

-   镜像资源之来源: **镜像仓库** 

    -   存储和管理镜像的平台
    -   Docker官方维护了一个公共仓库: [Docker Hub](), 可以凭借这个网站查阅镜像的信息地址
    -   也可以上传自己配置好的镜像

-   流程:

    1.  在启动docker之后
    2.  docker会用**守护线程**等待我们输命令
    3.  依据命令去镜像仓库里找资源
    4.  将其下载
    5.  将应用放入独立的容器

    


