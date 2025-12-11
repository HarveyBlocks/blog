# SortedSet

>   可排序Set集合

## 介绍

-   每个元素都带有Score属性,基于Score属性对元素排序
-   元素不可重复
-   查询速度快
-   排名从0开始

## 命令

### 添加/更新

```bash
zAdd zSet score member
```

-   若member以存在则更新score值

```bash
centos-redis:0>zAdd stu 79 Jhon
"1"
centos-redis:0>zAdd stu 80 Mike
"1"
centos-redis:0>zAdd stu 63 Mary 99 Salary
"2"
```

### 删除

```bash
zRem zSet member
```

### 获取

#### 排名

```bash
zRank zSet  member
zRevRank zSet  member
```

```bash
centos-redis:0>zRank stu Jhon
"1"
centos-redis:0>zRevRank stu Jhon
"2"
```

#### score值

```bash
zScore zSet member
```

```bash
centos-redis:0>zScore stu Jhon
"79"
```

#### *score值* 范围内的元素

```bash
zRangeByScore zSetmin max
zRevRangeByScore zSet min max
```

越往下, 成绩越高

```bash
centos-redis:0>zScore stu Jhon
"79"
centos-redis:0>zAdd stu 60 Amy 100 Jay
"2"
centos-redis:0>zRangeByScore stu 60 100
1) "Amy"
2) "Mary"
3) "Jhon"
4) "Mike"
5) "Salary"
6) "Jay"
```

#### *排名* 范围内的元素

```bash
zRange zSet last first
zRevRange zSet last first
```

-   前三名

```bash
centos-redis:0>zRevRange stu 0 2
1) "Jay"
2) "Salary"
3) "Mike"
```

### 元素个数

#### 所有元素个数

```bash
zCard zSet
```

```bash\
centos-redis:0>zCard stu 
"6"
```

#### *score值* 范围内的元素个数

```bash
zCount zSet min max
```

```bash
centos-redis:0>zCount stu 0 60
"1"
centos-redis:0>zCount stu 0 59
"0"
```

### 自增自减

```bash
zIncrBy zSet 步长 成员
```

```bash
centos-redis:0>zDecrBy stu 1 Amy
"ERR unknown command `zDecrBy`, with args beginning with: `stu`, `1`, `Amy`, "
centos-redis:0>zIncrBy stu -1 Amy
"59"
```

### 倒置

-   这些命令有关排序的都是默认升序

-   想要降序只要**把ZXXX改成ZREVXXX即可**

    ```
    zRank->zRevRank
    ```

-   这些范围查询都是左闭右闭

## 集合关系

### 交

```bash
zInter zSet1 zSet2
```

### 并

```bash
zUnion zSet1 zSet2
```

### 差

```bash
zDiff zSet1 zSet2
```

