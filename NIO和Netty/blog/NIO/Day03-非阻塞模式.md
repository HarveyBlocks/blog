# 非阻塞模式

## 阻塞切换为非阻塞

### 将`SocketServerChannel#accept`切换为非阻塞模式

```java
ssc.configureBlocking(false); // 默认true, 阻塞
```



```java
SocketChannel sc = ssc.accept(); // 非阻塞方法, 连接不到sc为null
if(sc!=null){
    log.debug("connected... {}", sc);
    channels.add(sc);
}
```



### 将`SocketChannel#read`切换为非阻塞模式

```java
sc.configureBlocking(false);
```



```java
int read = channel.read(buffer);// 非阻塞方法, 未读到数据, read返回0
if(read!=0){
    buffer.flip();
    debugRead(buffer);
    buffer.clear();
    log.debug("after read...{}", channel);
}
```

## 单线程与轮询带来的问题

-   对CPU的资源占用过大, 负担太重
-   我们希望线程在有数据时工作
