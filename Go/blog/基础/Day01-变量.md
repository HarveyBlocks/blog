# 变量

## 变量名

标识符的一种

## 声明

声明一个变量

```go
var 变量名 变量类型
```

声明多个变量

```go
var 变量名1, 变量名2 变量类型
```

```go
var num int
fmt.Println(num) // 默认是0
var flag bool
fmt.Println(flag) // 默认是false
var msg string
fmt.Println(msg)             // 默认是空字符串
fmt.Println(nil)             // 而不是nil(<nil>)
```

or

```go
var ( // 一般用来声明全局变量
    num1 int8
    num2 int16
    num3 int32
)
fmt.Println(num1) // 0
fmt.Println(num2) // 0
fmt.Println(num3) // 0
```

## 初始化

```go
var num int = 21_4361_2124_1212_3212 // 初始化
fmt.Println(num)
```

### 自动判定类型

依据初始化的值, Go能自己判断变量的类型

```go
var num = 21_4361_2124_1212_3212 // 初始化, 尽管是长整形, 也不用加L啦啦啦啦
fmt.Println(num)
```

但似乎没有自动缩放类型大小的功能(悲)

```java
var num = 21                    // 初始化
fmt.Println(unsafe.Sizeof(num)) // 8
```

### `:=`

使用`:=`声明变量

```go
num := 114514_1919_810_2233     // 初始化
fmt.Println(unsafe.Sizeof(num)) // 8
```

只能放在函数里, 而不能是全局变量

### 批量初始化

```go
var num1, num2, num3 = 1, 2, 3

fmt.Println(num1) // 1
fmt.Println(num2) // 2
fmt.Println(num3) // 3
```

```go
var num1, num2, num3 int8
num1, num2, num3 = 1, 2, 3

fmt.Println(num1) // 1
fmt.Println(num2) // 2
fmt.Println(num3) // 3
```

```go
num1, num2, num3 := 1, 2, 3

fmt.Println(num1) // 1
fmt.Println(num2) // 2
fmt.Println(num3) // 3
```

```cpp
var (
    num1 int8 = 2
    num2 int8 = 4
    num3 int32 = 6
)
fmt.Println(num1) // 2
fmt.Println(num2) // 4
fmt.Println(num3) // 6
```

## 赋值

### 多变量赋值

```go
var num1, num2, num3 int8
num1, num2, num3 = 1, 2, 3
fmt.Println(num1) // 1
fmt.Println(num2) // 2
fmt.Println(num3) // 3
```

类似于python

玩出花来: 

```go
var num1, num2, num3 int8
num1, num2, num3 = 1, 2, 3
num1, num2, num3 = num3, num1, num2
fmt.Println(num1) // 3
fmt.Println(num2) // 1
fmt.Println(num3) // 2
```

## 值类型和引用类型

### 值类型

int、float、bool 和 string 这些基本类型都属于值类型

使用这些类型的变量直接指向内存中的值

赋值操作就是对值进行了拷贝

### 引用类型

通过取地址运算符`&`, 来取得变量的地址

然后将地址赋值给变量, 变量的值就会是一个地址, 这个地址指向变量

```go
var a int8 = 12
var p *int8 = &a
fmt.Println(p)  // 0xc000116068
fmt.Println(*p) // 12
```

## 声明与使用

局部变量声明之后一定要被使用, 而全局变量是可以被声明而不被使用的

## 空白标识符`_`

```go
var num1, num2, num3 int8
num1, num2, num3 = 1, 2, 3
num1, _, num3 = num3, num1, num2
fmt.Println(num1) // 3
fmt.Println(num2) // 2
fmt.Println(num3) // 2
```

## 生命周期和内存结构

### 栈和堆

老生常谈此处略

### 变量逃逸

>   Escape Analysis

Go的编译器会自行判断, 变量是应该存在于堆中, 还是应该存在于栈中

译器分析代码的特征和代码生命期，决定应该如何堆还 是栈进行内存分配

```go
func foo(num int) *int {
    return &num // 取函数局部变量c的地址并返回。Go语言允许
    // 此时num从栈转向堆
}
func main() {
    var num int = 32
    var pNum *int = &num
    fmt.Println(pNum)  // 0xc00000a0d8
    fmt.Println(*pNum) // 32
    pNum = foo(num)
    fmt.Println(pNum)  // 0xc00000a110
    fmt.Println(*pNum) // 32
}
```
