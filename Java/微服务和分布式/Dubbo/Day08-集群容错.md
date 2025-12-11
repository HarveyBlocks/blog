# 集群容错

![image-20240406221817459](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/微服务和分布式/Dubbo/Day08-集群容错/image-20240406221817459.png)

## failover(默认)

失败重试

当失败出现, 重试其他服务器, 默认重复两次, 使用retries配置

一般用于读操作, 因为读操作无论执行多少次结果都一样, 写操作就不是这样(悲) 幂等性

重试次数用`retries`配置

## failfast

快速失败

失败了马上报错

用于写操作

## failsafe

**直接忽略**, 不报异常, 返回空结构

不重要的结果

## failback

失败自动恢复

后台记录失败请求, **定时重发**

用于重要操作

## forking

并行调用多个服务器, 只要有一个成功即返回成功

消耗性能

## broadcast

广播调用的所有提供者,逐个调用, 任意一台报错就报错

我估摸着用于测试就不错

还有数据同步要求比较高的, 三台机器的数据都需要更新的情况

## 配置选择

```java
@com.alibaba.dubbo.config.annotation.Reference(loadbalance = FailbackCluster.NAME)
private HelloService helloService;
```

emm, 一个服务有些方法需要Failback, 有些需要Failover咋办?

在服务的提供方配置, 能生效吗?

