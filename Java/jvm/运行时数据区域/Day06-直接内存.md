# 直接内存

系统内存

并不在《Java虚拟机规范》, 所以直接内存不属于Java运行时规范

JDK1.4中引入NIO机制, 使用了直接内存

java堆中的对象如果不再使用, 就要被回收, 回收时会影响对象的创建和使用

IO操作需要先把文件读入直接内存(缓冲区), 再把数据复制到Java堆中, 反复的复制降低了效率

## 创建直接内存上的数据

```java
ByteBUffer buf = ByteBuffer.allocateDirect(size);
```

## 查看直接内存

arthus

```shell
memory
```

属性名direct

此处显示的内存大小和metaspace的大小是分开算的

## 直接内存溢出

大概7个G的时候

`OutOfMemory: Direct buffer memory`

## 配置直接内存上限

```shell
-XX:MaxDirectMemorySize=2G
```

要比压力测试的最大内存大一些

