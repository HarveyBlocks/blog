MyISAM 速度快, 不支持外键, 事务

InnoDB 支持外键, 支持事务

`show engines;` 查看有何存储引擎可用

```sql
-- 设置存储引擎
CREATE TABLE table ENGINE = InnoDB;
-- 修改存储引擎
ALTER TABLE table ENGINE = MyISAM;
```

