# 地址缓存

Q : 注册中心挂了, 服务是否可以正常访问?

A : 可以. 因为dubo服务消费者在第一次调用时, 会将服务提供方地址缓存到本地, 以后在调用则不会访问注册中心

```log
19:42:19:203  WARN 11984 --- [ad(centos:2181)] org.apache.zookeeper.ClientCnxn          : Session 0x100000118400003 for server null, unexpected error, closing socket connection and attempting reconnect

java.net.ConnectException: Connection refused: no further information
	at java.base/sun.nio.ch.SocketChannelImpl.checkConnect(Native Method) ~[na:na]
	at java.base/sun.nio.ch.SocketChannelImpl.finishConnect(SocketChannelImpl.java:777) ~[na:na]
	at org.apache.zookeeper.ClientCnxnSocketNIO.doTransport(ClientCnxnSocketNIO.java:361) ~[zookeeper-3.4.9.jar:3.4.9-1757313]
	at org.apache.zookeeper.ClientCnxn$SendThread.run(ClientCnxn.java:1141) ~[zookeeper-3.4.9.jar:3.4.9-1757313]

```

但是会在服务的生产者和消费者这里一直报错

![image-20240406194008523](../assets/Day08-地址缓存/image-20240406194008523.png)

当服务提供者的地址发生变化时, 注册中心会通知服务消费者

