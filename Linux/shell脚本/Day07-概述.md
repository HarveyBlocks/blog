# Shell

>   壳

连接操作系统和应用程序

解释型脚本语言

## linux蹄冻的解析器

```shell
cat /etc/shells
```

```shell
/bin/sh
/bin/bash
/usr/bin/sh
/usr/bin/bash
/bin/tcsh
/bin/csh
```



centos默认bash

ubantu使用dash

## 脚本文件

`.sh`



## Hello World

```shell
#!/usr/bin/env bash
# 第一行一般写执行脚本文件的解析器
echo 'Hello World' # 这是注释
```





## 执行

###可执行权限

脚本文件需要具备可执行权限才能执行

```shell
chmod 751 test.txt
```





###启动解析器

`hello.sh` 是文件地址, 相对路径和绝对路径都行

```shell
bash hello.sh
# 或者
source hello.sh
```

-   `bash`是在操作系统正在使用的`bash`解析器启动`bash`
    -   子bash无法影响父环境
    
    ![image-20240409134806860](../shoot/Day07-%E6%A6%82%E8%BF%B0/image-20240409134806860.png)
-   `source`是启动新的`bash`解析器进行解析
    
    -   和操作系统的bash一个等级, 能获取环境变量



以下都能执行

```shell
/root/hello.sh
root/hello.sh
```

不能执行↓

```shell
hello.sh
```

能执行↓

```shell
./hello.sh
```



