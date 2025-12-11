以管理员身份运行"命令提示符"

```
net start SQL名
net stop SQL名
```

- 一般人的SQL名是MySQL80,我改成了MySQL

客户端连接

双击MySQL 8.0 Command line Client

或

```
mysql [-h 127.0.0.1] [-P 3306] -u root -t
```

