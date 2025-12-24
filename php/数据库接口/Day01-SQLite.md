# SQLite

- 既可以使用 PDO进行访问，也提供了一个 原生接口（sqlite3 扩展）
- 如果使用 PDO，在执行查询、插入、更新和删除操作时，SQLite 和MySQL 下的代码几乎是相同的

使用PDO链接SQLite

```php
$pdo = new PDO("sqlite:my_db.sqlite"); // 文件名
```

