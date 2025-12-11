# 并发编程

>   goroutine

## time

睡眠

```go
time.Sleep(2 * time.Second)
```

格式化的当前时间

```go
func formatedNow() string {
    return time.Now().Format("15:04:05.000") // 这些15,14是常量, 是时间单位的编码/代号, 具体啥单位对应啥编号见源码
}
```

## `go`开启协程

>   线程 Thread
>
>   协程 Coroutine

-   线程开辟自己的栈
-   协程从堆里分配一部分空间供自己使用
    -   避免上下文内存切换
    -   减少内存占用

```go
go fmt.Println("执行go") // 执行go 在执行main之后
fmt.Println("执行main")
time.Sleep(5 * time.Second)
```

对于Go来说, 似乎是主线程结束, 其他线程也会一并结束了

```go
func run() {
    time.Sleep(1 * time.Second)
    fmt.Println("执行run")
}
func main() {
    go run() // 异步执行
    fmt.Println("执行main")
    time.Sleep(5 * time.Second)
}
```

```go
func main(){
    go func() {
        time.Sleep(1 * time.Second)
        fmt.Println("执行run")
    }()
    fmt.Println("执行main")
    time.Sleep(5 * time.Second)
}
```

## sync.WaitGroup

-   计数
-   等待

```go
var wg = sync.WaitGroup{}

func main() {
    wg.Add(1) // 增加一个线程(计数器加一)
    go func() {
       time.Sleep(1 * time.Second)
       fmt.Println("执行run")
       time.Sleep(1 * time.Second)
       wg.Done() // 完成一个任务(计数器减一)
    }()
    fmt.Println("执行main")
    wg.Wait() // 等待Add的所有所有线程执行完
}
```

## channel

>   信道

采用生产者-消费者模式的协程间通信通道

阻塞等待并发线程返回消息

先进先出, 后进后出

### 声明

```go
var 信道名 = make(chan 消息类型, size)
```

### 消息传递

```go
// 一协程
信道名 <- 消息

// 另一协程, 阻塞等待消息
消息 := <- 信道名 
```

### 使用

```go
for i := 0; i < 3; i++ {
    go func(num int) {
       fmt.Println(formatedNow(), "函数获取到了: ", num)
       num = num * num
       time.Sleep(1 * time.Second)
       fmt.Println(formatedNow(), "发送消息:", num)
       ch <- num
    }(i)
}
fmt.Println(formatedNow(), "先处理一些其他信息, 先不急着使用结果")
time.Sleep(500 * time.Millisecond)
for i := 0; i < 3; i++ {
    msg := <-ch // 等待信道返回消息。
    fmt.Println(formatedNow(), "收到消息: ", msg)
}
fmt.Println(formatedNow(), "全部做完啦")
```

```go
16:18:52.854 先处理一些其他信息, 先不急着使用结果
16:18:52.854 函数获取到了:  1
16:18:52.854 函数获取到了:  2
16:18:52.854 函数获取到了:  0
16:18:53.876 发送消息: 4
16:18:53.877 发送消息: 0
16:18:53.877 发送消息: 1
16:18:53.877 收到消息:  4
16:18:53.877 收到消息:  0
16:18:53.878 收到消息:  1
16:18:53.878 全部做完啦

```

## select

-   阻塞等待多个channel返回消息(如果都有消极就随机触发)
-   如果有 default 子句，则执行该语句
-   没有就阻塞

```go
func foo(i int8, ch chan int8) {
    fmt.Println("send: ", i)
    ch <- i
}
func main() {
    ch0 := make(chan int8)
    ch1 := make(chan int8)
    go foo(0, ch0)
    go foo(1, ch1)
    go foo(2, ch0)
    go foo(3, ch1)
    go foo(4, ch0)
    go foo(5, ch1)
    time.Sleep(200 * time.Millisecond)
    select {
    case i := <-ch0:
       fmt.Println("get:", i)
    case i := <-ch1:
       fmt.Println("get:", i)
    }
}
```

