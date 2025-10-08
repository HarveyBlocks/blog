# 类型别名与定义

>   TypeAlias

## 类型定义

### 语法

```go
type 新类型 原类型
```

## 类型别名

类型别名就真的是类型的别名, 而没有任何从名字角度的不同

### 语法

```go
type 别名=原类型
```

## 区别

### %T

使用%T格式化参数，显示a变量本身的类型

```go
type IntegerType int
type IntegerAlias = int

func main() {
    fmt.Printf("%T\n", IntegerType(1)) // main.IntegerType
    fmt.Printf("%T\n", IntegerAlias(1)) // int
}
```

### 定义方法

只能在定义类型的包内定义这个类型的方法

而类型别名并不是定义了一个类型, 故没办法写这个类型的方法

```go
type IntegerType int
type IntegerAlias = int

func (c IntegerType) String() string {
    return "UNKNOWN"
}

/*
编译异常

    func (c IntegerAlias) String() string {
       return "UNKNOWN"
    }
*/
```
