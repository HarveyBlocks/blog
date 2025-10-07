# Dubbo-Admin

##概述

-   管理平台, 图形化的服务管理页面
-   从注册中心中获取到所有的提供者/消费者进行配置管理
-   前后端分离
    -   前端Vue
    -   后端Springboot

## 功能

-   路由规则
-   动态配置
-   服务降级
-   访问控制
-   权重调整



## 安装

依赖node.js, 去官网下

dubbo-admin在github

###修改配置

在安装目录下: 

`.\dubbo-admin\dubbo-admin-server\src\main\resources`

有`application.properties`

```properties
#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# centers in dubbo2.7
# 修改zookeeper地址
admin.registry.address=zookeeper://centos:2181
admin.config-center=zookeeper://centos:2181
admin.metadata-report.address=zookeeper://centos:2181 

admin.root.user.name=root
admin.root.user.password=root
#group
admin.registry.group=dubbo
admin.config-center.group=dubbo
admin.metadata-report.group=dubbo

admin.apollo.token=e16e5cd903fd0c97a116c873b448544b9d086de9
admin.apollo.appId=test
admin.apollo.env=dev
admin.apollo.cluster=default
admin.apollo.namespace=dubbo

```

### 启动

其实是启动maven项目, 在项目根目录打开powershell

```powershell
mvn clean package
```

花很长很长时间

他还会打印错误栈????他们测试的时候用的, 不用单心

会占用8080的端口, 要注意

启动jar包: `.\dubbo-admin-server\target\dubbo-admin-server-0.1.jar`

```powershell
java -jar ./dubbo-admin-server-0.1.jar
```

然后在前端的地方`.\dubbo-admin-ui`执行

```powershell
npm run dev
```

这个UI会跑在8081

很奇妙的是, 不启动ui, 也有一模一样的ui, 而且8080也能跑

![image-20240406180515088](../assert/Day08-Dubbo%E9%AB%98%E7%BA%A7%E7%89%B9%E6%80%A7/image-20240406180515088.png)

## 使用

### 服务查询

![image-20240406180630628](../assert/Day08-Dubbo%E9%AB%98%E7%BA%A7%E7%89%B9%E6%80%A7/image-20240406180630628.png)

详情->

![image-20240406180844572](../assert/Day08-Dubbo%E9%AB%98%E7%BA%A7%E7%89%B9%E6%80%A7/image-20240406180844572.png)

可以看到服务的端口是==20880==

对于每一个生产者服务, 都需要占用一个端口, 这个端口的默认是20880, 也可以改

这个端口需要在一台机器上唯一

```yaml
dubbo:
  application:
    name: hello-service # 需要唯一
  # 注册中心
  registry:
    address: zookeeper://centos:2181
  # 包扫描
  scan:
    base-packages: com.harvey.dubbo.service.impl
  protocol:
    port: 20881
```

![image-20240406181110749](../assert/Day08-Dubbo%E9%AB%98%E7%BA%A7%E7%89%B9%E6%80%A7/image-20240406181110749.png)

↑第一次访问后消费者被显示



查看元数据

![image-20240406181249145](../assert/Day08-Dubbo%E9%AB%98%E7%BA%A7%E7%89%B9%E6%80%A7/image-20240406181249145.png)

需要在生产者的地方配置



