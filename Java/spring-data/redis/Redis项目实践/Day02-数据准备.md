# 数据准备

本项目所有数据由**传智教育-黑马程序员**提供, 仅作学习

## Mysql数据导入

[sql文件](资料\hmdp.sql)

在用到时会有对表的详细介绍

-   `tb_user`
    -   用户表
-   `tb_user_info`
    -   用户详情表
-   `tb_shop`
    -   商户信息表
-   `tb_shop_type`
    -   商户类型表
-   `tb_blog`
    -   用户日记表
-   `tb_follow`
    -   用户关注表
-   `tb_voucher`
    -   优惠券表
-   `tb_vocher_order`
    -   优惠券订单表

**mysql5.7以上版本**



## 前后端分离部署

不用微服务啦啦啦(刚好没学)

前端部署在NGINX上

![image-20240102192238092](../../../typora-user-images/Day01-%E7%82%B9%E8%AF%84/image-20240102192238092.png)

## 后端项目

[hm-dianping](资料\hm-dianping.zip)

记得改服务器的host, 用户名密码之类

### 依赖

```xml
<!--redis-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<!--线程池-->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>

<!--spring-web-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!--mysql依赖-->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
    <version>5.1.47</version>
</dependency>
<!--lombok-->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!--mp-->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.4.3</version>
</dependency>
<!--糊涂工具包-->
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>5.7.17</version>
</dependency>
<!--单元测试-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### 测试

`com.harvey.review_system.controller.ShopTypeController`

```
http://localhost:8081/shop-type/list
```

![image-20240102195744090](../../../typora-user-images/Day01-%E7%82%B9%E8%AF%84/image-20240102195744090.png)

## 前端部署

[Nginx安装包](资料\nginx-1.18.0.zip)

解压到一个文件夹

**别放在D盘下! D盘创建文件需要管理员权限!**

以前那些已经在D盘下的文件不知道是怎么跑起来的😓

![image-20240102200050717](../../../typora-user-images/Day01-%E7%82%B9%E8%AF%84/image-20240102200050717.png)

`html`包下, 就有完整的代码了

![image-20240102200150418](../../../typora-user-images/Day01-%E7%82%B9%E8%AF%84/image-20240102200150418.png)

配置文件在`conf/nginx.conf`

前端的项目是做了移动端的

![image-20240102200554610](../../../typora-user-images/Day01-%E7%82%B9%E8%AF%84/image-20240102200554610.png)

可以了
