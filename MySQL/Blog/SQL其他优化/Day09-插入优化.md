
-   批量(500-1000)插入

-   手动提交事务

    ```mysql
    start transaction;
    insert into ...;
    insert into ...;
    insert into ...;
    commit;
    rollback;
    ```

-   主键顺序插入

    -   我直接不insert主键,直接auto_asc会怎么样?

## 大批量插入数据

-   把硬盘里的文件加载入数据库

1.  ```DOS
    mysql --local-infile -u root -p
    ```
	- 客户端连接服务器时 , 加上参数 --local-infile
2.  ```mysql
    set global local_infile = 1;
    select  @@local_infile;
	```
	- 设置全局参数local_infile = 1;
3.  ```mysql
    load data local infile '地址' 
    	into table 表名 
		fields terminated by '列分隔符' 
		lines  terminated by '行分隔符';

	load data local infile 'C:/Users/27970/Desktop/add.txt'
		into table tb_user
		fields terminated by ','
		lines terminated by '\n';

	load data infile './day.txt' 
		into tb_user 
		fields terminated by',' 
		lines terminated by'\n' (
		部分字段    
	);
	```
	- 执行load指令
	- file里的字符串不要加引号!!!!!!!!

-   主键顺序插入的性能高于乱序插入

![image-20231023132140085](../../assets/Day09-插入优化/image-20231023132140085.png)

