# Write

## 问题的出现

服务端

```java
public void write(SocketChannel sc) throws IOException {
    StringBuilder sb = new StringBuilder();
    int i = 3000_0000;
    while (i-- > 0) {
        sb.append("a");
    }
    ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());
    int count = 0;
    while (buffer.hasRemaining()) {
        int write = sc.write(buffer);// 不会一次性写入,会分批次写, 返回实际写入的数据
        count += write;
        log.debug("count = {},write={}", count, write);
    }
}
```

```java
SocketChannel sc = acceptHandler.accept(ssc, selector);
writeHandler.write(sc);
```

客户端

```java
public void readClient() throws IOException, InterruptedException {
    SocketChannel sc = SocketChannel.open(new InetSocketAddress("localhost", 8080));
    int count = 0;
    ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
    while (true){
        int read = sc.read(buffer);// 阻塞式的
        count += read;
        log.debug("count = {},read = {}",count,read);
        buffer.clear();
    }
}
```

测试结果

```log
count = 4194272,write=4194272
count = 26869555,write=22675283
count = 26869555,write=0
count = 26869555,write=0
count = 26869555,write=0
count = 26869555,write=0
count = 26869555,write=0
count = 26869555,write=0
count = 26869555,write=0
count = 26869555,write=0
count = 26869555,write=0
count = 26869555,write=0
count = 30000000,write=3130445
```

发现有多次无法写入数据, 这是由于数据量太大, 导致操作系统无法参与分配这么大的**网络缓冲区**

网络缓冲区满了之后, 服务器什么也不能做, 但却**不得不反复循环, 浪费了资源**

## 可写事件

让select关注是否**可以往网络缓冲区写数据**, 然后提醒服务器, 而不是不断去尝试无意义的`write = 0`

```java
if (key.isAcceptable()) {
    SelectionKey scKey = acceptHandler.accept(ssc, selector);
    ByteBuffer buffer = ceateBigBuffer();
    if (buffer.hasRemaining()) {
        // 添加读事件
        scKey.interestOps(
                scKey.interestOps() | SelectionKey.OP_WRITE);
        scKey.attach(buffer);
    }
} 
```

```java
else if (key.isWritable()) {
    ByteBuffer buffer = (ByteBuffer) key.attachment();
    if (!writeHandler.write((SocketChannel) key.channel(), buffer)) {
        // 写完了
        key.attach(null); 
        // 考虑到写事件, 会由于buffer为null报空指针
        // key.attach(ByteBuffer.allocate(AcceptHandler.DEFAULT_READ_BUFFER_CAPCITY));
        // 由于这是测试, 没有啥业务逻辑,可能没有完全之策
        // 毕竟ceateBigBuffer()还是很无厘头的
        int ops = key.interestOps() - SelectionKey.OP_WRITE;
        key.interestOps(ops);
    }
}
```

这样好了

```java
else if (key.isReadable()) {
    try {
        ByteBuffer readBuffer = (ByteBuffer) key.attachment();
        if (/*容量不够*/!readHandler.read((SocketChannel) key.channel(), readBuffer)) {
            key.attach(expansion(readBuffer));
        }
        ByteBuffer writeBuffer = ceateBigBuffer();
        if (writeBuffer.hasRemaining()) {
            key.interestOps(
                    key.interestOps() | SelectionKey.OP_WRITE);
            key.attach(writeBuffer);
        }
    } catch (IOException e) {
        log.warn(e.getMessage());
        key.cancel();
    }
} 
```

`int ops = key.interestOps() - SelectionKey.OP_WRITE;`

对于可写事件的监听需要及时去除, 因为它监听的是**能否有空间写数据**, 

如果没有写数据的需求, 而又打开了对可写的监听, 则会一直监听到可写的事件

```text
如果在往客户端写的时候,写到一半, 不可写了,而暂时终止
就乘着间隙, 客户端同时发来了数据
此时网卡会先进入可读的分支, 然后读取数据, 然后对发来的数据进行处理
而且attachment会被覆盖
此时写了一半的数据, 客户端会怎么处理呢? 放着不管吗?
客户端会不会出现由于一直等待后半段的数据而接收不到新数据的情况?
不太会... 因为可以同时发生多个事件
客户端会不会出现发到一般的数据与新数据混合在一起导致错误的问题?
```

不会, TCP协议的边界保证了数据的完整性~~

