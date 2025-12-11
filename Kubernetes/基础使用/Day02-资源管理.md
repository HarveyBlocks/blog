
# 资源管理

## 概念

kubernetes所有的内容都抽象为资源, 

**pod**是最小管理单元, 用户不能直接控制container, container放在pod里 

Kubernetes通过**pod控制器**管理pod的container资源

pod提供服务, **service**访问pod 的服务

![img](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Kubernetes/基础使用/Day02-资源管理/image-20200406225334627.png)

## 资源管理方式

### 介绍和举例

-   命令式对象管理

    -   直接使用命令去操作kubernetes资源

    ```shell
    kubectl run|... nginx-pod --image=nginx:latest --port=80
    ```

-   命令式对象配置

    -   通过命令配置和配置文件去操作kubernetes资源

    ```shell
    kubectl create|patch|... -f nginx-pod.yaml
    ```

    -   `patch` 更新
    -   `-f`: `--filename=[]`
    -   将参数转移到配置文件

-   声明式对象配置

    -   通过apply命令和配置文件去操作kubernetes

    ```shell
    kubectl apply -f ngnix-pod.yaml
    ```

    -   仅用于`create`和`patch`资源
    -   apply: 如果有资源, 就更新资源; 如果没有资源就创建资源

### 比较

| 类型           | 操作对象 | 适用环境 | 优点                                       | 缺点                             |
| :------------- | :------- | :------- | :----------------------------------------- | :------------------------------- |
| 命令式对象管理 | 对象     | 测试     | 简单, 便于查询资源状态和信息               | 只能操作活动对象，无法审计、跟踪 |
| 命令式对象配置 | 文件     | 开发     | 可以审计、跟踪                             | 项目大时，配置文件多，操作麻烦   |
| 声明式对象配置 | 目录     | 开发     | 支持目录操作(目录中的所有配置文件都被操作) | 意外情况下难以调试               |

## 命令式对象管理

### kubectl

kubernetes集群管理工具

-   能对集群本身进行管理,

-   能够在集群上进行容器化应用的安装部署

-   语法: 

    ```shell
    kubectl command type [[name] [flags]]
    ```

    -   `command`: ` create` | `get` | `delete` | `patch`
    -   `type` : `deployment` `pod` `service(svc)`
    -   `name` : 大小写敏感
    -   `flags`: 可选参数

在node节点上运行kubectl需要使用master节点上的.kube文件

```shell
scp -r $HOME/.kube node1: $HOME/
```

### Command

#### get命令使用

```shell
[root@node1 ~]# kubectl get pods
NAME                     READY   STATUS    RESTARTS      AGE
nginx-56fcf95486-qpglp   1/1     Running   1 (31m ago)   85m
[root@node1 ~]# kubectl get pod nginx-56fcf95486-qpglp
NAME                     READY   STATUS    RESTARTS      AGE
nginx-56fcf95486-qpglp   1/1     Running   1 (31m ago)   86m
[root@node1 ~]# kubectl get pod nginx-56fcf95486-qpgl
Error from server (NotFound): pods "nginx-56fcf95486-qpgl" not found
```

参数

-   `-o` : `output` 指定打印形式, 也可以输出到文件
    -   打印格式: json, yaml, name, wide(详细信息)
-   `-n`: 查看指定namespace下的pod, 缺省, 则查看`default`namespace下的pod

#### 基本命令

| 命令         | 命令作用     |
| ------------ | ------------ |
| cluster-info | 显示集群信息 |
| help         | 查看命令帮助 |
| version      | 显示版本信息 |

#### CRUD命令

| 命令    | 命令作用     |
| :------ | :----------- |
| create  | 创建一个资源 |
| edit    | 编辑一个资源 |
| get     | 获取一个资源 |
| patch   | 更新一个资源 |
| delete  | 删除一个资源 |
| explain | 展示资源文档 |

#### 运行和调试

| 命令      | 命令作用                   |
| --------- | -------------------------- |
| run       | 在集群中运行一个指定的镜像 |
| expose    | 暴露资源为Service          |
| describe  | 显示资源内部信息, 挂载点   |
| logs      | 输出容器在 pod 中的日志    |
| attach    | 缠绕, 进入运行中的容器     |
| exec      | 执行容器中的一个命令       |
| cp        | 在Pod内外复制文件          |
| rollout   | 首次展示, 管理资源的发布   |
| scale     | 规模, 扩(缩)容Pod的数量    |
| autoscale | 自动调整Pod的数量          |

#### 其他命令

| 命令  | 命令作用               |
| ----- | ---------------------- |
| apply | 通过文件对资源进行配置 |
| label | 更新资源上的标签       |

### Type

### 资源分类

| 资源分类      | 资源名称                 | 缩写    | 资源作用        |
| :------------ | :----------------------- | :------ | :-------------- |
| 集群级别资源  | nodes                    | no      | 集群组成部分    |
| namespaces    | ns                       | 隔离Pod |                 |
| pod资源       | pods                     | po      | 装载容器        |
| pod资源控制器 | replicationcontrollers   | rc      | 控制pod资源     |
|               | replicasets              | rs      | 控制pod资源     |
|               | deployments              | deploy  | 控制pod资源     |
|               | daemonsets               | ds      | 控制pod资源     |
|               | jobs                     |         | 控制pod资源     |
|               | cronjobs                 | cj      | 控制pod资源     |
|               | horizontalpodautoscalers | hpa     | 控制pod资源     |
|               | statefulsets             | sts     | 控制pod资源     |
| 服务发现资源  | services                 | svc     | 统一pod对外接口 |
|               | ingress                  | ing     | 统一pod对外接口 |
| 存储资源      | volumeattachments        |         | 存储            |
|               | persistentvolumes        | pv      | 存储            |
|               |                          | pvc     | 存储            |
| 源            | configmaps               | cm      | 配置            |
|               | secrets                  |         | 配置            |

### Name

合法的名字

```Regular
[a-z0-9]([-a-z0-9]*[a-z0-9])?
```

## 命令式对象配置

### 配置文件

```yml
apiVersion: v1
kind: Namespace
metadata:
  # dev是取的名字
  name: dev

---

apiVersion: v1
kind: Pod
metadata:
  # nginx-pod是取的名字
  name: nginx-pod
  namespace: dev
spec:
  containers:
    # nginx-containers是取的名字
    - name: nginx-containers
      image: nginx:latest

```

```shell
kubectl create -f nginx.yaml 
# >> namespace/dev created
# >> pod/nginx-pod created

```

```shell
kubectl delete -f nginx.yaml 
# >> namespace "dev" deleted
# >> pod "nginx-pod" deleted

```

## 声明式对象配置

操作同命令式对象配置

