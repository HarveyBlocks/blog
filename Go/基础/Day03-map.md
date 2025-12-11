# 映射

>   map

底层用散列表实现

## 声明

```go
映射名称 := make(map[键类型]值类型, size)
```

```go
numberMap := make(map[int]string)
fmt.Println(numberMap)
```

## 初始化

```go
numberMap := map[int]string{
    1: "0001",
    2: "0010",
    3: "0011",
    4: "0100",
}

fmt.Println(numberMap) // map[1:0001 2:0010 3:0011 4:0100]
```

## 赋值

```go
映射名称[键] = 值
```

```go
numberMap := make(map[int]string, 12)
fmt.Println(numberMap) // map[]
numberMap[0] = "0"
numberMap[1] = "1"
numberMap[2] = "2"
numberMap[3] = "3"
numberMap[4] = "4"
fmt.Println(numberMap) // map[0:0 1:1 2:2 3:3 4:4]
numberMap[0] = "0000"
fmt.Println(numberMap) // map[0:0000 1:1 2:2 3:3 4:4]
```

## 使用

### 遍历

```go
for key, value := range numberMap {
    fmt.Printf("[%d]=%s\n", key, value)
}
```

### delete键值对

```go
delete(映射变量, 键)
```

```go
delete(numberMap, 1)
```

### 清空

Go语言中并没有为map提供任何清空所有元素的函数、方法

唯有重新make一个新的map

Go语言中的并行垃圾回收效率比写一个清空函数高效多了(逆天)

## 线程安全的映射

>   sync.Map

原生Map采用fail-fast机制, 速速抛出异常

