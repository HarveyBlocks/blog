# 异步

##异步优势

为什么采用异步

和直接"多线程"有何不同

为什么不采用单线程?



假设去医院看病

流程如下:

```mermaid
graph LR

资讯医生-->抽血
抽血 --> 拿着血液报告资讯医生
```

这是一条线, 用单线程吗? 

啊? 难道抽血全程还要医生紧跟着吗?

这个医生资讯的时候可以下一个病人吗, 这个病人自己去(创建一条新线程[抽血的护士])抽血不久好了吗?

抽好血之后带着报告去原来的医生哪里排队就好了嘛

拿着报告资讯医生不就是

```java
future.addListener(future -> log.warn("执行异步后操作..."));
```

 

-   请求时要切换线程其实增加时损耗
    -   Netty的EventLoop内部使用了单线程线程池, 不用创建销毁线程, 应该不会消费很多时间吧?
-   但是增加了吞吐量: 处理请求的个数



## Future与Promise

Netty的Future是JDK的Future的子类, Netty的Promise子类是Netty的Future的子类

-   JDK的Future
    -   只能同步等待任务结束(成功或失败), 才能得到结果
    -   任务太单一
-   netty的Future
    -   可以同步等待任务接收得到结果(`sync()`, 使用主线程执行后续结果)
    -   可以异步方式得到结果(`addListener()`, 使用的是同一个EventLoop的线程)
    -   但都要等待任务结束
-   netty的Promise
    -   有Netty的Future的功能
    -   脱离了线程独立存在, 只作为两个线程间传递结果的容器
    -   有两个`set`方法设置成功结果和失败结果

###部分API

| 功能/名称    | jdk Future                     | netty Future                                                 | Promise      |
| ------------ | ------------------------------ | ------------------------------------------------------------ | ------------ |
| cancel       | 取消任务                       | -                                                            | -            |
| isCanceled   | 任务是否取消                   | -                                                            | -            |
| isDone       | 任务是否完成，不能区分成功失败 | -                                                            | -            |
| get          | 获取任务结果，阻塞等待         | -                                                            | -            |
| getNow       | -                              | 获取任务结果，非阻塞，还未产生结果时返回 null                | -            |
| isSuccess    | -                              | 判断任务是否成功                                             | -            |
| await        | -                              | 等待任务结束，如果任务失败，不会抛异常，而是通过 (close.await().isSuccess()) 判断 | -            |
| sync         | -                              | 等待任务结束，如果任务失败，抛出异常                         | -            |
| addLinstener | -                              | 添加回调，异步接收结果                                       | -            |
| cause        | -                              | 获取失败信息，非阻塞，如果没有失败，返回null                 | -            |
| setSuccess   | -                              | -                                                            | 设置成功结果 |
| setFailure   | -                              | -                                                            | 设置失败结果 |

Promise更类似于面向切面吧? 

### JDK Future创建

```java
ExecutorService executorService = Executors.newSingleThreadExecutor();
Future<Integer> submit = executorService.submit(() -> 10);
```

###Netty Future创建

```java
Future<Integer> submit = new NioEventLoopGroup().next().submit(() -> 10);
```

### Promise的创建使用

```java
EventLoop eventLoop = new NioEventLoopGroup().next();
Promise<Integer> promise1 = eventLoop.newPromise();
ExecutorService executorService = Executors.newSingleThreadExecutor();
DefaultPromise<Integer> promise2 = new DefaultPromise<>(eventLoop);
```

```java
executorService.submit(()->{
    try {
        log.debug("execute");
        Thread.sleep(1000);
        if(flag){
            promise.setSuccess(10);
        }else {
            int a = 1/0;
        }
    } catch (RuntimeException e) {
        promise.setFailure(e);
        throw new RuntimeException(e);
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
});
```

