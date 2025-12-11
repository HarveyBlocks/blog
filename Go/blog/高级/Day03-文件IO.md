# 文件IO

简单读取文件到内存

```go
filename := ""
// 打开文件
file, err := os.Open(filename)

// 文件找不到，返回空
if err != nil {
    return
}

// 使用读取器读取文件
reader := bufio.NewReader(file)

for {

    // 读取文件的一行
    line, err := reader.ReadString('\n')
    if err != nil {
       break
    }

    // 切掉行左右两边的空白字符
    line = strings.TrimSpace(line)

    // 忽略空行
    if line == "" {
       continue
    }

    // ...
    fmt.Println(line)
}

// 在函数结束时，关闭文件
defer file.Close()
```

