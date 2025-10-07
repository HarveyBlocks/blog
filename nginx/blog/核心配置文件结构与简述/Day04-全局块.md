# 全局块

```nginx
# 全局块
# 指令名 指令值
user  nginx;
worker_processes  auto;

error_log  /var/log/nginx/error.log notice;
pid        /var/run/nginx.pid;

```



## `User`

用于配置运行Nnginx服务器的 **Worker** 进程的用户和用户组, 用于限制对资源的访问

例如普通用户就无法访问到root目录下的资源, 例如html页面

### 语法

```nginx
user user_name [group_name];
```

### 默认值

```nginx
user nobody;
```

## `processes`

>工作进程

1.  一个Master多个Worker
2.  Worker接收用户的请求
3.  问:  Master一定要创建Worker进程吗, 能创建多少个进程





### `master_process`

用来指定是否开启工作进程

```nginx
master_process on|off;
```

-   默认`on`
-   在测试的时候只留一个主进程



### `worker_processes`

生成的工作进程的数量

Nginx服务器实现并发处理服务的关键所在

`worker_processes`值越大, 可以支持的并发处理量也越多

```nginx
worker_processes auto|具体数值;
```

数量建议和CPU个数一致

**只有的开启`master_processes`时候才能生效**

##`daemon`

>   设定Nginx是否以守护进程的方式启动



守护式进程式Linux后台执行的一种服务进程, 特点是独立与控制终端, 不会随着终端关闭的停止

```nginx
daemon on ;
```

## `pid`

用来配置Nginx当前master进程的进程号ID存储的文件路径

```nginx
pid filepath ;
```



## `error_log`

配置错误日志存放位置

```nginx
error_log filepath [日志级别] ;
```

可以配置在**全局块, http, server, location**

-   日志级别
    -   debug
    -   info
    -   notice
    -   warn
    -   error
    -   crit 临界
    -   alert 警报
    -   emerg 紧急





## include

引入其他配置文件, 使Nginx的配置层次鲜明

```Nginx
include filepath ;
```

也可以用在`http`块(任何一个位置)里, 引用`server`块, 等

这里就会有一个问题: 如果在被引用文件中有的配置使用的是相对路径, 且引用和被引用的根目录不同, 那么是否有可能找不到文件

或者说, 在include之后解析被引用文件(则要注意以上问题), 还是在include之前解析被引用文件(不用注意以上问题).

这个是否是向`#include`一样简单地复制? (大概吧)