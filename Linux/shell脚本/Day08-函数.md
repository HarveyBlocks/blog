# 函数

## 系统函数

### 调用系统函数-命令替换

```shell
$(命令 [参数])
```

例如, 日志文件名, 参数+'log'+时间戳

已知时间戳函数是`data %s`

```shell
#!/usr/bin/env sh
filename="$1"_log_$(date +%s)
echo "$filename"
```

### basename

从路径获取文件名, 不管文件存在与否, 只是简单对字符串的处理

```shell
basename [string/pathname] [suffix]
```

强最后一个`/`之前的全删掉

suffix后缀, 指定了就去掉字符串的相同的后缀

### dirname

截取最后一个`/`之前的字符. , 不管文件存在与否, 只是简单对字符串的处理

获取文件的全路径

```shell
#!/usr/bin/env sh
cache_path=$PWD
cmd=$0;
cd $(dirname $cmd)
now_path=$PWD
all_path="$now_path"/"$(basename $cmd)"
cd $cache_path # 这一步其实不需要, 因为移动了, 但不会影响上层的控制台
echo $all_path
```

使用source, $0将为source命令所在路径

## 自定义函数

```shell
[function] funname[()]
{
	命令;
	[return int;] # 只能返回整数(0~255), 表示函数执行成功与否, 省略默认返回最后一句的执行成功与否
}
```

参数的获取不用形参名, 而是使用`$1`等来使用, `$#`,`$*`,`$@`等值都会变成函数参数的信息, **$0依旧是脚本调用命令**

```shell
#!/usr/bin/env sh
function my_fun() {
    echo "$#" "$@" "$*"
    return 1
}
my_fun a
echo $? # 1
```

不好的返回结果方式(因为函数是高度封装的)

```shell
#!/usr/bin/env sh
x
function my_fun() {
    x="你好"
}
echo $x
```

**↓失败**

```shell
#!/usr/bin/env sh

function my_fun() {
    x="你好"
    echo $x
}

my_fun > a
echo $a
```

正确的做法: 

```shell
#!/usr/bin/env sh

function my_fun() {
    x="你好, "$1
    echo $x
}

a=$(my_fun "世界")
echo $a
```

## 实践-归档

```shell
#!/usr/bin/env sh
# 归档(archive), 文档备份
if [ $# -lt 1 ]; then
    # 没有参数
    echo "缺少参数: 目录"
    exit
fi

for dir_name in $@ ; do
    if [ -d $dir_name ]; then
      tar -czvf ./archive_"$(date +%s)".tar.gz $dir_name
    else :
      echo "文件: \`""$dir_name"\`不存在
    fi
done
```

