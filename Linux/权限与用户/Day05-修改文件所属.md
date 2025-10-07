# chown 修改文件的所属用户和用户组

- chown转为root用户打造
- 文件的**所属**,见`ls -l`

## 语法

```Linux
chown [-R] [用户][:][用户组] 文件(夹)路径
```

- -R,对文件夹内文件进行相同规则

## 示例

```Linux
chown root test.txt 设置用户
chown :root test.txt 设置用户组
chown root:Harvey test.txt 设置用户和用户组
chown -R root:root /
```

