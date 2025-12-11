# 环境搭建

## 环境类型

一主一从 or 多主多从

-   一主一从
    -   搭建简单
    -   单机故障危险
    -   适用于测试环境
-   多主多从
    -   搭建麻烦
    -   安全性高
    -   适用于生产环境

## 安装

### 安装方式

-   `minikube`
    -   快速搭建单节点kubernetes工具
-   `kubeadm`
    -   快速搭建kubernetes集群的工具
-   二进制包
    -   自己编译安装
    -   组件之间需要产生证书

### Linux环境准备

1.  查看版本Centos版本, 要求7.5以上

    ```shell
    cat /etc/redhat-release 
    ```

2.  关闭firewalld和iptabels服务, k8s和docker会产生很多iptables规则, 为例让系统规则和他们混淆, 关闭系统规则

    ```shell
    systemctl stop firewalld
    systemctl disable firewalld
    systemctl stop iptables
    systemctl disable iptables

    ```

3.  警用selinux

    ```shell
    getenforce # 查看状态
    vim /etc/selinux/config
    ```

    ```properties
    SELINUX=disabled
    ```

    重启生效

4.  禁用swap分区(虚拟内存分区, 内存用完了, 使用磁盘来代替内存, 对性能产生极大影响), K8S强制要求关闭K8S

    ```shell
    vim /etc/fstab
    ```

    ```properties
    # UUID=af12fc56-22eb-4990-bf75-e31204cc3dff swap                    swap    defaults        0 0
    ```

    重启后生效

    查看是否关闭, swap应全是0

    ```shell
    free -m
    ```

    如果不能关闭swap分区, 就在集群安装过程中通过明确的参数进行配置说明

5.  修改linux内核

    ```shell
    vim /etc/sysctl.d/kubernetes.conf
    ```

    ```properties
    net.bridge.bridge-nf-call-iptables=1
    net.bridge.bridge-nf-call-ip6tables=1
    net.ipv4.ip_forward=1
    ```

    ```shell
    # 重新加载配置
    sysctl -p
    # 加载王条过滤模块
    modprobe br_netfilter
    # 查看网桥过滤模块石佛加载成功
    lsmod | grep br_netfilter

    ```

6.  配置ipvs

    在kubernets有两种代理模式, 一种基于iptables, 一种基于ipvs

    ipvs性能更高

    ```shell
    # 安装ipset和ipvsadm
    yum install ipset ipvsadmin -y

    ```

    添加需要加载的模块写入脚本文件

    ```shell
    cat <<EOF> /etc/sysconfig/modules/ipvs.modules 
    #!/bin/bash
    modprobe -- ip_vs
    modprobe -- ip_vs_rr
    modprobe -- ip_vs_wrr
    modprobe -- ip_vs_sh
    modprobe -- nf_conntrack_ipv4
    EOF

    ```

    并执行脚本

    ```shell
    # 配置文件权限
    chmod 755 /etc/sysconfig/modules/ipvs.modules
    # 执行该脚本文件
    bash /etc/sysconfig/modules/ipvs.modules
    # 查看脚本是否允许成功
    lsmod | grep -e ip_vs -e nf_conntrack_ipv4

    ```

### 相关组件安装

1.  安装Docker, 添加配置文件

    ```shell
    vim /etc/docker/daemon.json
    ```

    ```json
    {
        "exec-opts": ["native.cgroupdriver=systemd"],
    	"registry-mirrors": ["https://t9t6i673.mirror.aliyuncs.com"]
    }

    ```

    -   `exec-opts`: Docekr在默认情况下使用Cgroup Driver为cgroupfs, 而kubernetes推荐使用systemd来代替cgroupfs

2.  安装Kubeadm(v1.28.2), Kubelet(v1.28.2), kubectl(v1.28.2)

    ```shell
    cat <<EOF > /etc/yum.repos.d/kubernetes.repo
    [kubernetes]
    name=Kubernetes
    baseurl=http://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64
    enabled=1
    gpgcheck=0
    repo_gpgcheck=0
    gpgkey=http://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg
    	http://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
    EOF

    ```

    ```shell
    yum install -y kubelet kubeadm kubectl
    ```

    配置kubelet的cgroup

    ```shell
    vim /etc/sysconfig/kubelet
    ```

    ```properties
    KUBELET_CGROUP_ARGS="--cgroup-driver=systemd"
    KUBE_PROXY_MODE="ipvs"
    ```

    ```shell
    systemctl enable kubelet
    systemctl enable kubelet
    systemctl status kubelet
    ```

### 集群初始化

#### 准备群镜像

kubeadm要下载组件, 源在国外

```shell
# 在安装kubernetes集群之前，必须要提前准备好集群需要的镜像，所需镜像可以通过下面命令查看
kubeadm config images list

```

返回推荐版本

	kube-apiserver:v1.28.9
	kube-controller-manager:v1.28.9
	kube-scheduler:v1.28.9
	kube-proxy:v1.28.9
	pause:3.9
	etcd:3.5.9-0
	coredns:v1.10.1
```shell
# 下载镜像
# 此镜像在kubernetes的仓库中，由于网络原因，无法连接，下面提供了一种替代方案
images=(
    kube-apiserver:v1.28.9
    kube-controller-manager:v1.28.9
    kube-scheduler:v1.28.9
    kube-proxy:v1.28.9
    pause:3.9
    etcd:3.5.9-0
    coredns:v1.10.1
)
for imageName in ${images[@]} ; do
	docker pull registry.cn-hangzhou.aliyuncs.com/google_containers/$imageName
	docker tag registry.cn-hangzhou.aliyuncs.com/google_containers/$imageName k8s.gcr.io/$imageName
	docker rmi registry.cn-hangzhou.aliyuncs.com/google_containers/$imageName
done

```

以下内容大困惑, 把想到的都试了一遍 

#### Master

```shell
# 将当前配置到处到配置文件
containerd config dump > /etc/containerd/config.toml

# 修改配置文件/etc/containerd/config.toml， 更改sandbox_image配置
vim /etc/containerd/config.toml
```

```ini
[plugins]
  [plugins."io.containerd.grpc.v1.cri"]
    sandbox_image = "registry.aliyuncs.com/google_containers/pause:3.9"
```

```shell
kubeadm reset -f

kubeadm init \
	--apiserver-advertise-address=192.168.88.141 \
	--image-repository registry.aliyuncs.com/google_containers \
	--kubernetes-version v1.28.9 \
	--service-cidr=10.96.0.0/12 \
	--pod-network-cidr=10.244.0.0/16\
	--v=6

mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

#### Node

有必要

```shell
# 将当前配置到处到配置文件
containerd config dump > /etc/containerd/config.toml

# 修改配置文件/etc/containerd/config.toml， 更改sandbox_image配置
vim /etc/containerd/config.toml
```

```ini
[plugins]
  [plugins."io.containerd.grpc.v1.cri"]
    sandbox_image = "registry.aliyuncs.com/google_containers/pause:3.9"
```

?不知道有没有必要

```shell
systemctl stop kubelet
rm -rf /etc/kubernetes/manifests
kubeadm init \
	--image-repository registry.aliyuncs.com/google_containers \
	--kubernetes-version v1.28.9 \
	--service-cidr=10.96.0.0/12 \
	--pod-network-cidr=10.244.0.0/16 \
	--v=6
```

? 不知道有没有必要

```shell
containerd config default | sudo tee /etc/containerd/config.toml
# 修改 runtime_type 的值
sed -i  '96s/runtime_type.*/runtime_type = "io.containerd.runtime.v1.linux"/' /etc/containerd/config.toml

# 确认修改值
cat -n /etc/containerd/config.toml
systemctl  restart containerd
journalctl  -f -u containerd

```

```shell
kubeadm reset -f # 有必要

# 有必要
systemctl stop kubelet 
rm -rf /etc/kubernetes/kubelet.conf 
rm -rf /etc/kubernetes/pki/ca.crt 
rm -rf /etc/kubernetes/bootstrap-kubelet.conf

# 有必要
kubeadm join 192.168.88.141:6443 \
	--token p27bcz.mb839q1ka626f2aq \
    --discovery-token-ca-cert-hash \
    sha256:70e16ed7c604e57b73f4854d7e47a6caf1a5b53f555f55362ac7a00c9e878bdc \
   	--v=6
```

### 安装网络插件

kubernetes支持多种网络插件, 例如flannel, calico, cannal等

在master节点上安装网络插件, 使用DaemonSet控制器, 会在每个节点上运行

```shell
wget https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml
```

修改配置文件启动fannel

```shell
nerdctl pull registry.aliyuncs.com/google_containers/pause:3.8
nerdctl tag registry.aliyuncs.com/google_containers/pause:3.8  registry.k8s.io/pause:3.8

```

```shell
kubectl apply -f kube-flannel.yml
```

查看节点状态

```shell
kubectl get nodes
```

![image-20240511142744210](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Kubernetes/基础使用/Day01-环境搭建/image-20240511142744210.png)

逆天

## 部署镜像

```shell
# 部署Nginx
kubectl create deployment nginx --image=nginx:latest
# >> deployment.apps/nginx created

# 暴露端口
kubectl expose deployment nginx --port=80 --type=NodePort
# --type=NodePort 集群之外的浏览器也能访问
# >> service/nginx exposed

# 查看服务状态
kubectl get pods,svc
```

![image-20240511143226959](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Kubernetes/基础使用/Day01-环境搭建/image-20240511143226959.png)

-   `80:30991`, 本机的30991暴露

    ![image-20240511144104949](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Kubernetes/基础使用/Day01-环境搭建/image-20240511144104949.png)

