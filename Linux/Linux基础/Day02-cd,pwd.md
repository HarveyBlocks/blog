# cd命令切换工作目录

change directory

```linux
cd [路径]
```

不写路径默认回HOME

``` linux
cd ~
```

也是回HOME





写完整绝对路径:

```linux
cd /    >>>根目录
cd /usr/games >>>game目录
```



不写全会进入工作目录的子目录

```Linux
cd Desktop
```

如果不是该工作目录的子目录(孙子目录也不行),error

```linux
no such file or directory
```



# pwd 打印工作目录命令

Print Work Directory

无参无选项

**注意**:打印**工作目录**,而不是"现所在的文件夹",所以会把绝对路径打印完全

