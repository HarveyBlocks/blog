# 类型指针

-   类型指针，允许对这个指针类型的数据进行修改
-   传递数据使用指针，而无须拷贝数据
-   类型指针不能进行偏移和运算

切片也是指针

##使用

### 取地址

```go
var 指针名 *指向类型 =  &指向变量
```

### 取值

```go
*指针名
```



```go
var num int16 = 0x7f77
var pNum *int16 = &num // 取地址
fmt.Println(*pNum)     // 取值
```

```go
var num []int16 = []int16{1, 2, 3}
var pNum *int16 = num // 不成
```
```go
var num []int16 = []int16{1, 2, 3}
var pNum *[]int16 = &num // 取地址
fmt.Println(*pNum)       // [1 2 3]
```

###更改指向值

```go
var num int16 = 0x7763
var pNum *int16 = &num // 取地址
fmt.Println(*pNum)     // 30563
fmt.Println(num)       // 30563
*pNum = num + 1
fmt.Println(*pNum) // 30564
fmt.Println(num)   // 30564
```

##获取命令行参数

```go
package main

import (
    "flag"
    "fmt"
)

var mode *string = flag.String(
    "name", "default_name", "the description of arg 'name'", // description将出现在help中
)

func main() {
    fmt.Println(*mode) // NAME_IF_NULL
}
```

```shell
go run hello.go --name=NAME_IF_NULL
```

