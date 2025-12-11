# 事件执行流程

```java
private void processSelectedKey(SelectionKey k, AbstractNioChannel ch) {
    // ...    
    if ((readyOps & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) != 0 || readyOps == 0) {
        // 可读事件
        unsafe.read(); // 入口
    }
    // ...
}
```



```java
@Override
    public void read() {
        assert eventLoop().inEventLoop();
        final ChannelConfig config = config();
        final ChannelPipeline pipeline = pipeline();
        final RecvByteBufAllocator.Handle allocHandle = unsafe().recvBufAllocHandle();
        allocHandle.reset(config);

        boolean closed = false;
        Throwable exception = null;
        try {
            try {
                do {
                    int localRead = doReadMessages(readBuf); // 创建SocketChannel, 设置非阻塞
                    if (localRead == 0) {
                        break;
                    }
                    if (localRead < 0) {
                        closed = true;
                        break;
                    }

                    allocHandle.incMessagesRead(localRead);
                } while (continueReading(allocHandle));
            } catch (Throwable t) {
                exception = t;
            }

            int size = readBuf.size();
            for (int i = 0; i < size; i ++) {
                readPending = false;
                pipeline.fireChannelRead(readBuf.get(i));
            }
            readBuf.clear();
            allocHandle.readComplete();
            pipeline.fireChannelReadComplete();

            if (exception != null) {
                closed = closeOnReadError(exception);

                pipeline.fireExceptionCaught(exception);
            }

            if (closed) {
                inputShutdown = true;
                if (isOpen()) {
                    close(voidPromise());
                }
            }
        } finally {
            // Check if there is a readPending which was not processed yet.
            // This could be for two reasons:
            // * The user called Channel.read() or ChannelHandlerContext.read() in channelRead(...) method
            // * The user called Channel.read() or ChannelHandlerContext.read() in channelReadComplete(...) method
            //
            // See https://github.com/netty/netty/issues/2254
            if (!readPending && !config.isAutoRead()) {
                removeReadOp();
            }
        }
    }
}
```



## Accept流程



1.  selector.select()阻塞直到事件发生
2.  遍历处理selectKeys
3.  拿到一个key, 判断事件类型
    -   是ACCEPT
4.  创建SocketChannel, 设置为非阻塞的NioSocketChannel
5.  将SocketChannel注册至selector
6.  关注selectionKey 的read事件

### 创建SocketChannel

```java
@Override
protected int doReadMessages(List<Object> buf) throws Exception {
    SocketChannel ch = SocketUtils.accept(javaChannel()); // 创建连接

    try {
        if (ch != null) {
            buf.add(new NioSocketChannel(this, ch)); // 创建NioSocketChannel
            // 将原生的SocketChannel作为构造器的参数, 传给了NioSocketChannel
            return 1;
        }
    } catch (Throwable t) {
        // ...
    }

    return 0;
}
```

#### 创建SocketChannel

```java
public static SocketChannel accept(final ServerSocketChannel serverSocketChannel) throws IOException {
    try {
        return doPrivileged(Action->{
            @Override
            public SocketChannel run() throws IOException {
                return serverSocketChannel.accept(); // 创建连接
            }
        });
    } catch (PrivilegedActionException e) {
        // ...
    }
}
```

#### 加入readBuf

![image-20240403213323201](../../assetss/Day11-%E4%BA%8B%E4%BB%B6%E6%89%A7%E8%A1%8C%E6%B5%81%E7%A8%8B/image-20240403213323201.png)

#### 将readBuf加入ServerSocketChannel流水线

获取流水线`pipeline`

![](../../assetss/Day11-%E4%BA%8B%E4%BB%B6%E6%89%A7%E8%A1%8C%E6%B5%81%E7%A8%8B/image-20240403213739764.png)

```java
@Override
public void read() {
    // ...
    final ChannelPipeline pipeline = pipeline();
    // ...
    try {
        // ...
        int size = readBuf.size();
        for (int i = 0; i < size; i ++) {
            readPending = false;
            pipeline.fireChannelRead(readBuf.get(i));
        }
        readBuf.clear();
        //...
    } finally {
        // ...
    }
}
```

### 将SocketChannel注册至selector

由pipeline中的Accpetor(`ServerBootstrapAcceptor`)来处理连接并注册

```java
public void channelRead(ChannelHandlerContext ctx, Object msg) {
    final Channel child = (Channel) msg;

    child.pipeline().addLast(childHandler);

    setChannelOptions(child, childOptions, logger); // 设置参数
    setAttributes(child, childAttrs);

    try {
        childGroup.register(child)
            // register
            // 在childGroup(NioEventLoopGroup)里创建一个新的NioEventLoop
            // 将新的NioEventLoop里面的Selcetor与child(Channle)进行绑定
            // Selector监听新建连接的NioSocketChannel上的事件
            .addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        forceClose(child, future.cause());
                    }
                }
        });
    } catch (Throwable t) {
        forceClose(child, t);
    }
}
```



#### 注册流程

```java
@Override
public final void register(EventLoop eventLoop, final ChannelPromise promise) {
    // ...
    if (eventLoop.inEventLoop()) {
        register0(promise);
    } else {
        try {
            eventLoop.execute(new Runnable() {
                // 线程切换
                // 确保注册是在一个新的线程里去执行的
                // 即SocketChannel对应的EventLoop上去执行
                @Override
                public void run() {
                    register0(promise);
                }
            });
        } catch (Throwable t) {
            // ...
        }
    }
}
```

```java
private void register0(ChannelPromise promise) {
    try {
        // ...
        doRegister();

        // ...
        // 执行初始化操作
        pipeline.invokeHandlerAddedIfNeeded();

        // ...
        if (isActive()) {
            if (firstRegistration) {
                // 关注Read事件
                pipeline.fireChannelActive();
            } else if (...){...}
        }
    } catch (Throwable t) {
        // ...
    }
}
```

-   注册JDK原生的SocketChannel

    ```java
    @Override
    protected void doRegister() throws Exception {
        boolean selected = false;
        for (;;) {
            try {
                // JDK原生的SocketChannel的绑定与注册
                selectionKey = javaChannel()
                    .register(eventLoop().unwrappedSelector(), 
                              0/*关注的事件(没有)*/, this/*附件是Nio的SocketChannel*/);
                return;
            } catch (CancelledKeyException e) {
                // ...
            }
        }
    }
    ```

-   初始化Channel

    就是我们自己的

    ```java
    .childHandler(
            new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler());
                }
            }
    )
    ```

### 关注selectionKey 的read事件

`fireChannelActive()`->`invokeChannelActive()`->`channelActive()`

```java
@Override
public void channelActive(ChannelHandlerContext ctx) {
    ctx.fireChannelActive();
	// 在新连接的Pipeline里
    // 由Handers:
    // head=>logging(自己的)=>tail
    readIfIsAutoRead(); // <- 在这里完成对read事件的关注
}
```

`read()`->`read()`->`read()`->`invokeRead()`->`read()`->`readBegin()`->`doBeginRead()`

```java
@Override
protected void doBeginRead() throws Exception {
    // ...

    final int interestOps = selectionKey.interestOps();
    if ((interestOps & readInterestOp) == 0) {
        // 关注读时间
        selectionKey.interestOps(interestOps | readInterestOp);
    }
}
```

## Read流程

```java
    @Override
    public final void read() {
       	// ...
        final ByteBufAllocator allocator = config.getAllocator();
        final RecvByteBufAllocator.Handle allocHandle = recvBufAllocHandle();
        allocHandle.reset(config);

        ByteBuf byteBuf = null;
        // ...
        try {
            do {
                // ...
                allocHandle.lastBytesRead(doReadBytes(byteBuf));
                // ...
                pipeline.fireChannelRead(byteBuf);
                // head->logging->tail
            } while (...);

            // ...
        } catch (Throwable t) {
            // ...
        } finally {
            // ...
    }
}
```

