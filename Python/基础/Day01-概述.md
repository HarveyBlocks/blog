# Python概述



www.python.org

官网好像是荷兰语还是法语啥的

## Linux安装

第三方前置程序

```shell
yum install wget zlib-devel bzip2-devel openssl-devel ncurses-devel readline-devel tk-devel gcc make zlib zlib-devel libffi-devel -y
```



在官网上获取Python的源码(source)

[Python Source Releases | Python.org](https://www.python.org/downloads/source/)

找到版本

右键, 复制链接地址



```shell
wget https://www.python.org/ftp/python/3.10.4/Python-3.10.4.tgz
```

```shell
tar -xvf Python-....tgz
```



```shell
cd Python...
./config --prefix=/usr/local/python
make && make install
cd /usr/local/python/bin
/usr/binpython # 进入python
```

软连接

```shell
rm -f /usr/bin/python
ln -s /usr/local/python3.10.4/bin/python3.10 /usr/bin/python
```

修改yum的配置文件(Python会把yum的配置文件顶替成老版本)

```shell
vi /usr/libexec/urlgrabber-ext-down
```

把`/usr/bin/python`改为`/usr/bin/python2`



```shell
vi /usr/bin/yum
```

把`/usr/bin/python`改为`/usr/bin/python2`



## Python解释器

```
"C:\Users\27970\AppData\Local"
```



## PyCharm





## 关键字

![image-20240302095611412](../assetss/Day01-%E6%A6%82%E8%BF%B0/image-20240302095611412.png)



pass : 方法/ 类/ 循环/ 分支没有实质的代码, 就可以使用pass而不会报错, 到时候就是调用而不会有别的效果

​		对于类来所, 如果一个子类继承了多个父类之后, 不需要更多的方法等(只是建档将父类相加),就可以使用pass

assert

is

nonlocal

yield

## 标识符

-   大小写敏感
-   关键词禁止
-   方法和变量推荐使用**下划线命名法**
-   类名使用驼峰

## 注释

-   单行注释

    ```python
    # 这是注释
    ```

-   多行注释

    ```python
    """我都忘记
    这是多行
    注释了
    """
    ```

