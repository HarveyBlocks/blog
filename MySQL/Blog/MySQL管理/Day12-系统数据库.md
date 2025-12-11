# 系统数据库

## information_schema

-   提供访问数据库元数据的各种表和视图
    -   包含:
        -   数据库
        -   表
        -   字段类型
        -   访问权限
    -   元数据
        -   我们数据库本身的一些数据

![image-20231027191842418](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MySQL/MySQL管理/Day12-系统数据库/image-20231027191842418.png)

## mysql

-   存储MySQL服务器正常运行所需要的各种信息(时区,主从,用户,权限)

## performance_schema

-   监控MySQL的底村运行状态,用于收集数据库服务器性能参数
-   锁的信息啊之类的

## sys

-   一系列方便DBA和开发人员利用perfprmance_schema 性能数据库进行性能调优和诊断的视图

