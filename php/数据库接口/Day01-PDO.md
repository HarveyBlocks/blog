# PDO

```php
$dbh = new PDO("mysql:host=localhost;dbname=test", 'root', 'password'); // 建立连接
foreach ($dbh->query('SELECT * fromFOO') as $row) { // 检索查询结果
	print_r($row); // 显示结果
    echo "ID: " . $raw['id'] . ", Name: " . $raw['name'] . '<br>';
}
$dbh = null; // 关闭连接
```

## 链接数据库

```php
try {
    $pdo = new PDO("mysql:host=localhost;dbname=test", 'root', 'password'); // 建立连接
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    echo "PDO 链接成功";
    foreach ($dbh->query('SELECT * fromFOO') as $row) { // 检索查询结果
        print_r($row); // 显示结果
    }
    $dbh = null; // 关闭连接
}catch(PDOException $e){
    die("链接失败啦: " . $e->getMessage());
}
```



## 写数据

```php
$stmt = $pdo->prepare("DELETE FROM users where id = :id");
$stmt->execute([':id'=>1]);
echo '数据删除成功';
```

