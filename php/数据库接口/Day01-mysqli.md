# mysqli

## 建立数据库

```php
@$db = new mysqli('host', 'username','password', 'db_database');
if (mysqli_connect_errno()){
	echo 'Error: Could not connect to database.Please try again later.';
	exit;
}
```

## 执行查询

```php
$query = "SELECT * FROM books WHERE $searchField LIKE '%$searchWord%'";
$result = $db->query($query);
```

字符转义

```php
mysqli_real_escape_string()
```

使用`stripslashes`反转义

prepared statements 防注入

```php
$stmt = $mysqli->prepare("SELECT * FROM users WHERE username = ? AND password = ?");
$stmt->bind_param("ss", $username, $password); // 自动处理转义
$stmt->execute();
// 得到查询结果的行数（记录数）
$num_results = $result->num_rows;
for ($i=0; $i <$num_results; $i++) {
    // 处理并显示结果
    $row = $result->fetch_assoc();
    // htmlspecialchars
    // 在html中, 比如<, >, 等
    echo htmlspecialchars(stripslashes($row[‘title’]));
    echo stripslashes($row[‘author’]);
    echo stripslashes($row[‘isbn’]);
    echo stripslashes($row[‘price’]);
}
```

## 执行写

```php
$query = "INSERT INTO books VALUES('$isbn', '$author', '$title', '$price')";
$result = $db->query($query);
// 获得受影响的行数（与SELECT的方法不同）
echo $db->affected_rows.' 个字段收到影响';
```

## 面向过程的方式

1. 连接到MySQL
	```php
	mysqli_connect(hostName,userName,dbasePassword)
	```
2. 选择数据库
	```php
	mysqli_select_db(connection, dbaseName)
	```
3. 运行查询并得到结果
	```php
	mysqli_query(sqlQuery, connection)
	```
4. 获取查询结果中的记录数
	```php
	mysqli_num_rows( mysqli_result result )
	```
5. 获得查询结果中的一行
	```php
	$row = mysqli_fetch_assoc($result);
	```
6. 获得一列的值，格式化后输出
	```php
	$row[‘isbn’]，$row[‘title’]
	```



