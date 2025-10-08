# 运算符

## 计算'表达式'

>   将表达式作为参数, 传入`expr`命令, 让expr计算

```shell
#!/usr/bin/env sh
expr  1 + 2
# 返回3
```

`*`在shell中拥有许多意义, 例如通配符, 所以`*`要表示乘法运算需要做转义

```shell
#!/usr/bin/env sh
expr  2 \* 3 
# 返回6
```

括弧还不能做参数?!

```shell
#!/usr/bin/env sh
a=$(expr 1 + 2)
echo $a
# 3
a=`expr 3 + 2`
echo $a
# 5
```

## 运算符

`$[运算表达式]`和`$((运算表达式))`

算数运算

```shell
#!/usr/bin/env sh
echo $[1 + 2*3]
# 7
echo $[(1 + 2)*3]
# 9
echo $(((1 + 2)*3))
# 9

a=2
b=3
echo $((a+b))
# 5
c=$((a/b))
echo $c
# 0
c=$((b/a))
echo $c
# 1
```

比较运算

```shell
#!/usr/bin/env sh
echo $[(1 + 2)==3]
# 1
echo $(((1 + 2)!=3))
# 0
echo $(((1 + 5)>=3))
# 1
```

逻辑运算

```shell
#!/usr/bin/env sh
echo $((1 + 2!=3||3==4-1))
# 1
```

令人疑惑的三元运算符

```shell
yes="true"
no="false"
echo $((1 + 2!=3?yes:no))
# 0
# ???????????????????????????
echo $((1 + 2==3?yes:no))
# 0
# ???????????????????????????
echo $((1 + 2!=3?"true":"false"))
# 0
# ???????????????????????????
echo $((1 + 2!=3?100:200))
# 200
echo $((1 + 2==3?200:300))
# 200
echo $((1 + 2!=3?"你好":"你不好"))
# 报错 ??????????????????????????????
```

位移运算符

```shell
#!/usr/bin/env sh
a=0
# 1
a=$(((a<<1)+1))
echo $a
# 2
a=$(((a<<1)+1))
echo $a
# 3
a=$(((a<<1)+1))
echo $a
# ...
# 63
a=$(((a<<1)+1))
echo $a
# 64
a=$(((a<<1)+1))
echo $a
```

存储`long long signned`

```shell
#!/usr/bin/env sh
n=$#
result=0
for (( i = 0; i < n; i++ )); do
    result=$((result+$i))
done
echo $result
```

```shell
./sum.sh 1 2 5 2 3 4
15

```

### 变量表达式

`a+=1`

↓

`a=$((a+1))`

????

```shell
27970@Harvey-PC MINGW64 /d
$ let a+=1

27970@Harvey-PC MINGW64 /d
$ echo $a
1

27970@Harvey-PC MINGW64 /d
$ let a++

27970@Harvey-PC MINGW64 /d
$ echo $a
2

```

