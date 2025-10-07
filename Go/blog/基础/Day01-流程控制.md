# 流程控制

## 顺序

略

## 分支

没有`?:`运算符, 悲

### if

去掉了冗余的`()`括号

语法

```go
if bool表达式{
    // bool表达式为true时, 执行该逻辑
}
```

花括号是必须的, 没有说明"如果只有一句逻辑就可以不加花括号", 没有!

### if-else

```go
if bool表达式 {
    // bool表达式 为 true 时, 执行该逻辑
} else {
   // bool表达式 为 false 时, 执行该逻辑
}
```

### if嵌套

```go
num := 12
if num > 0 {
    fmt.Printf("%d是一个正数\n", num)
} else if num < 0 {
    fmt.Printf("%d是一个负数\n", num)
} else {
    fmt.Printf("%d是一个0\n", num)
}
```

###switch-case

```go
switch [表达式X] {
    case 表达式1:
        ...
    case 表达式2:
        ...
    case 更多的case:
    default:
        ...
}
```

-   表达式X及其值可以是任何类型
-   表达式1,2,3 的返回值需要是同一类型
-   没有break, 也是执行完一个代码块就结束了的
-   可以用逗号表达式来表示逻辑或
-   表达式X可以被省略, 而表达式1,2,3是一个个bool表达式, 此时bool表达式为true者, 进入语句块

```go
num := 1
value := num % 2
switch num {
case 0, 1, 2:
    fmt.Println("0,1,2")
case value:
    fmt.Println("value")
case value * 2:
    fmt.Println("value*2")
case value + 1:
    fmt.Println("value+1")
case value - 1:
    fmt.Println("value-1")
default:
    fmt.Println("default")
}
```

支持字符串:

```go
num := "a"
value := num
num += "2"
switch num {
case value:
    fmt.Println("value")
case value + "0":
    fmt.Println("value+0")
case value + "1":
    fmt.Println("value+1")
case value + "2":
    fmt.Println("value+2")
default:
    fmt.Println("default")
}
```

也可以用来判断类型

```go
var x interface{}

switch i := x.(type) {
case nil:
    fmt.Printf(" x 的类型 :%T", i)
case int:
    fmt.Printf("x 是 int 型")
case float64:
    fmt.Printf("x 是 float64 型")
case func(int) float64:
    fmt.Printf("x 是 func(int) 型")
case bool, string:
    fmt.Printf("x 是 bool 或 string 型")
default:
    fmt.Printf("未知类型")
}
```

### fallthrough

会强制执行**fallthrough**后面的case语句而忽略对`case`的判断

```cpp
x := 1
switch {
case x == 0:
    fmt.Println("0")
    fallthrough
case x == 1:
    fmt.Println("1")
    fallthrough
case x == 2:
    fmt.Println("2")
    fallthrough
case x == 3:
    fmt.Println("3")
case x == 4:
    fmt.Println("4")
    fallthrough
default:
    fmt.Println("default")
    //fallthrough 不能再加了
}
// 输出:
// 1
// 2
// 3
```

```go
x := 1
switch x {
case 0:
    fmt.Println("0")
    fallthrough
case 1:
    fmt.Println("1")
    fallthrough
case 2:
    fmt.Println("2")
    fallthrough
case 3:
    fmt.Println("3")
case 4:
    fmt.Println("4")
    fallthrough
default:
    fmt.Println("default")
    //fallthrough 不能再加了
}
// 输出:
// 1
// 2
// 3
```

合理, 毕竟跳出的情况常见, 继续往下执行的情况少见嘛



## 循环

没有do-while; while, go认为一切循环都能由for转成

### while

```go
for condition { }
```

### fori

```go
for i := 0; i < len(src); i++ {

}
```

### src.for

```go
for i := range src {
    fmt.Println(i, src[i])
}
```

### src.forr

```go
for i, e := range src {
    fmt.Println(i, e)
}
```

### break-continue

略

### goto

```go
package main

import "fmt"

func main() {
   /* 定义局部变量 */
   var a int = 10

   /* 循环 */
   LOOP: for a < 20 {
      if a == 15 {
         /* 跳过迭代 */
         a = a + 1
         goto LOOP
      }
      fmt.Printf("a的值为 : %d\n", a)
      a++    
   }  
}
```

