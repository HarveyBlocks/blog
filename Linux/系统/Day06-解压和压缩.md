#压缩与解压缩

## tar

>   解压或压缩tar或gzip文件

###压缩文件格式

-   `.tar文件`
    -   称之为`tarball`, 归档文件
    -   简单的将文件组装到一个`.tar`的文件内
    -   并没有太多文件体积的减少(不排除体积增加的情况), 仅仅是简单的封装
-   `.gz文件`
    -   也常见为`.tar.gz`文件, `gzip`格式压缩文件
    -   即使用gzip压缩算法, 将文件压缩到一个文件内
    -   可以极大的减少压缩后的体积
-   `tar`命令可对这两种格式进行压缩和解压缩

###命令

```bash
tar [-z -c -x -v] -f 压缩文件名 [ [-C] 文件路径]
```

-   `-z`

    gzip模式

    **缺省即为tarball模式**

    最好放在最前面

-   解压相关

    -   `-c`

        创建压缩文件 , 用于压缩

-   压缩相关

    -   `-x`

        解压

    -   `-C`

        选择解压目的地, **解压模必须有**

-   `-v` 

    显示压缩, 解压过程, 查看进度

-   `-f`

    要创建的文件, 或要解压的文件

    `-f`**必须**在所有选项中位置**处于最后一个**

###压缩

```bash
tar -cvf test.tar 1.txt 2.txt 3.txt
```

将`1.txt 2.txt 3.txt`压缩到`test.tar`

```bash
tar -zcvf test.tar.gz 1.txt 2.txt 3.txt
```

**以gzip模式**将`1.txt 2.txt 3.txt`压缩到`test.tar.gz`

### 解压

```bash
tar -xvf test.tar
```

解压到当前目录

```bash
tar -xvf test.tar -C ~/testdir
```

解压到`~/testdir`

```bash
tar -zxvf test.tar.gz -C ~/testdir
```

**以gzip模式**解压到`~/testdir`

## zip, unzip

>   解压或压缩zip文件

### 压缩zip

```bash
zip [-r] 压缩目的地文件 被压缩文件列表
```

-   `-r`

    表示递归

    当被压缩文件里有文件夹时使用

### 解压缩unzip

```bash
unzip 待解压的文件 [-d  解压缩目的地]
```





**解压有同名的内容, 会把内容覆盖**