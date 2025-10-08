# 软连接

- 创建软连接,可以将文件,文件夹连接到其他位置
- 类似于windows的**"快捷方式"**

## 创建软连接

### 语法

```Linux
ln -s 被链接的文件(夹) 软连接的位置
```

- -s 创建软连接

### 示例

```Linux
ln -s /etc/yum.conf ~/yum.conf        链接文件 
ln -s /etc/yum ~/yum                  链接文件夹
```

![image-20231231202010501](../../Kubernetes/assets/Day05-%E8%BD%AF%E8%BF%9E%E6%8E%A5/image-20231231202010501.png)

l表示软连接
