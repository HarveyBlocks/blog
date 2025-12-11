# 包和模块

## 包

### 访问控制

对于类型/接口/方法/函数/字段

-   首字母大写, 对其他 package 可见
-   首字母小写, 对其他 package 不可见

### 编译运行包

同一个包下的文件之间, 可以互相访问

由于不会被import进来, 也不知道类/接口/函数/变量都在哪个文件里

就会出现编译main函数时找不到对象

```shell
go run main.go
# command-line-arguments
.\main.go:4:2: undefined: Bye

```

编译多个文件

```go
go run main.go calc.go
```

编译运行包下所有文件

```shell
go run .
```

## Go Modules

包管理工具

### mod命令

-   初始化一个mod

    ```shell
    go mod init main
    ```

    当前目录下创建了一个`go.mod`文件

    ```
    module Hello

    go 1.22

    ```

    这个文件记录当前模块的模块名以及所有依赖包的版本。

### 配置镜像

-   七牛云代理

```go
go env -w GO111MODULE=on
go env -w GOPROXY=https://goproxy.cn,direct
```

### 下载依赖

运行之后就会检查没有的包, 然后让你下载

```
main.go:5:2: no required module provides package rsc.io/quote; to add it:
	go get rsc.io/quote

```

然后执行

```shell
go get rsc.io/quote
```

日志: 

```
go: downloading rsc.io/quote v1.5.2
go: downloading rsc.io/sampler v1.3.0
go: downloading golang.org/x/text v0.0.0-20170915032832-14c0d48ead0c
go: added golang.org/x/text v0.0.0-20170915032832-14c0d48ead0c
go: added rsc.io/quote v1.5.2
go: added rsc.io/sampler v1.3.0
```

然后go.mod文件也发生了变化:

```
module Hello

go 1.22

require (
	golang.org/x/text v0.0.0-20170915032832-14c0d48ead0c // indirect
	rsc.io/quote v1.5.2 // indirect
	rsc.io/sampler v1.3.0 // indirect
)

```

还多了`go.sum`文件:

```
golang.org/x/text v0.0.0-20170915032832-14c0d48ead0c h1:qgOY6WgZOaTkIIMiVjBQcw93ERBE4m30iBm00nkL0i8=
golang.org/x/text v0.0.0-20170915032832-14c0d48ead0c/go.mod h1:NqM8EUOU14njkJ3fqMW+pc6Ldnwhi/IjpwHt7yyuwOQ=
rsc.io/quote v1.5.2 h1:w5fcysjrx7yqtD/aO+QwRjYZOKnaM9Uh2b40tElTs3Y=
rsc.io/quote v1.5.2/go.mod h1:LzX7hefJvL54yjefDEDHNONDjII0t9xZLPXsUe+TKr0=
rsc.io/sampler v1.3.0 h1:7uVkIFmeBqHfdjD+gZwtXXI+RODJ2Wc4O7MPEh/QiW4=
rsc.io/sampler v1.3.0/go.mod h1:T1hPZKmBbMNahiBKFy5HrXp6adAjACjK9JXDnKaTXpA=

```

