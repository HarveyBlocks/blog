# MySQL数据类型

-   日期类型和字符串类型应加上`''`引号
-   好像是有true-false的布尔值的

## 数

| 类型         | 大小                                     | 范围（有符号）/易得无符号                                    | 用途       |
| :----------- | :--------------------------------------- | :----------------------------------------------------------- | :--------- |
| TINYINT      | 1 Bytes                                  | (-128，127)                                                  | byte       |
| SMALLINT     | 2 Bytes                                  | (-32 768，32 767)                                            | short      |
| MEDIUMINT    | 3 Bytes                                  | (-8 388 608，8 388 607)                                      | 大整数值   |
| INT或INTEGER | 4 Bytes                                  | (-2 147 483 648，2 147 483 647)                              | int        |
| BIGINT       | 8 Bytes                                  | (-9,223,372,036,854,775,808，9 223 372 036 854 775 807)      | long       |
| FLOAT        | 4 Bytes                                  | (-3.402...E+38，-1.175... E-38)，0，(1.175...E-38，3.402... E+38) | float      |
| DOUBLE       | 8 Bytes                                  | (-1.797...E+308，-2.225...E-308)，0，(2.225... E-308，1.797...E+308) | double     |
| DECIMAL      | 对DECIMAL(M,D) ，如果M>D，为M+2否则为D+2 | 依赖于M(精度)和D(标度)的值                                   | 精确定点数 |

```mysql
age tinyInt unsigned,
score double(4,1)-- double(整体长度,小数位数),100.0,4位,故double(4,1)
money DECIMAL(5,2)-- 123.45,M=5,D=2
```

## 日期和时间类型

| 类型      | 大小 ( bytes) | 范围                                                    | 格式                | 用途                     |
| :-------- | :------------ | :------------------------------------------------------ | :------------------ | :----------------------- |
| DATE      | 3             | 1000-01-01/9999-12-31                                   | YYYY-MM-DD          | 日期值                   |
| TIME      | 3             | -838:59:59/838:59:59                                    | HH:MM:SS            | 时间值或持续时间         |
| YEAR      | 1             | 1901/2155                                               | YYYY                | 年份值                   |
| DATETIME  | 8             | 1000-01-01 00:00:00 到 9999-12-31 23:59:59              | YYYY-MM-DD hh:mm:ss | 混合日期和时间值         |
| TIMESTAMP | 4             | 1970-01-01 00:00:01UTC 到(北京时间) 2038-01-19 03:14:07 | YYYY-MM-DD hh:mm:ss | 混合日期和时间值，时间戳 |

## 字符串类型

字符串类型指CHAR、VARCHAR、BINARY、VARBINARY、BLOB、TEXT、ENUM和SET。该节描述了这些类型如何工作以及如何在查询中使用这些类型。

| 类型       | 大小          | 用途                            |
| :--------- | :------------ | :------------------------------ |
| CHAR       | [0,256 bytes) | 定长字符串                      |
| VARCHAR    | [0,64KB)      | 变长字符串                      |
| TINYTEXT   | [0,256 bytes) | 短文本字符串                    |
| TEXT       | [0,64KB)      | 长文本数据                      |
| MEDIUMTEXT | [0,16MB)      | 中等长度文本数据                |
| LONGTEXT   |[0,4GB)| 极大文本数据                    |
| TINYBLOB   | [0,256 bytes) | 不超过 255 个字符的二进制字符串 |
| BLOB       | [0,64KB)      | 二进制形式的长文本数据          |
| MEDIUMBLOB | [0,16MB)      | 二进制形式的中等长度文本数据    |
| LONGBLOB   | [0,4GB)       | 二进制形式的极大文本数据        |

- char(n) 和 varchar(n) 中括号中 n
  - **n 代表字符的个数，并不代表字节个数**
- CHAR 和 VARCHAR 
  - 假设在字符集相同,且该字符集中的每个字符占内存x
  - CHAR 的存储空间在声明了它的容量就固定
    - 如:
    - char(10),size=10*x
    - char(20),size=20*x
  - VARCHAR  的存储空间经过计算(**导致它的性能比CHAR低**)
- blob与test:
  - 带blob的(如:TINYBLOB、BLOB、MEDIUMBLOB 和 LONGBLOB)用于描述二进制数据(如:视频,音频)
  - 带test的(如:TINYTEXT、TEXT、MEDIUMTEXT 和 LONGTEXT)用于描述文本数据

-   字符串比较大小是逐位从高位到低位逐个比较（按ascii码）

## 示例

```mysql
create table employee(
	id int comment '编号',
	worknumber varchar(10) comment '员工工号',
    name varchar(5) comment '姓名',
    gender char(1) comment '性别',
    age tinyint unsigned comment '无负数,小于255',
    idcard char(18) comment '身份证',
    entrydate date comment '入职时间,年月日,不需要时分秒'
)comment '员工表';
```

