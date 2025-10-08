# MySQL

## 5.7版本安装

需要root权限

1.  配置yum仓库

    ```bash
    # 更新密钥
    rpm --import https://repo.mysql.com/RPM-GPG-KEY-mysql-2022
    # 安装MySQL,获取rpm软件安装包,拓展yum库
    rpm -Uvh http://repo.mysql.com//mysql57-community-release-el7-7.noarch.rpm
    ```

2.  yum安装mysql

    ```bash
    yum -y install mysql-community-server
    ```

3.  系统配置,mysql服务名mysqld

    ```bash
    systemctl start mysqld
    systemctl enable mysqld
    systemctl status mysqld
    ```

4.  查看初始密码

    ```bash
    cat /var/log/mysqld.log
    ```

    通过管道符查找信息

    ```bash
    cat /var/log/mysqld.log | grep "temporary password"
    ```

    ![image-20240101192940263](../../Kubernetes/assets/Day06-MySQL%E5%AE%89%E8%A3%85/image-20240101192940263.png)

5.  进入mysql

    ```bash
    mysql -uroot -p
    ```

    输入密码,然后进入mysql页面

6.  设置账号密码

    ```mysql
    alter user 'root'@'localhost' identified by '密码';
    ```

    密码大于8位, 有大写字母,特殊符号, 不能是连续的简单语句123,abc啊?

    -   设置一些简单的密码**(生产环境就别搞)**

        ```mysql
        set global validate_password_policy=LOW;
        -- 密码安全级别降低
        set global validate_password_length=4;
        -- 降低密码要求的最低位
        
        alter user 'root'@'localhost' identified by '12356';
        ```

7.  设置远程登录**(生产环境就别搞)**

    ```mysql
    grant all privilege 
    	on *.* to root@'IP地址,%表示模糊占位符,占多个位' identified '密码' 
    	wirh grant option;
    ```

8.  检查端口

    ```bash
    netstat -anp | grep 3306
    ```

    

## 8.0版本安装

需要root权限

1.  配置yum仓库

    ```bash
    # 更新密钥
    rpm --import https://repo.mysql.com/RPM-GPG-KEY-mysql-2022
    # 安装MySQL,获取rpm软件安装包,拓展yum库
    rpm -Uvh http://repo.mysql.com//mysql80-community-release-el7-2.noarch.rpm
    ```

2.  yum安装mysql

    ```bash
    yum -y install mysql-community-server
    ```

3.  系统配置,mysql服务名mysqld

    ```bash
    systemctl start mysqld
    systemctl enable mysqld
    systemctl status mysqld
    ```

4.  查看初始密码

    ```bash
    cat /var/log/mysqld.log
    ```

    通过管道符查找信息

    ```bash
    grep "temporary password" /var/log/mysqld.log
    ```

    ![image-20240101192940263](../../Kubernetes/assets/Day06-MySQL%E5%AE%89%E8%A3%85/image-20240101192940263-1715341408584.png)

5.  进入mysql

    ```bash
    mysql -uroot -p
    ```

    输入密码,然后进入mysql页面

6.  设置账号密码

    ```mysql
    alter user 'root'@'localhost' identified by '密码';
    ```

    密码大于8位, 有大写字母,特殊符号, 不能是连续的简单语句123,abc啊?

    -   设置一些简单的密码**(生产环境就别搞)**

        这里有一点区别

        ```mysql
        set global validate_password.policy=0;
        -- 密码安全级别降低
        set global validate_password.length=4;
        -- 降低密码要求的最低位
        
        alter user 'root'@'localhost' identified by '123456';
        ```

7.  设置远程登录,并设置远程登录密码**(生产环境就别搞)**

    ```mysql
    -- 第一次设置
    create user 'root'@'%' Identified wirh mysql_native_password by '密码';
    -- 后续修改
    alter user 'root'@'%' Identified wirh mysql_native_password by '密码';
    ```

8.  检查端口

    ```bash
    netstat -anp | grep 3306
    ```
