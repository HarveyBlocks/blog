#  MyCat配置

## 核心配置文件

核心配置文件/usr/local/mycact/conf/schema.xml

```xml
<?xml version="1.0"?>
<!DOCTYPE mycat:schema SYSTEM "schema.dtd">
<mycat:schema xmlns:mycat="http://io.mycat/">

    <!--<schema>标签是顶级结构逻辑库,name:逻辑库的库名,自己指定-->
    <schema name="MY_SCHEMA01" checkSQLschema="true" sqlMaxLimit="100">
        <!--逻辑库下的逻辑表,可以有很多的逻辑表-->
        <table name = "TB_ORDER" dataNode="dn1,dn2,dn3" rule="auto-sharing-long" />
        <!--name:表名自定义         数据节点,与下方dataNode联系              分片规则-->
    </schema>

    <dataNode name="dn1" dataHost="localhost1" database="db01" />
    <!--数据节点名,与上方dataBode联系,唯一-->
    <dataNode name="dn2" dataHost="localhost2" database="db01" />
                    <!--节点主机,名字自定义,有几台,写几个,与下方dataHost联系-->
    <dataNode name="dn3" dataHost="localhost3" database="db01" />
                           <!--主机下的数据库,哪个是逻辑库的分库,数据库名应该是正是存在的数据库名,三位一体方位-->

    <!--节点主机名与上方dataHost联系--><!--最大连接数,最小连接数,连接的负载均衡策略-->
    <dataHost name="localhost1" maxCon="1000" minCon="10" balance="0"
              writeType="0" dbType="mysql" dbDriver="jdbc" 
              switchType="2"  slaveThreshold="100">
                             <!--dbDriver有两种形式:"native"和"jdbc",我们使用jdbc-->
        <heartbeat>show slave status</heartbeat><!--心跳,不用管-->
        <!--数据库的连接信息-->
        <writeHost host="master"
                   url="jdbc:mysql://192.168.200.210:3306?useSSL=false&amp;serverTimezone=Asia/Shanghai&amp;characterEncoding=utf8"
                   user="root" password="123456"/><!--&amp;即转移&-->
        <!--host的值涉及主从复制-->
    </dataHost>
    <dataHost name="localhost2" maxCon="1000" minCon="10" balance="0"
              writeType="0" dbType="mysql" dbDriver="jdbc" 
              switchType="2"  slaveThreshold="100">
        <heartbeat>show slave status</heartbeat>
        <writeHost host="master"
                   url="jdbc:mysql://192.168.200.213:3306?useSSL=false&amp;serverTimezone=Asia/Shanghai&amp;characterEncoding=utf8"
                   user="root" password="123456"/>
    </dataHost>
    <dataHost name="localhost3" maxCon="1000" minCon="10" balance="0"
              writeType="0" dbType="mysql" dbDriver="jdbc" 
              switchType="2"  slaveThreshold="100">
        <heartbeat>show slave status</heartbeat>]
        <writeHost host="master"
                   url="jdbc:mysql://192.168.200.214:3306?useSSL=false&amp;serverTimezone=Asia/Shanghai&amp;characterEncoding=utf8"
                   user="root" password="123456"/>
        <!--IP地址记得改-->
    </dataHost>

</mycat:schema>
```



### 逻辑库属性

```xml
<!--<schema>标签是顶级结构逻辑库,name:逻辑库的库名,自己指定-->
<schema name="MY_SCHEMA01" checkSQLschema="true" sqlMaxLimit="100">
	<!--很多逻辑表-->
</schema>
```

-   name:

    -   库名
    -   **区分大小写**(好不合理)

-   checkSQLschema

    -   是否自动去除数据库名称?true则自动去除

    -   例如:

        ```mysql
        select * from TB_ORDER;
        ```

        当**没有进入到MY_SCHEMA01**数据库时

        ```mysql
        select * from MY_SCHEMA01.TB_ORDER;
        ```

        这么写才合理

        但**自动去除数据库名称为True**时,可以不写前面的数据库名,自动帮你去找数据表

-   sqlMaxLimit

    -   在进行查询操作时(如果不适用**limit**),查询记录的上限(为了性能**不会把所有数据给你查出来**的)
    -   每个数据节点100个

#### 逻辑表属性

```xml
<!--逻辑库下的逻辑表,可以有很多的逻辑表-->
<table name = "TB_ORDER" dataNode="dn1,dn2,dn3" rule="auto-sharing-long" />
<!--name:表名自定义         数据节点,与下方dataNode联系              分片规则-->
```
-   name
    -   逻辑表名
    -   **同一张数据库下应该唯一**
-   dataNode

    -   逻辑表分布在那哪几个数据节点之中
-   用逗号分割
    -   需要和**dataNode标签**中的name**对应**
-   rule
    -   分片规则的名字
    -   分片规则名在**rule.xml**中定义
-   primarykey
    -   逻辑表对应真是表的主键
-   type
    -   逻辑表的类型
    -   有**全局表**和**普通表**
        -   不配默认时普通表
        -   全局表配置为**"global"**

### 数据节点

```xml
<dataNode name="dn1" dataHost="localhost1" database="db01" />
<!--数据节点名,与上方dataBode联系,唯一-->
<dataNode name="dn2" dataHost="localhost2" database="db01" />
     <!--节点主机,名字自定义,有几台,写几个,与下方dataHost联系-->
<dataNode name="dn3" dataHost="localhost3" database="db01" />
                 <!--主机下的数据库,哪个是逻辑库的分库,数据库名应该是正是存在的数据库名,三位一体方位-->
```

-   name
    -   数据节点名
    -   需要和**table标签**中的name**对应**
-   dataHost
    -   数据节关联的节点主机
    -   需要和**dataHost标签**中的name**对应**
-   database
    -   当前这个数据节点,关联的是这个**节点主机的**哪个**数据库**





```xml
<!--节点主机名与上方dataHost联系--><!--最大连接数,最小连接数,连接的负载均衡策略-->
<dataHost name="localhost1" maxCon="1000" minCon="10" balance="0"
          writeType="0" dbType="mysql" dbDriver="jdbc" switchType="2"  slaveThreshold="100">
                        <!--dbDriver有两种形式:"native"和"jdbc",我们使用jdbc-->
	<heartbeat>show slave status</heartbeat><!--心跳,不用管-->
    <!--数据库的连接信息-->
</dataHost>
```

-   name
    -   唯一标识
    -   供上层dataNode标签使用
-   maxCon/minCon
    -   最大/小**连接数**
-   balance
    -   负载均衡策略
    -   可取值为0,1,2,3
    -   涉及**读写分离** 
-   writeType
    -   写操作分发方式
        -   0: 写操作转发到第一个writeHost, 第一个挂了,切换到第二个
        -   1: 写操作随机分发到配置的weiteHost
-   dbDriver
    -   数据库驱动
    -   支持**native**和**jdbc**

#### 数据库连接信息

    <writeHost host="master"
               url="jdbc:mysql://192.168.200.210:3306?useSSL=false&amp;serverTimezone=Asia/Shanghai&amp;characterEncoding=utf8"
               user="root" password="123456"/><!--&amp;即转意&-->
    <!--host的值涉及主从复制-->


## 分页规则配置

>   rule.xml
>
>   定义拆分表的规则

上面使用了`rule="auto-sharing-long"`,引用了conf/rule.xml

-   rule.xml

    ```xml
    <tableRule name="auto-sharing-long">
    	<rule>
        	<columns>id</columns>
            <!--根据id分派你-->
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

    所以id>500万的会去第二个库(没必要有五百万个数据)

    超过一千五百万就报错:

    ```
    ERROR 1064 (HY000): can't find any valid datanode :TB_ORDER -> ID -> 15000001
    ```

    在autopartition-long.txt继续写就可以解决这个问题



## 用户权限配置

>   server.xml

### 配置系统运行的环境信息

```xml
<system>
    <!--是否开启无密码登录,默认为0,表示需要密码;否则为1,表示不需要密码-->
	<property name="nonePasswordLogin">0</property>
    <!--是否启用版本10的握手协议,默认为1,表示启用-->
    <property name="userHandshakeV10">1</property>
    <!--是否启用SQL的实时统计,默认为0,表示关闭,这个可以开起来-->
    <property name="useSqlStat">1</property>
    <!--是否启用全加班一致性检测,默认为0,表示关闭-->
    <property name="useGlobleTableCheck">0</property>  
    <!--MyCat服务端端口号默认为8066-->
    <property name="serverPort">8066</property>
    <!--MyCat管理端端口号默认为9066-->
    <property name="managerPort">9066</property>
</system>
```





### 配置用户的用户权限

```xml
<user name="root" defaultAccount="true">
	<property name="password">123456</property>
    <!--多个数据库之间以逗号分隔-->
    <property name="schema">MY_SCHEMA01</property>
    <!--表级权限设置-->
</user>

<!--用户权限配置-->
<user name="user">
	<property name="password">123456</property>
    <property name="schema">MY_SCHEMA01</property>
    <!--权限设置-->
    <property name="readOnly">true</property>
</user>
```

### 表级权限配置

```xml
				<!--改成true表示实行检查-->
<privileges check="true">
    <schema name="MY_SCHEMA01" dml="0010">
    	<table name="tb01" dml="0000"/><!--就近原则,使用表的权限-->
        <table name="tb02" /><!--使用库的权限-->
        <!--0000和1111分别对应IUSD(增改查删)的权限是否开启-->
    </schema>
</privileges>
```

