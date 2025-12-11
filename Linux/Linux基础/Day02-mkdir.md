# mkdir创建目录命令

Make Directory

```Linux
mkdir [-p] 需要创建的文件夹(绝对,相对)路径
```

路径**必填**(当前路径下就填./)

选项可选,适用于创建连续多层级的目录

## -p

``` linux
mkdir ~Desktop/test/diy
```

Error

no such file or directory

因为 /test是不存在的

所以要使用-p

```linux
mkdir ~Desktop/test/diy
```

## 注意

mkdir命令有修改的**权限**;

所以只能在HOME目录内操作,而不要在HOME外操作

![image-20230928231939496](../assets/Day02/image-20230928211948628.png)

**权限**以后讲

