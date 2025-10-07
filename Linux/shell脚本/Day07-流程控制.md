# 流程控制



## 分支结构

### 断言?

```shell
#!/usr/bin/env sh
a=2
test $a = 1
echo $?
# 1, 失败
test $a = 2
echo $?
# 0, 成功
# > 和 < 不行
# > 是输出重定向, <是输入重定向
```

但是一直`test`太烦

```shell
[ 比较表达式 ]
# [space比较表达式space]
echo $? 
# 成功 or not
# [  ] 返回1, 失败
```

**表达式前后一定要加空格!!!!!!!**

里头不能加括号

### 比较

#### 字符串比较

`=` 

```shell
word="hello"
[ word = hello ]
```

等号两边有空格

####大小比较

`-eq` equal

`-ne` not equal

`-lt` less than

`-gt` grater than

`-le` less equal

`-ge` grater equal

```shell
[ 2 -eq 1 ]
```



#### 文件权限

`-r` 可读 read

`-w` 可写 write

`-x `可执行 exec

```shell
[ -r hello.sh ]
```



#### 文件性质

`-e` 文件存在 exist

`-f` 是文件 file

`-d` 是文件夹 directory



#### 三元运算符?

```shell
[ 比较表达式 ] && 成功执行该语句 || 失败执行该语句
```

```shell
[ 1 -eq 2 ] && echo '对' || echo '否'
```



###if-elif-else

```shell
if ((1!=2)); then
    echo "对"
fi
```

```shell
#!/usr/bin/env sh

word=nihao
if [ $word = "nihao" ]; then # ;表示一行多个命令
  echo "你好"
elif [ $word = nihao ]; then
    echo "你好呀"
elif [ $word = Nihao ] || [ $word = nihao ]; then
	echo "你好吗"
elif [ $word = Nihao -a $word = nihao ]; then # -a -> and ; -o -> or ;
	echo "你好吗"
else
  echo "失败啦"
fi
# 你好
# 你好
```

### switch-case

```shell
#!/usr/bin/env sh
case $1 in
1)
  echo "一"
  ;;
2)
  echo "二"
  ;;
3)
  echo "三"
  ;;
*) 				# Default
  echo "*"
  ;;
esac
```

```shell
#!/usr/bin/env sh
case $1 in
na*)
  echo "na开头的"
  ;;
*me)
  echo "me结尾的"
  ;;
*name*)
  echo "包含name的"
  ;;
*)
  echo "我不造啊"
  ;;
esac
```

## 循环

### while循环

```shell
#!/usr/bin/env sh
num=$1
sum=0
if [ "$num"x = x ]; then
  echo "cant be null"
  return
fi
while [ $num -gt 0 ]; do
  sum=$((sum+$num))
  num=$((num-1))
done
echo $sum
```

### for循环

倒计时器

```shell
#!/usr/bin/env sh
n=$1
for (( i = 0; i < n; i++ )); do
  echo $((n-i))
done
```

### fori

for i in range

```shell
#!/usr/bin/env sh
for i in {1..5} ; do
    echo $i
done
```

输出每个参数: 

```shell
#!/usr/bin/env sh
num=$@
for i in $num ; do
    echo $i
done
```

#### 打印所有参数

```shell
#!/usr/bin/env sh
# 把每一个参数打印
for i in $@; do
    echo $i
done
for i in $*; do
    echo $i
done
for i in "$@"; do
    echo $i
done
# 打印一行
for i in "$*"; do
    echo $i
done
```

