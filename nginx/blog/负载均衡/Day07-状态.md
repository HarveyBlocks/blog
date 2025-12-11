#  状态

代理服务器管理Server的时候, 需要指定各个Server的状态

| 状态         | 概述                                                      |
| ------------ | --------------------------------------------------------- |
| down         | 当前的server暂时不参与负载均衡                            |
| backup       | 标记为备份服务器                                          |
| max_fails    | 允许请求失败的次数, 默认1                                 |
| fail_timeout | 经过max_fails失败后, 服务暂停时间, 默认10s, 不写单位就是s |
| max_conns    | 限制最大的接收连接数                                      |





-   模拟宕机

    ```shell
    firewall-cmd --permanent --remove-port=8003/tcp
    ```

-   模拟恢复

    ```shell
    firewall-cmd --permanent --add-port=8003/tcp
    ```

    

    



## 设置状态

```nginx
upstream lb_test{
	server localhost:8001 down; # 用于停机维修
	server localhost:8002 backup; # 只有8003挺了, 8002才会上, 否则不上
	server localhost:8003 max_fails=10 fail_timeout=1s; # 憋加空格
}
```

