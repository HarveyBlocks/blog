# ZAB协议

>   原子广播协议

- Leader
    - 单一主进程
    - 分发客户端事物请求(写请求)
    - 接收读请求直接处理返回
    - 以事务提交proposal的形式广播到所有的Follower进程
    - 每一个事物分配一个xid
    - 接收每个Follower的事务执行结果, 超过半数Follower响应, 就提交事务并广播其他Server进行数据同步
- Follower
    - Leader将事务操作封装成提案分发给所有Follow er
    - 接收写请求, 转发上交Leader
    - 接收读请求直接处理返回
    - 执行事务操作
    - follower响应个数超过一半, 进行事务提交, 同时让所有的follower进行数据同步
- Observer
    - 没有投票权
    - 用于分担读请求压力
    - 增加Follower(有投票权), 会增加Leader和Follower的通信, 倒置写效率降低

## 两种模式

### 恢复状态

服务启动或Leader崩溃, zookeeper进入恢复模式

选举Leader, leader选出后, 将完成Leader和其他机器的数据同步

当大多数的Server(达到可以进行半数选举的程度)完成和Leader 的同步后, 恢复模式结束

### 关播模式



## ZXIDs

`long long ` -> 64bit

```c
struct zxid {
	epoch: 32; // 纪元. 
    // 每个Leader有唯一Epoch, 
    // 一次选举生成新的Epoch
    // 新leader产生. 更新所有的zkServer的xid和epoch
	xid: 32;   // 事务ID, 递增
}
```

