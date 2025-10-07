# 核心配置文件是哪个?

[核心配置文件](D:\IT_study\MySQL\MySQL Server 8.0\my.ini)

D:\IT_study\MySQL\MySQL Server 8.0\my.ini

添加下列文字到[核心配置文件](D:\IT_study\MySQL\MySQL Server 8.0\my.ini)

```ini
log-output=FILE
general-log=1
general_log_file="D:\IT_study\MySQL\mysql.log"
slow-query-log=1
slow_query_log="D:\IT_study\MySQL\mysql_slow.log"
long_query_time=2
```

1.  cmd中输入services.msc
2.  打开服务
3.  重启MySQL
4.  运行(如通过JDBC)SQL
5.  查看日志

![image-20231010203419390](../../Screenshots/Day06/image-20231010203419390.png)