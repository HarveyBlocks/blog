# Hash类型

## 介绍

-   无序
-   每个字段独立, 可对每个字段做curd

## Hash操作

### 增改查

#### 增改

添加一个hash

```bash
HSet Object field value
```

```bash
centos-redis:0>hset user:A id 1
"OK"
centos-redis:0>hset user:A name 'Jack'
"OK"
centos-redis:0>hset user:A age 15
"OK"
centos-redis:0>hset user:A gender '男'
"OK"
```

![image-20240102001912466](../../assetss/Day01-Hash%E7%B1%BB%E5%9E%8B/image-20240102001912466.png)

-   标的是key其实是field

```bash
centos-redis:0>hset user:B id 2 name 'Mary' age 15 gender '女'
"4"
```

![image-20240102002101946](../../assetss/Day01-Hash%E7%B1%BB%E5%9E%8B/image-20240102002101946.png)



**若存在这个field, 则不执行**

```bash
HSetNx Object field value
```

#### 删除字段

```bash
hdel 键 字段
```



```bash
centos-redis:0>hdel user:A age
"1"
```

![image-20240102003610670](../../assetss/Day01-Hash%E7%B1%BB%E5%9E%8B/image-20240102003610670.png)

#### 获取

```bash
HGet Object field
```

```bash
centos-redis:0>hget user:A id
"1"
centos-redis:0>hget user:A name
"Jack"
centos-redis:0>hget user:A gender
"男"
centos-redis:0>hget user:A age
"15"
```



### 批量操作



#### 多个key

 ```bash
HMSet Object field1 Value1 field2 Value2 ..
 ```

有什么意义?



```bash
HMGet Object field1 field2 ...
```

```bash
centos-redis:0>hmget user:A id age name gender
1) "1"
2) "15"
3) "Jack"
4) "男"
```



#### 查一个key

获取一个hash类型的key中的所有filed和value

```bash
HGetAll Object
```

```bash
centos-redis:0>hgetAll user:A
1) "id"
2) "1"
3) "name"
4) "Jack"
5) "age"
6) "15"
7) "gender"
8) "男"
```



获取一个hash类型的key中的所有filed

```bash
HKeys Object
```

```bash
centos-redis:0>hKeys user:A
1) "id"
2) "name"
3) "age"
4) "gender"
```



获取一个hash类型的key中所有的value

```bash
HVals key
```

```bash
centos-redis:0>hVals user:A
1) "1"
2) "Jack"
3) "15"
4) "男"
```



### 自增自减

让一个hash类型key的字段自增长并指定步长

```bash
HIncrBy Object field 步长
```

```bash
centos-redis:0>hVals user:A
1) "1"
2) "Jack"
3) "15"
4) "男"
```



没有`hdecrBy`

```bash
centos-redis:0>hdecrBy
"ERR unknown command `hdecrBy`, with args beginning with: "
```

