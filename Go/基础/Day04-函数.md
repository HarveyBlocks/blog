# 函数

支持多返回值

## 语法

```go
func funcName(param1 Type1, param2 Type2, ...) (return1 Type3, ...) {
    // body
}
```

## 使用

```go
func add(a int, b int) (int) {
    res := a + b
    return res
}
```

给返回值命名

```go
func add(a int, b int) (res int) {
    res = a + b
    return
}
```

### 不定参数

```go
func add(nums ...int) (sum int) {
    sum = 0 // 其实不需要, 但为了可读性吧?
    for _, num := range nums {
       sum += num
    }
    return
}
```

不支持默认参数值

### 值传递

Go语言采用值传递, 指针做参数就传递指针值

## 匿名函数(闭包)

### 语法

```go
func 外层函数() {
    // 外层函数逻辑
    结果 := func(形式参数 参数类型, ...) {
       // 内层函数
    }(实际参数,...)
    // 外层函数逻辑
    return
}
```

### 使用

```go
func A() int {
    x := 5
    result := func(a int) int {
       x++
       fmt.Println(x) // 6
       return a
    }(12)
    fmt.Println(result) // 12
    fmt.Println(x)      // 6
    return x
}
```

返回6

## defer

>   推迟

`defer`的内容在函数运行结束之后再运行

```go
func A() {
    defer fmt.Println("B")
    fmt.Println("A")
}
```

```
A
B
```

多个`defer`按照声明顺序的倒序被回调

```go
func A() {
    defer fmt.Println("1")
    defer fmt.Println("2")
    defer fmt.Println("3")
    defer fmt.Println("4")
    defer fmt.Println("5")
}
```

```
5
4
3
2
1
```

`defer`之后可以跟一个语句, 多个语句后面需要使用闭包

 ```go
func A() {
    defer func() {
       // 回调内容
    }()
}
 ```

### defer的实际参数

实际参数的值再defer声明的时候确定(值, 而不是引用)

```go
i := 5
defer func(i int) {
    fmt.Println(i) // 5
}(i)
i++
```

### defer调用的时机

1.  将返回变量值赋值给返回值(声明在函数签名上的, 如果没声明就是一个编译器创建的暂时变量)
2.  调用defer函数
3.  执行返回RET命令

情况一

```go
func A() int {
    x := 5
    defer func(a int) {
       x++
       fmt.Println(x) // 7
    }(x)
    x++
    fmt.Println(x) // 6
    return x
}
func main() {
    fmt.Println(A()) // 6
}
```

情况2

```go
func A() (x int) {
    x = 5
    defer func(a int) {
       x++
       fmt.Println(x) // 7
    }(x)
    x++
    fmt.Println(x) // 6
    return x
}
func main() {
    fmt.Println(A()) // 7
}
```

情况3

```go
func A() (y int) {
    x := 5
    defer func(a int) {
       x++
       fmt.Println(x) // 7
    }(x)
    x++
    fmt.Println(x) // 6
    return x
}
func main() {
    fmt.Println(A()) // 6
}
```

### defer函数调用参数

`defer`+单个函数, 函数的参数会在一开始就以被声明的顺序被解析/运算,

`defer`+闭包, 闭包中再调用函数, 闭包内函数的参数会在外部函数执行完之后以被声明的倒序进行回调

例子如下: 

```go
func X(name string, value int) int {
	fmt.Println(name, value)
	return value
}
func A() {
	defer X("A", X("B", 1))
	defer X("A", X("B", 2))
	defer func() {
		X("C", X("D", 1))
	}()
	defer func() {
		X("C", X("D", 2))
	}()
    return
}
```

```
B 1
B 2
D 2
C 2
D 1
C 1
A 2
A 1
```

