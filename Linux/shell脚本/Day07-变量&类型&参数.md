# 变量

1.  作用域分
    -   全局的环境变量
    -   局部的环境变量
2.  创建角色分
    -   系统预定义变量
    -   用户自定义变量



## 系统预定义变量

### 常用系统变量

-   `$HOME`
-   `$PWD`
-   `$SHELL`
-   `$USER`

### 查看系统变量值

```shell
echo $HOME
echo $PWD
echo $SHELL
echo $USER
```

查看全部的系统变量

```bash
env
```

```shell
env | less
```

或

```shell
printenv # 查看全部
printenv USER # 查看一个, 不用$
```

### 系统的局部变量

`set`查出当前环境所有的变量(系统预定义+用户自定义)

```shell
set
```

```shell
set | less
```



## 自定义变量

😓别用`var`做变量名, 人家有系统带默认值

### 定义语法

-   定义变量

    ```shell
    变量名=变量值
    ```

    **等号前后不能有空格**

    分开写默认是命令和参数

-   撤销变量

    ```shell
    unset 变量
    ```

-   声明静态变量/只读变量

    ```shell
    readonly 变量
    ```

    **静态变量不能unset**

```shell
#!/usr/bin/env bash
var=hello
echo $var
var="Hello World"
echo "$var" # 这里也可以加引号?!
var='hello world'
echo $var
Var='Goodbye'
echo $var
echo $Var # 大小写敏感
```

```shell
readonly my_var=Aa
unset my_var
# D:/IT_study/source/JDK/dubbo/api/src/main/resources/hello.sh: line 2: unset: my_var: cannot unset: readonly variable
```

## 变量的作用域

子Bash是可以获取到父Bash的变量的, 但其更改只在子Bash中生效

父Bash不可以获取子Bash的变量



### 局部变量的声明

```shell
var="value"
```



### 全局变量的声明

`export` 导出

把当前的变量导出到全局

```shell
var=1
export var
```

```shell
#!/usr/bin/env bash
var1=hello
echo $var1 # hello
export var1
bash
  echo $var1  # hello
  var1=hi
  var2=world
  echo $var1 # hi
  echo $var2 # world
  export var2
    bash
      echo $var1 # hi
      echo $var2 # world
    exit
exit
echo $var1 # hello
echo $var2 # 空
```

`export`不能使变量在父bash生效

```shell
#!/usr/bin/env bash
var1=hello
export var1
bash
  var1=hi
  export var1
exit
echo $var1 # hello
```

### `source`和`bash`

命令行中: 

```shell
var=1
```

文件`hello.sh`中

```shell
export $var
```

命令行中:

```shell
bash hello.sh # var有值
source hello.sh # hello.sh有值
```

## 类型

### 字符串

```shell
num=2+3
echo $num # 2+3
```

所有的变量类型默认都是字符串

如果真的要计算结果:

```shell
num=$[1+2]
echo $num # 3
num=$(($num+2)) # 这里可以写 num=$((num+2)) 一样
echo $num # 5
```

待到运算符处再议

#### 单引号与双引号

```shell
num=3
echo "$num" # 3
echo '$num' # $num
```

单引号引起来的全部认定为字符串

双引号引起来的, 若包含引用, 就执行引用

#### 字符串拼接

-   防止传入参数为null

    ```shell
    #!/usr/bin/env sh
    # 求1+2+....+s1
    word=$1
    if [ "$word""plus" = "Hello"plus ]; then
        echo "$word"plus # Helloplus
    else
      echo "$word"plus # 如果为null, 也有plus保底
    fi
    ```

## 参数与引用

### 参数

用`$1`到`$9`表示第几个参数, `${10}`等表示后面的参数

```shell
echo "hello $1, $2"
```



```shell
27970@Harvey-PC MINGW64 /d/IT_study/source/JDK/dubbo/api/src/main/resources
$ ./hello.sh A B
hello A, B

27970@Harvey-PC MINGW64 /d/IT_study/source/JDK/dubbo/api/src/main/resources
$ ./hello.sh A
hello A, 

27970@Harvey-PC MINGW64 /d/IT_study/source/JDK/dubbo/api/src/main/resources
$ ./hello.sh 
hello , 

```

### 特殊参数

#### `$0`

>   当前命令文件名 , 或者说, 调用当前脚本文件所使用的方式, 或者就是字面意义上的第0个参数

```shell
#!/usr/bin/env sh
echo $0
```

```shell
27970@Harvey-PC MINGW64 /d/IT_study/source/JDK/dubbo/api/src/main/resources
$ /usr/bin/env sh D:/IT_study/source/JDK/dubbo/api/src/main/resources/hello.sh
D:/IT_study/source/JDK/dubbo/api/src/main/resources/hello.sh

27970@Harvey-PC MINGW64 /d/IT_study/source/JDK/dubbo/api/src/main/resources
$ ./hello.sh
./hello.sh

```



`hello2.sh`↓

```
./hello.sh
```

```shell
27970@Harvey-PC MINGW64 /d/IT_study/source/JDK/dubbo/api/src/main/resources
$ /bin/sh D:/IT_study/source/JDK/dubbo/api/src/main/resources/hello2.sh
./hello.sh


```

#### `$#`

>   获取所有输入参数的个数

```shell
#!/usr/bin/env sh
num=$#
echo ${num}
```



```shell
27970@Harvey-PC MINGW64 /d/IT_study/source/JDK/dubbo/api/src/main/resources
$ /usr/bin/env sh D:/IT_study/source/JDK/dubbo/api/src/main/resources/hello.sh
0

27970@Harvey-PC MINGW64 /d/IT_study/source/JDK/dubbo/api/src/main/resources
$ ./hello.sh 1 2 3 4 5
5


```



#### `$*`与`$@`

>   `$*`表示把所有参数看作整体
>
>   `$@`表示把所有参数放在一个集合

```shell
#!/usr/bin/env sh

echo "$*"
echo "$@"
```



```shell
27970@Harvey-PC MINGW64 /d/IT_study/source/JDK/dubbo/api/src/main/resources
$ ./hello.sh 1 2       3
1 2 3
1 2 3

```

#### `$?`

>   返回上一条命令的执行结果, 成功为0, 错误为其他

```shell
#!/usr/bin/env sh

balabala
echo $?
echo 'balabala'
echo $?
```

```shell
27970@Harvey-PC MINGW64 /d/IT_study/source/JDK/dubbo/api/src/main/resources
$ ./hello.sh
./hello.sh: line 3: balabala: command not found
127
balabala
0

```

