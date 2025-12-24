# 概览

PHP 中的 MySQL 接口专用接口函数方式
有两套函数可以访问 MySQL

- `MySQL` 扩展库, 一套函数名以 `mysql_` 开头
- `MySQLi` 扩展库（支持MySQL 4.1以后的新特性）, 一套函数名以 `mysqli_` 开头

访问各种数据库(PgSQL,Oracle,SQLServer..)
都有不同的接口函数
- `pgsql_`
- `oci_`
- `mssql_`
- ...



统一数据库接口(PDO, PHP Data Objects)

- PDO_MYSQL
- 从PHP 5.1 开始正式成为 PHP 的核心模块

越新的编程接口功能越强大，使用越方便

- MySQL 扩展在 PHP 5.5.0 之后废弃
- MySQLi 兼容性很好
- PDO 需要 PHP 5.1 之后的版本

## 配置

可以在`php.ini`中进行配置

```ini
extension=php_mysql.dll
extension=php_mysqli.dll
extension=php_pdo.dll
extension=php_pdo_mysql.dl
```

用 `phpinfo()` 查看是否正确配置

