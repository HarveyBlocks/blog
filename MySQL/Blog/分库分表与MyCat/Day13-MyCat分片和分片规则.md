# 垂直分库

## 分类分析

>   shoping逻辑库下:

-   用户类表																一类库对应一个节点
    -   用户信息表
    -   用户住址
        -   省字典表
        -   市字典表
        -   区字典表
-   订单表                                                                     二类库对应一个节点
    -   商品信息表
    -   订单信息表
-   商品表                                                                     三类库对应一个节点
    -   物品表
    -   描述表
    -   品牌表



## 创建库和表

每个 节点,每个表都创建shopping库,即每个服务器的shopping库创建各自的表

## 配置

-   核心配置

    -   配置逻辑库,配置逻辑表,指定每一个逻辑库锁对应的数据节点

    -   不要妄想我能翻译得准确,能一一对应

        ```xml
        <?xml version="1.0"?>
        <!DOCTYPE mycat:schema SYSTEM "schema.dtd">
        <mycat:schema xmlns:mycat="http://io.mycat/">
        
            <schema name="SHOPING" checkSQLschema="true" sqlMaxLimit="100">
        
                <!--用户表-->
                <table name = "tb_user_msg" dataNode="dn1" primaryKey="id"/>
                <table name = "tb_user_address" dataNode="dn1" primaryKey="id"/>
                <table name = "tb_user_province" dataNode="dn1" primaryKey="id"/>
                <table name = "tb_user_city" dataNode="dn1" primaryKey="id"/>
                <table name = "tb_user_regin" dataNode="dn1" primaryKey="id"/>
                
                <!--订单表-->
                <table name = "tb_order_items" dataNode="dn2" primaryKey="id"/>
                <table name = "tb_order_msg" dataNode="dn2" primaryKey="id"/>
                
        
                <!--商品表-->
                <table name = "tb_goods_desc" dataNode="dn3" primaryKey="id"/>
                <table name = "tb_goods_brand" dataNode="dn3" primaryKey="id"/>
                <table name = "tb_goods_items" dataNode="dn3" primaryKey="id"/>        
        
                <!--垂直分库不用配置分页规则-->
        
            </schema>
        
            <dataNode name="dn1" dataHost="localhost1" database="shoping" />
            <dataNode name="dn2" dataHost="localhost2" database="shoping" />
            <dataNode name="dn3" dataHost="localhost3" database="shoping" />
        
            <dataHost name="localhost1" maxCon="1000" minCon="10" balance="0"
                      writeType="0" dbType="mysql" dbDriver="jdbc" switchType="2"  slaveThreshold="100">
                <heartbeat>show slave status</heartbeat>
                <writeHost host="master"
                           url="jdbc:mysql://192.168.200.210:3306?useSSL=false&amp;serverTimezone=Asia/Shanghai&amp;characterEncoding=utf8"
                           user="root" password="123456"/>
            </dataHost>
            <dataHost name="localhost2" maxCon="1000" minCon="10" balance="0"
                      writeType="0" dbType="mysql" dbDriver="jdbc" switchType="2"  slaveThreshold="100">
                <heartbeat>show slave status</heartbeat>
                <writeHost host="master"
                           url="jdbc:mysql://192.168.200.213:3306?useSSL=false&amp;serverTimezone=Asia/Shanghai&amp;characterEncoding=utf8"
                           user="root" password="123456"/>
            </dataHost>
            
            <dataHost name="localhost3" maxCon="1000" minCon="10" balance="0"
                      writeType="0" dbType="mysql" dbDriver="jdbc" switchType="2"  slaveThreshold="100">
                <heartbeat>show slave status</heartbeat>]
                <writeHost host="master"
                           url="jdbc:mysql://192.168.200.214:3306?useSSL=false&amp;serverTimezone=Asia/Shanghai&amp;characterEncoding=utf8"
                           user="root" password="123456"/>
            </dataHost>
        
        </mycat:schema>
        ```

-   用户配置

    ```xml
    <user name="root" defaultAccount="true">
    	<property name="password">123456</property>
        <!--多个数据库之间以逗号分隔-->
        <property name="schema">MY_SCHEMA01</property>
        <!--表级权限设置-->
    </user>
    ```

    

## 测试与产生的问题

### 多路由的多表查询产生错误

>   已知**省市区的信息**在**数据节点1**, 如果在**数据节点3**的**商品信息表**中有**省市区编号**
>
>   现在想要把**数据节点3**的商品信息表中**省市区的编号**转变为其真实的地域名
>
>   我们需要**多表联查**
>
>   但是,表**省市区的信息**和表**商品信息表**不在同一个数据库下, 甚至都不在同一个服务器下
>
>   **这个多表查询能成功吗???**

1.  直接使用MySQL查询控制台是肯定不能成功的

2.  使用MyCat报错:

    ```mysql
    ERROR 1064 (HY000): invalid route in sql, multi tables found but datanode has no intersection 
    ```

    `SQL 中的路由无效，找到多个表，但 Datanode 没有交集 `

    一句多表查询涉及了多个没有交集的数据库

-   解决:

    将被多个路由需要的表设定为**全局表**

    即将省市区数据字典表,就将其设置为全局表

#### 全局表

-   全局表将在任意分片中存在,且完全相同
-   一改全改,一加全加,一删全删
-   字典表常常作为全局表



-   配置方法:

    1.  **三个分片数据节点都设置为全局表的数据节点**
    2.  **物理的数据库**也要每个都**创建一样的数据表**
    3.  将表的类型设置成全局

    ```xml
    <!--字典表,全局表-->
    <table name = "tb_user_province" dataNode="dn1,dn2,dn3" primaryKey="id" type="global"/>
    <table name = "tb_user_city"  dataNode="dn1,dn2,dn3" primaryKey="id" type="global"/>
    <table name = "tb_user_regin" dataNode="dn1,dn2,dn3" primaryKey="id" type="global"/>
    ```



# 水平拆分

tb_log

## 需求

>   不论表有多大,都要均匀地分散在三个分片中



## 配置

-   核心配置参考入门, 入门也是水平拆分,dataHost不用变

```xml
<schema name="SHOPING" checkSQLschema="true" sqlMaxLimit="100">
    <!--日志表-->												<!--增加分片规则-->
    <table name = "tb_log" dataNode="dn4,dn5,dn6" primaryKey="id",role="?"/>
</schema>
```

```xml
<dataNode name="dn4" dataHost="localhost1" database="log" />
<dataNode name="dn5" dataHost="localhost2" database="log" />
<dataNode name="dn6" dataHost="localhost3" database="log" />
```



-   用户配置参考入门,增加一个log库

```xml
<user name="root" defaultAccount="true">
	<property name="password">123456</property>
    <property name="schema">log</property>
    <property name="readOnly">true</property>
</user>
```

-   分片规则:均匀分布?取模分片!`mod-long`,也就是取余



# 分片规则

>   常见的九种分片规则



## 不同的表要用同一个分片规则

>   不要这么死脑筋嘛

```xml
<tableRule name="tb_user_auto-sharing-long">
	<rule>
    	<columns>user_id</columns>
        <!--根据id分配-->
        <algorithm>rang-long</algorithm>
        <!--使用算法rang-long,引用分派你函数function标签-->
    </rule>
</tableRule>
<tableRule name="tb_order_auto-sharing-long">
	<rule>
    	<columns>order_id</columns>
        <!--根据id分配-->
        <algorithm>rang-long</algorithm>
        <!--使用算法rang-long,引用分派你函数function标签-->
    </rule>
</tableRule>
```







## 范围分片

>auto-sharing-long
>
>rang-lang



上面使用了`rule="auto-sharing-long"`,引用了conf/rule.xml

-   rule.xml

    ```xml
    <tableRule name="auto-sharing-long">
    	<rule>
        	<columns>id</columns>
            <!--根据id分配-->
            <algorithm>rang-long</algorithm>
            <!--使用算法rang-long,引用分派你函数function标签-->
        </rule>
    </tableRule>
    <funcion name="rang-lang" 
             class="io.mycat.route.function.AutoPartitionByLong">
        <property name="mapFile">autopartition-long.txt</property>
    </funcion>
    ```

-   conf/autopartition-long.txt

    ```txt
    # range start-end,data node index
    # K=1000,M=10000.
    0-500M=0
    500-1000M=1
    1000-1500M=2
    ```

    500M->五百万

    看data node index数据节点索引

    所以id>500万的会去第二个库(**没必要有五百万个数据**)

    超过一千五百万就报错:

    ```
    ERROR 1064 (HY000): can't find any valid datanode :TB_ORDER -> ID -> 15000001
    ```

    在autopartition-long.txt继续写就可以解决这个问题



## 取模分片

>   mod-long
>
>   mod-long

-   rule.xml

    ```xml
    <tableRule name="mod-long">
    	<rule>
        	<columns>id</columns>
            <!--根据id取余之后的结果决定将数据放入哪个分片-->
            <algorithm>mod-long</algorithm>
        </rule>
    </tableRule>
    <funcion name="mod-long" 
             class="io.mycat.route.function.PartitionByMod">
        <!--取余的除数取决于配置的count-->
        <property name="count">3</property>
    </funcion>
    ```

-   a%count=0->db1

    a%count=1->db2

    a%count=2->db3



## 一致性Hash

>   sharding-by-murmur
>
>   murmur

-   依据Hash来判断落于哪个分片

-   对于没法取余的数据(如uuid)

-   相比于取模分片, 就算后期再增加分配篇,只要UUID相同,一个数据依旧会落在那个分片上

-   rule.xml

    ```xml
    <tableRule name="sharding-by-murmur">
    	<rule>
        	<columns>id</columns>
            <algorithm>murmur</algorithm>
        </rule>
    </tableRule>
    <funcion name="murmur" 
             class="io.mycat.route.function.PartitionByMod">
        <!--种子默认是0-->
        <property name="seed">0</property>
        <!--要分片的数据库节点数量,必须指定,否则没有办法分片-->
        <property name="count">3</property>
        <!--一个实际的数据库节点被映射为这么多虚拟节点,默认为160倍数,也就是虚拟节点数是物理节点数的160倍-->
    	<property name="virtualBucketTimes">160</property>
        <!--节点的权重,没有指定权重的节点默认是1-->
        <!--<property name="weightMapFile">1</property>-->
    </funcion>
    ```




## 枚举分片

>   sharding-by-intfile-enumstatus
>
>   hash_int

-   通过再配置文件中**配置可能的枚举值**,指定数据**分布到不同的数据节点**上

-   适用于按照省份,性别,状态拆分数据等业务

-   适合用枚举类型的就适合用枚举分片

-   rule.xml

    ```xml
    <tableRule name="sharding-by-intfile-enumstatus">
    	<rule>
            <!--判断的字段-->
        	<columns>status</columns>
            <algorithm>hash_int</algorithm>
        </rule>
    </tableRule>
    <funcion name="hash_int" 
             class="io.mycat.route.function.PartitionByFileMap">
        <!--默认节点,当出现我们指定的枚举之外的值,将去到默认节点-->
        <!--默认是2,表示向第三分片-->
        <property name="defaultNode">2</property>
        <!--设置枚举的文本文件-->
        <property name="mapFile">partition-hash-int.txt</property>
    </funcion>
    ```

    

-   partition-hash-int.txt

    ```txt
    1=0
    2=1
    3=2
    ```

    枚举值=分片的索引 

## 应用指定

>sharding-by-substring

-   运行阶段,由应用自主决定路由到那个分片
-   直接**根据字符子串**(内容必须是数字,但是是字符串)计算分片

```xml
<tableRule name="sharding-by-substring">
	<rule>
        <!--判断的字段-->
    	<columns>id</columns>
        <algorithm>sharding-by-substring</algorithm>
    </rule>
</tableRule>
<funcion name="sharding-by-substring" 
         class="io.mycat.route.function.PartitionDirectBySubSting">
    <!--开始索引,0,1,2,3....-->
    <property name="startIndex">0</property>
    <!--截取长度-->
    <property name="size">2</property>
    <!--分片数量-->
    <property name="partitionCount">3</property>
    <!--默认分片-->
    <property name="defaultPartition">2</property>
</funcion>
```

-   0104312->1
-   0041247->0
-   0241123->2
-   0100022->1
-   0423532->4

## 固定Hash算法

-   对二进制进行操作
-   取id与**0x03ff**(10个1)进行**位与**运算(取出低10位)
-   根据位于**运算的结果**([0,1023])决定会把数据放入那个分片
-   例如:[0,255]->0,[256,511]->1,[512,1023]->2,

```xml
<tableRule name="sharding-by-long-hash">
	<rule>
        <!--判断的字段-->
    	<columns>id</columns>
        <algorithm>sharding-by-long-hash</algorithm>
    </rule>
</tableRule>
<funcion name="sharding-by-long-hash" 
         class="io.mycat.route.function.PartitionByLong">
    <!--Count和Lengeh之间的长度必须相同-->
    <property name="partitionCount">2,1</property>
    <!--分片长度,默认最大1024,即默认把所有的的数据集中到一个分片-->
    <property name="partitionLength">256,512</property>
    <!--加起来必须是1024-->
</funcion>
```

-   2,1;256,512表示:256+256+512=1024,即[0,255],[256,511],[512,1023]

    依此创造一个数组做映射:

    ![image-20231211152247072](D:\IT_study\blog\MySQL\assets/Day13-MyCat分片和分片规则/image-20231211152247072.png)



### 和取模分片的区别:

| id       | 0    | 1    | 2    | 3    | 4    | 5    | 6    | 7    | 8    |
| -------- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- |
| 分片结果 | 0    | 1    | 2    | 0    | 1    | 2    | 0    | 1    | 2    |



| id       | 0    | 1    | 2    | 3    | 4    | 5    | 6    | 7    | 8    |
| -------- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- |
| 分片结果 | 0    | 0    | 1    | 1    | 2    | 2    | 0    | 0    | 1    |

-   当批量插入id**连续的数据**时,固定分片Hash算法**不会**在**每个分片之间不断转变**,而取模每一个id(连续的)会不断转变分片





## 字符串Hash解析算法

-   解决字符串无法进行Hash算法



原理:

1. 截取子字符串
2. 算Hash值
3. &1023



```xml
<tableRule name="sharding-by-stringhash">
	<rule>
        <!--判断的字段-->
    	<columns>id</columns>
        <algorithm>sharding-by-stringhash</algorithm>
    </rule>
</tableRule>
<funcion name="sharding-by-stringhash" 
         class="io.mycat.route.function.PartitionBySubSting">
    <!--
		值分片的分布[0,255]在第0个分片,
		每个被分到的长度是一致的,
		所以partitionLength*partitionCount总等于1024
	-->
    <property name="partitionLength">256</property>
    <!--分片数量-->
    <property name="partitionCount">4</property>
    <!--
		0:2截取0,1,2;
		0:0表示从0截取到length位,但length位是没有的;
		0:-1表示从0到最后的全字符串
	-->
    <property name="hashSlice">0:2</property>
</funcion>
```

## 按(天)日期分片

-   配置几天位一组
-   一组里的天分完了换一个分片
-   几组都轮完了就从头

 

```xml
<tableRule name="sharding-by-date">
	<rule>
        <!--判断的字段-->
    	<columns>create_time</columns>
        <algorithm>sharding-by-date</algorithm>
    </rule>
</tableRule>
<funcion name="sharding-by-date" 
         class="io.mycat.route.function.PartitionByDate">
    <!--定义时间格式-->
    <property name="dataFormate">yyyy-MM-dd</property>
    <!--开始日期(包含)-->
    <property name="sBeginDate">2022-01-01</property>
    <!--结束日期(包含)-->
    <property name="sBeginDate">2022-01-30</property>
    <!--十天为一个分片-->
    <property name="sPartionDate">10</property>
    <!--加起来必须是1024-->
</funcion>
```

-   配置分片的节点数量必须和分片规则数量一致,或者分片数只能多,不能少

     即如果有三个分片的节点,3*sPartionDate>=30 (2022-01-30-2022-01-01)

    如果**配置到01-31**,但是依旧是3*sPartionDate=30 **,就会报错**

-   **但是**,如果**插入的数据**是2022-01-31的话就是就会**重复循环落入第一个分片**

## 按自然月分片

-   循环把数据分配给分片

    ```xml
    <tableRule name="sharding-by-month">
    	<rule>
            <!--判断的字段-->
        	<columns>create_time</columns>
            <algorithm>sharding-by-month</algorithm>
        </rule>
    </tableRule>
    <funcion name="sharding-by-month" 
             class="io.mycat.route.function.PartitionByMonth">
        <!--定义时间格式-->
        <property name="dataFormate">yyyy-MM-dd</property>
        <!--开始月份(包含)-->
        <property name="sBeginDate">2022-01-01</property>
        <!--结束月份(包含)-->
        <property name="sBeginDate">2022-03-31</property>
        <!--一月为一个分片-->
    </funcion>
    ```

-   表的dataNode的分片必须和分片规则数量一致

    例如2022-01-01到2022-12-31一共需要12个分片

    2022-01-01到2022-03-31一共需要三个分片

