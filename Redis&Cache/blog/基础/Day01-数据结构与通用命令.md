# 数据结构

数据类型是正对值的, 键的命名没有限制, 命令也可以做键

```bash
centos-redis:0>set  $)_ 2
"OK"
centos-redis:0>get $)_
"2"
```

## 类型

-   基本类型

    -   String

        ```
        Hello world
        ```

        最简单

    -   Hash

        ```
        {name:"Javk",age:21}
        ```

        类似于结构体啊

        无序, 不可重

    -   List

        [A -> B -> C -> C]

        允许重复, 有序

    -   Set

        ```
        {A,B,C}
        ```

        集合

        无序, 不可重复

    -   SortedSet

        ```
        {A:1,B:2,C:3}
        ```

        类似字典

-   特殊类型

    -   GEO

        ```
        {A:(120.3,30.5)}
        ```

        地址? 

    -   BitMap

        ```
        01001001010000100010100101011101
        ```

    -   HyperLog

        ```bash
        01001001010000100010100101011101
        ```

## 通用命令



### 获取已存在的键

```bash
keys 键名
```

键名支持统配符

```bash
keys *
keys a*
```

模糊查询效率很低, 且redis是单线程的, 雪上加霜



### 删除

```bash
del 键名 [键名...]
```

-   返回删除个数Integer
-   key不存在也能删,看返回值Integer



### 判断一个值是否存在

```
exist 键名
```

-   返回"0"/"1"



### 有效期

#### 设置有效期

-   Exprire给已存在key设置有效期(秒)

```bash
expire 键名 时间(秒)
```

-   返回integer,影响的键数



#### 查看剩余有效期

-   TTL**查看**key的剩余有效期

```bash
TTL 键名
```

-   返回Integer时间 或(integer)-2表已删除 或-1表示永久有效

