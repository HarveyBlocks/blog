# BlockingQueue

##LinkedBlockingQueue

`LinkedBlockingQueue`的capacity是上限, 是finnal的字段

基于生产者-消费者模式

线程对象Worker: 

```java
@Override
public void run(){
    while(task!=null||(task = taskQueue.take()!=null)){
        try{
            task.run();
        }finnaly{
            task = null;
        }
    }
    synchronized(workers){
        workers.remove(this);
    }
}
```

## SynchronousQueue

同步队列

没有容量, 如果没有线程从队列中取元素, 就没办法从

```java
BlockingQueue<String> messages = new SynchronousQueue<>();
for (int i = 0; i < 3; i++) {
    int num = i;
    new Thread(() -> {

        String s = "message " + num;
        log.debug("放入: " + s);
        try {
            messages.put(s);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.debug(s + " 放好了");
    }).start();
}
sleep(1);
for (int i = 0; i < 3; i++) {
    log.debug("开始取");
    try {
        log.debug("取到了: " + messages.take());
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
    sleep(0.2);
}
```

```log
20:17:35.963 [Thread-2] DEBUG org.harvey.juc.juc.BlockingQueueDemo -- 放入: message 2
20:17:35.963 [Thread-0] DEBUG org.harvey.juc.juc.BlockingQueueDemo -- 放入: message 0
20:17:35.963 [Thread-1] DEBUG org.harvey.juc.juc.BlockingQueueDemo -- 放入: message 1
20:17:36.964 [main] DEBUG org.harvey.juc.juc.BlockingQueueDemo -- 开始取
20:17:36.964 [main] DEBUG org.harvey.juc.juc.BlockingQueueDemo -- 取到了: message 1
20:17:36.967 [Thread-1] DEBUG org.harvey.juc.juc.BlockingQueueDemo -- message 1 放好了
20:17:37.169 [main] DEBUG org.harvey.juc.juc.BlockingQueueDemo -- 开始取
20:17:37.170 [main] DEBUG org.harvey.juc.juc.BlockingQueueDemo -- 取到了: message 2
20:17:37.170 [Thread-2] DEBUG org.harvey.juc.juc.BlockingQueueDemo -- message 2 放好了
20:17:37.374 [main] DEBUG org.harvey.juc.juc.BlockingQueueDemo -- 开始取
20:17:37.375 [main] DEBUG org.harvey.juc.juc.BlockingQueueDemo -- 取到了: message 0
20:17:37.375 [Thread-0] DEBUG org.harvey.juc.juc.BlockingQueueDemo -- message 0 放好了
```

