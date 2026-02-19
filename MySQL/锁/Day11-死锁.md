# 死锁

当一个事务锁的顺序是id=2,id=3，另一个事务锁顺序反过来，并发运行时，就会出现死锁。

死锁出现时，mysql会选择较小的事务进行回滚，并向上报错。

客户端1

```sql
START TRANSACTION;

UPDATE tb_category SET path = '/未分类' WHERE id = 'f1dbe4bf-7e4e-44c8-bffa-834874a79e71';

UPDATE tb_category SET path = '/工作' WHERE id = 'd8992c17-2412-4c5a-ba9c-36ffd42ec8ca';
```

客户端2

```sql
START TRANSACTION;

UPDATE tb_category SET path = '/工作' WHERE id = 'd8992c17-2412-4c5a-ba9c-36ffd42ec8ca';

UPDATE tb_category SET path = '/未分类' WHERE id = 'f1dbe4bf-7e4e-44c8-bffa-834874a79e71';
```



![DeathLock-MySQL_tb_category](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MySQL/锁/Day11-死锁/DeathLock-MySQL_tb_category.svg)

