# 启动流程

## NIO启动流程

Netty基于NIO, 那么有必要知道NIO的启动流程

1.  创建NioEventLoopGroup来封装Selector

    ```java
    Selector selector = Selector.open();
    ```

2.  创建NioServerSocketChannel, 同时会初始化它的关联的Handler , 以及原生的ssc存储config

    ```java
    NioServerSocketChannel attachment = new NioServerSocketCannel();
    ```

3.  创建NioServerSocketChannel式, 创建了Java原生的ServerSocketChannel

    ```java
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open(); // ssc
    serverSocketChannel.configureBlocking(false);
    ```

4.  启动NioEventLoopGroup的BOSS线程执行接下来的任务

5.  注册(仅关联Selector和NioServerSocketChannel) , 未关注事件

    ```java
    SelectionKey selectsionKey = serverSocketChannel.regiter(selector, 0, attachment);
    ```

6.  head->初始化器->ServerBootstrapAcceptor->tail, 初始化器式一次性的, 只为添加acceptor

7.  绑定端口

    ```java
    serverSocketChannel.bind(new InetSocketAddress(8080));
    ```

8.  触发Channnel active事件, 在head中关注op_accept事件

    ```java
    selectionKey.interesOps(SelectionKey.OP_ACCEPT);
    ```

## bind

```java
private ChannelFuture doBind(final SocketAddress localAddress) {
    final ChannelFuture regFuture = initAndRegister();
    final Channel channel = regFuture.channel();
    if (regFuture.cause() != null) {
        return regFuture;
    }

    if (regFuture.isDone()) {
        // At this point we know that the registration was complete and successful.
        ChannelPromise promise = channel.newPromise();
        doBind0(regFuture, channel, localAddress, promise);
        return promise;
    } else {
        // Registration future is almost always fulfilled already, but just in case it's not.
        final PendingRegistrationPromise promise = new PendingRegistrationPromise(channel);
        regFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Throwable cause = future.cause();
                if (cause != null) {
                    // Registration on the EventLoop failed so fail the ChannelPromise directly to not cause an
                    // IllegalStateException once we try to access the EventLoop of the Channel.
                    promise.setFailure(cause);
                } else {
                    // Registration was successful, so set the correct executor to use.
                    // See https://github.com/netty/netty/issues/2586
                    promise.registered();

                    doBind0(regFuture, channel, localAddress, promise);
                }
            }
        });
        return promise;
    }
}
```

### initAndRegister()

创建ServerSocketChannel, 并注册到ServerSocketChannel上

```java
ServerSocketChannel serverSocketChannel = ServerSocketChannel.open(); // ssc
serverSocketChannel.configureBlocking(false);

Selector selector = Selector.open();
NioServerSocketChannel attachment = new NioServerSocketCannel();
SelectionKey selectsionKey = serverSocketChannel.regiter(selector, 0, attachment);

```

### init

```Java
Channel channel = null;
try {
    channel = channelFactory.newChannel();
    init(channel);
} catch (Throwable t) {
    if (channel != null) {
        // channel can be null if newChannel crashed (eg SocketException("too many open files"))
        channel.unsafe().closeForcibly();
        // as the Channel is not registered yet we need to force the usage of the GlobalEventExecutor
        return new DefaultChannelPromise(channel, GlobalEventExecutor.INSTANCE).setFailure(t);
    }
    // as the Channel is not registered yet we need to force the usage of the GlobalEventExecutor
    return new DefaultChannelPromise(new FailedChannel(), GlobalEventExecutor.INSTANCE).setFailure(t);
}
```

-   int(channel):

    ```java
    ChannelPipeline p = channel.pipeline();

    // ...

    p.addLast(new ChannelInitializer<Channel>() {
        // 该Handler被注入, 但还未被调用, 在完成Register之后被调用
        @Override
        public void initChannel(final Channel ch) {
            ...
        }
    });
    ```

### register

```java
ChannelFuture regFuture = config().group().register(channel);
if (regFuture.cause() != null) {
    if (channel.isRegistered()) {
        channel.close();
    } else {
        channel.unsafe().closeForcibly();
    }
}

// If we are here and the promise is not failed, it's one of the following cases:
// 1) If we attempted registration from the event loop, the registration has been completed at this point.
//    i.e. It's safe to attempt bind() or connect() now because the channel has been registered.
// 2) If we attempted registration from the other thread, the registration request has been successfully
//    added to the event loop's task queue for later execution.
//    i.e. It's safe to attempt bind() or connect() now:
//         because bind() or connect() will be executed *after* the scheduled registration task is executed
//         because register(), bind(), and connect() are all bound to the same thread.

return regFuture;
```

-   regiter(channel)

    ```java
    @Override
    public final void register(EventLoop eventLoop, final ChannelPromise promise) {
        ObjectUtil.checkNotNull(eventLoop, "eventLoop");
        if (isRegistered()) {...}
        if (!isCompatible(eventLoop)) {...}

        AbstractChannel.this.eventLoop = eventLoop;

        if (eventLoop.inEventLoop()) {
            // 当前线程是NIO线程
            register0(promise);
        } else {
            try {
                // 线程切换
                eventLoop.execute(new Runnable() {
                    @Override
                    public void run() {
                        register0(promise);
                    }
                });
            } catch (Throwable t) {
                logger.warn(
                        "Force-closing a channel whose registration task was not accepted by an event loop: {}",
                        AbstractChannel.this, t);
                closeForcibly();
                closeFuture.setClosed();
                safeSetFailure(promise, t);
            }
        }
    }
    ```

    ```java
        @Override
        protected void doRegister() throws Exception {
            boolean selected = false;
            for (;;) {
                try {
                    selectionKey = javaChannel().register(eventLoop().unwrappedSelector(), 0, this);
                    return;
                } catch (CancelledKeyException e) {
                    if (!selected) {
                        // Force the Selector to select now as the "canceled" SelectionKey may still be
                        // cached and not removed because no Select.select(..) operation was called yet.
                        eventLoop().selectNow();
                        selected = true;
                    } else {
                        // We forced a select operation on the selector before but the SelectionKey is still cached
                        // for whatever reason. JDK bug ?
                        throw e;
                    }
                }
            }
        }
    ```

-   调用之前的init()里的initChannel()

    ```java
    final ChannelPipeline pipeline = ch.pipeline();
    ChannelHandler handler = config.handler();
    if (handler != null) {...}

    ch.eventLoop().execute(new Runnable() {
        @Override
        public void run() {
            // 添加了Acceptor的Handler, 在Accept事件发生后建立连接
            pipeline.addLast(new ServerBootstrapAcceptor(
                    ch, currentChildGroup, 
                	currentChildHandler, 
                	currentChildOptions, 
                	currentChildAttrs));
        }
    });
    ```

### doBind0(regFuture, channel, localAddress, promise);

绑定端口

```java
boolean wasActive = isActive();
try {
    doBind(localAddress); // 最终调用Java原生的ServerSocketChannel
} catch (Throwable t) {
    safeSetFailure(promise, t);
    closeIfClosed();
    return;
}

if (!wasActive && isActive()) {
    // 执行Handler
    invokeLater(new Runnable() {
        @Override
        public void run() {
            // head->我们的->tail
            pipeline.fireChannelActive();
        }
    });
}
```

在HeadContext这个Handler里, 我们的ServerSocketChannel关注了OP_ACCEPT事件

```java
@Override
protected void doBeginRead() throws Exception {
    // Channel.read() or ChannelHandlerContext.read() was called
    final SelectionKey selectionKey = this.selectionKey;
    if (!selectionKey.isValid()) {
        return;
    }

    readPending = true;

    final int interestOps = selectionKey.interestOps();
    if ((interestOps & readInterestOp) == 0) {
        selectionKey.interestOps(interestOps | readInterestOp);
    }
}
```

