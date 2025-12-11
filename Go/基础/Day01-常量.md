# 常量

## 语法

```go
const 常量名 [类型] = 常量值
```

```go
const PI float32 = 3.1415926
```

-   赋值给常量的值, 需要是常量表达式

    -   可以是len(), cap(), unsafe.Sizeof()函数计算表达式的值
    -   常量表达式中，函数必须是内置函数，否则编译不过

    ```go
    const intSize = unsafe.Sizeof(12)
    ```

-   常量中的数据类型只可以是布尔型、数字型（整数型、浮点型和复数）和字符串型

-   不能是指针类型(引用类型)

-   类型可以被省略

    ```cpp
    const PI = 3.1415926
    ```

-   批量声明

    ```go
    const (
    	PI = 3.14
        E = 2.71
    )
    ```

    如果不声明表达式, 将沿用上一常量表达式(似乎是表达式, 而不是值, 见最后)

    ```go
    const (
        a = 1
        b
        c
        d
    )

    func main() {
        fmt.Println(a)	 // 1
        fmt.Println(b)  // 1
        fmt.Println(c)  // 1
        fmt.Println(d)  // 1
    }
    ```

## 枚举

没有原生的枚举, 用const间接实现(悲)

```cpp
const (
    MALE   = 0
    FEMALE = 1
)
```

## iota

在const出现时将被重置为0

const 中每新增一行常量声明将使 iota 计数一次

### 计数器机制

iota 可以被用作枚举值

```go
const (
    MALE   = iota
    FEMALE = iota
)

func main() {
    fmt.Println(MALE)   // 0
    fmt.Println(FEMALE) // 1
}
```

const每多一行增加一个值, 和是否调用iota无关

```cpp
const (
    RED    = iota
    ORANGE = 999
    YELLOW = 998
    GREEN  = 997
    BLUE   = 996
    PURPLE = iota
)

func main() {
    fmt.Println(RED)    // 0
    fmt.Println(ORANGE) // 999
    fmt.Println(YELLOW) // 998
    fmt.Println(GREEN)  // 997
    fmt.Println(BLUE)   // 996
    fmt.Println(PURPLE) // 5
}
```

与变量个数无关, 跟行有关

```cpp
const (
    RED, ORANGE = iota, 999
    YELLOW      = 998
    GREEN       = 997
    BLUE        = 996
    PURPLE      = iota
)

func main() {
    fmt.Println(RED)    // 0
    fmt.Println(ORANGE) // 999
    fmt.Println(YELLOW) // 998
    fmt.Println(GREEN)  // 997
    fmt.Println(BLUE)   // 996
    fmt.Println(PURPLE) // 4
}
```

空白行不会增加iota的值

```go
const (
    RED, ORANGE = iota, 999
 	// 空行不影响
    YELLOW = 998
    GREEN  = 997
    BLUE   = 996
    PURPLE = iota
)

func main() {
    fmt.Println(RED)    // 0
    fmt.Println(ORANGE) // 999
    fmt.Println(YELLOW) // 998
    fmt.Println(GREEN)  // 997
    fmt.Println(BLUE)   // 996
    fmt.Println(PURPLE) // 4
}
```

多个语句写在同一行不会影响iota的值

```go
const (
    RED, ORANGE = iota, 999;YELLOW = 998; GREEN  = 997
    BLUE   = 996
    PURPLE = iota
)

func main() {
    fmt.Println(RED)    // 0
    fmt.Println(ORANGE) // 999
    fmt.Println(YELLOW) // 998
    fmt.Println(GREEN)  // 997
    fmt.Println(BLUE)   // 996
    fmt.Println(PURPLE) // 4
}
```

考虑到go编译器实质上会将`;`补上, iota的实质估计是在察觉到`;`后计数

### 自动填充

```go
const (
    RED = iota
    ORANGE
    YELLOW
    GREEN
    BLUE
    PURPLE
)

func main() {
    fmt.Println(RED)    // 0
    fmt.Println(ORANGE) // 1
    fmt.Println(YELLOW) // 2
    fmt.Println(GREEN)  // 3
    fmt.Println(BLUE)   // 4
    fmt.Println(PURPLE) // 5
}
```

```go
const (
    RED    = -1
    ORANGE = iota
    YELLOW // 自动填充数值
    GREEN
    BLUE
    PURPLE
)

func main() {
    fmt.Println(RED)    // -1
    fmt.Println(ORANGE) // 1
    fmt.Println(YELLOW) // 2
    fmt.Println(GREEN)  // 3
    fmt.Println(BLUE)   // 4
    fmt.Println(PURPLE) // 5
}
```

奇妙

```go
const (
    i = 1 << iota
    j = 3 << iota
    k
    l
)

func main() {
    fmt.Println("i=", i) // 1
    fmt.Println("j=", j) // 110
    fmt.Println("k=", k) // 1100
    fmt.Println("l=", l) // 11000
}
```

k和l都沿用了`3<<iota`, 而`iota`是计数器不断累加

## 数组变量

```go
var nums []int = []int{1, 2, 3}
fmt.Println(nums)
```

## 其他

也可以有这些变量:

函数:

```GO
var function func(x int) bool = func(x int) bool {
    fmt.Println(x)
    return true
}
fmt.Println(function) // 0x559640
fmt.Println(function(1))
```

结构体:

```go
var student struct {
    name string
}
```

