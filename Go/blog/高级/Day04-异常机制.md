# 异常机制

##抛出异常

以返回值的形式返回给调用者处理

```go
func add(nums ...int) (sum int, err error) {
    if len(nums) == 0 {
       err = errors.New("没有参数是想要怎样啦")
       return
    }
    for _, num := range nums {
       sum += num
    }
    // 一定要加return qwq
    return
}
```

##defer-recover-panic