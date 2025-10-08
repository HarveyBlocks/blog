# AIO

>   Asynchronous IO 异步IO

AIO 用来解决数据复制阶段的阻塞问题

* 同步在进行读写操作时，线程需要等待结果，相当于闲置
* 异步在进行读写操作时，**线程不必等待结果**，而是将来由操作系统来通过**回调方式**由**另外的线程来获得结果**
* 异步模型需要底层操作系统（Kernel）提供支持

    * Windows 系统通过 IOCP 实现了真正的异步 IO
    * Linux 系统异步 IO 在 2.6 版本引入，但其底层实现还是用多路复用模拟了异步 IO，性能没有优势
* Netty 由于在5.0版本实现了"效率不提高, 用法难度高, 维护还困难"的异步IO. 5.0版本被废弃, 现在最高版本依旧是4.0

## 文件 AIO

先来看看 AsynchronousFileChannel

```java
Path path = Paths.get(RESOURCE_PATH_PREFIX+"data.txt");
try ( AsynchronousFileChannel asyncFc = 
     AsynchronousFileChannel.open(path, StandardOpenOption.READ)){
    tryAsyncRead(asyncFc);
    log.debug("do other things...");
    // 异步的执行使用的是守护线程
    // 为避免主线程的停止导致的守护线程意外停止, 使用System.in.read()
	int read = System.in.read();
	log.debug("System.in.read :{}", read);
} catch (IOException e) {
    log.error(e.getMessage(),e);
}
```

`private static void tryAsyncRead(AsynchronousFileChannel asyncFc) throws IOException`

```java
ByteBuffer buffer = ByteBuffer.allocate(64);
log.debug("begin...");
asyncFc.read(buffer, 0/*读取的起始未知*/, null/*附件*/,
        /*
        * CompletionHandler<读取到的字节数, 附件类型>
        * 新线程来执行下面的方法
        * */
        new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                log.debug("read completed...{}", result);
                buffer.flip();
                log.debug(debugAll(buffer));
            }
            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                log.error("read failed...");
            }
        });
```

输出

```
13:36:35.545 [main] DEBUG com.harvey.netty.nio.demo.AioDemo1 - begin...
13:36:35.550 [main] DEBUG com.harvey.netty.nio.demo.AioDemo1 - do other things...
13:36:35.550 [Thread-20] DEBUG com.harvey.netty.nio.demo.AioDemo1 - read completed...44
13:36:35.567 [Thread-20] DEBUG io.netty.util.internal.logging.InternalLoggerFactory - Using SLF4J as the default logging framework
13:36:35.637 [Thread-20] DEBUG com.harvey.netty.nio.demo.AioDemo1 - 
+--------+-------------------- all ------------------------+----------------+position: [00], limit: [44]
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 48 65 6c 6c 6f 2c 77 6f 72 6c 64 2e 0d 0a 49 27 |Hello,world...I'|
|00000010| 6d 20 5a 68 61 6e 67 20 53 61 6e 2e 0d 0a 48 6f |m Zhang San...Ho|
|00000020| 77 20 61 72 65 20 79 6f 75 3f 0d 0a 00 00 00 00 |w are you?......|
|00000030| 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 |................|
+--------+-------------------------------------------------+----------------+
```

* 响应文件读取成功的是另一个线程 Thread-20
* 主线程并没有 IO 操作阻塞





## 网络 AIO

只有网络才支持多路复用

网路的异步IO很长, 概念很多, 略
