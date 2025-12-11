# 主线程和守护线程

对于Java程序来说, 只有所有程序结束, Java程序才结束

而守护线程, 是在其他非守护线程结束了, 即使守护线程中还有代码没有执行完, 也会强制结束

## 设置守护线程

```java
Thread t = new Thread(() -> {
    log.debug("守护线程");
});
t.setDaemon(true);
t.start();
```

