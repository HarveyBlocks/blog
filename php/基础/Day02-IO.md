# IO

## 文件操作函数

打开文件

```php
fopen();
```

关闭文件

```php
fclose();
```

读取文件

```php
fread();
fgets();
file_get_contents();
```

写入文件

```php
fwrite();
fputs();
file_put_contents();
```

复制文件

```php
copy();
```

删除文件

```php
unlink();
```

重命名（移动）文件

```php
rename();
```

## 目录操作函数

打开一个目录

```php
opendir();
```

读取目录文件列表

```php
readdir();
rewinddir();
```

创建，删除目录

```php
mkdir();
rmdir();
```

获取目录权限掩码

```php
umask();
```

## 文件权限

对于Linux系统, 文件权限可分为`组`, `所有者`, `其他`

有三种属性权限 `读`, `写`, `执行`

