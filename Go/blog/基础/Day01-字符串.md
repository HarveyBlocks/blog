# 字符串

## 连接

Go语言支持字符串用`+`连接

```go
// aaabbbb
fmt.Println("aaa" + "bbbb") 
```

## 格式化

使用`fmt.Sprint()`对字符串进行格式化



| 格式 | 说明                                     |
| :--- | :--------------------------------------- |
| %v   | 按值的本来值输出                         |
| %+v  | 在 %v 基础上，对结构体字段名和值进行展开 |
| %#v  | 输出 Go 语言语法格式的值                 |
| %T   | 输出 Go 语言语法格式的类型和值           |
| %b   | 整型以二进制方式显示                     |
| %o   | 整型以八进制方式显示                     |
| %d   | 整型以十进制方式显示                     |
| %x   | 整型以十六进制方式显示                   |
| %X   | 整型以十六进制、字母大写方式显示         |
| %f   | 浮点数                                   |
| %U   | Unicode 字符                             |
| %p   | 指针，十六进制方式显示                   |
| %%   | 输出 % 本体                              |

## 使用

### 取长度

```go
fmt.Println(len("Hello World"))             // 11 ASCII码字符/字节数
fmt.Println(len("你好世界"))                    // 12 字节数
fmt.Println(utf8.RuneCountInString("你好世界")) // 4 字符数
```

### 遍历字符

遍历字节略

```go
str := "你好世界"
for index, element := range str {
    fmt.Printf("[%d]%c\n", index, element)
}
/*
    [0]你
    [3]好
    [6]世
    [9]界
*/
```

### 切片/字串

同[切片](Day02-数组与切片.md)

### splite

```go
str := "1,2,3,4,5,6"
fmt.Println(strings.Split(str, ",")) // 分割成字符串数组
```

### indexOf

```go
str := "1,2,3,4,5,6"
fmt.Println(strings.Index(str, ",")) // 第一个","所在索引
fmt.Println(strings.LastIndex(str, ",")) // 最后一个","所在索引
```

-   找不到返回-1

### 更改字节

不能直接在字符串上更改, 需要转成字节切片然后修改字节

```go
str := "1,2,3,4,5,6"
fmt.Println(&str) // 0xc000024070
fmt.Println(str)  // 0xc000024070
data := []byte(str)
for index := 0; index < len(data)/2; index++ {
   data[len(data)-index-1], data[index] = data[index], data[len(data)-index-1]
}
str = string(data)
fmt.Println(&str) // 0xc000024070
fmt.Println(str)  // 6,5,4,3,2,1
```
### trim

```go
str := "\n\t\r1,2,3,4,5,6          \n\n"
fmt.Println(strings.TrimSpace(str))       // 1,2,3,4,5,6
fmt.Println(strings.Trim(str, " \n\t\r")) // 1,2,3,4,5,6
```



## bytes.Buffer

高效的字符串拼接

```go
str1 := "AAA"
str2 := "BBBB"
str3 := "CC"
// 声明字节缓冲
var sb bytes.Buffer
// 把字符串写入缓冲
sb.WriteString(str1)
sb.WriteString(str2)
sb.WriteString(str3)
// 将缓冲以字符串形式输出
fmt.Println(sb.String())
```