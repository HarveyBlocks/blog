
# TODO

- [ ] 集合

  - [ ] 固定大小视图(失败, 文档和测试不匹配)

- [ ] CRUD

  - [ ] 可重试写(可重试写入需要[副本集](https://www.mongodb.com/zh-cn/docs/manual/replication/#std-label-replication)或[分片集群](https://www.mongodb.com/zh-cn/docs/manual/sharding/#std-label-sharding-introduction)，并且**不**支持[独立实例](https://www.mongodb.com/zh-cn/docs/manual/reference/glossary/#std-term-standalone)。)
  - [x] 可重试读(自动执行)
  - [ ] 文本搜索(Search)
  - [x] 地理查询
  - [ ] Update-聚合
  - [ ] Update-MQL

- [ ] MQL

- [ ] 索引

  - [ ] 属性
    - [ ] Hidden
    - [ ] 忽略大小写
    - [ ] ...
  - [ ] 类型
    - [x] 地理空间
      - [x] [GeoJsonType](https://www.mongodb.com/zh-cn/docs/manual/reference/geojson/#multipoint)
    - [ ] 通配符
    - [ ] Hash

- [ ] Search

- [ ] 建模Schema

- [ ] 集群

  - [ ] 写关注(集群)

    本节点的写操作执行之后, 通知给几个节点此操作已完成, 这个写才算真的完成, 才将写入的结果返回

  - [ ] 读关注(集群)

    一次读取操作, 指定读关注级别, 级别表示读出的来的数据是被几个节点确认过的数据

    应当是被几个节点确认已经存在/写入(收到通知), 才可以被查询

  - [ ] 写操作和读操作在主从集群(读写分离)

    主节点处理写关注, 从节点返回确认, 主节点接受到的确认数大于指定要求, 才返回成功给客户端

    由于数据可能被回滚, 因此不能盲目返回给客户端

    主节点存储一条记录的时间戳, 当足够多的从节点确认记录同步, 就更新这个时间戳; 

    当查询请求打到某个从节点, 查到了这条记录, 检查主节点的时间戳, 发现还没更新, 说明没有足够多的节点同步这个记录, 就会等待大家都同步(是否等待可以配置)了, 才会返回

    主节点上的数据总是最新, 因此不处理写关注; 从节点不处理写, 因此也不处理写关注

- [ ] 分片 Slace(横向扩展)

- [ ] 事务

- [ ] 授权

