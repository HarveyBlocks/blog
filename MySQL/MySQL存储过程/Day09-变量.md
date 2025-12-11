-   系统变量
    -   非用户定义
    -   会话变量(SESSION)
        -   局部有效
    -   全局变量(GLOBAL)
-   用户自定义变量
    -   局部变量
    -   针对存储过程  

## 系统变量

### 查看

```mysql
show [session|global] variables;                -- 查看所有系统变量
show [session|global] variables like '模糊匹配';
select @@[session.|global.]变量名;					-- 查看指定变量
```

-   默认Session

## 设置

```mysql
set [session|global] @@[session.|global.]变量名;
```

-   重启会恢复默认
-   永久修改在/etc/my.cnf系统配置文件

