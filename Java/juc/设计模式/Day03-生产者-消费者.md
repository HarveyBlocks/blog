# 生产者-消费者模式

-   和保护性暂停模式不同的是, 不需要产生结果和消费结果的线程一一对应
-   生产者-消费者模式可以用来平衡消费和生产的线程资源
-   生产者仅赋值产生结果数据,不管数据如何处理, 消费者专心处理结果数据

## 实现

### 循环Runnable包装

```java
public class AlwaysLoopWarp implements Runnable {
    private final Runnable eachLoop;

    public AlwaysLoopWarp(Runnable eachLoop) {
        this.eachLoop = eachLoop;
    }

    @Override
    public final void run() {
        while (true) {
            eachLoop.run();
        }
    }
}
```

### 生产者接口

```java
public abstract class MyProducer<P> implements Supplier<P> {
    @Override
    public final P get() {
        return produce();
    }

    public abstract P produce();
}
```

### 消费者接口

```java
public abstract class MyConsumer<P> implements Consumer<P> {
    @Override
    public final void accept(P product) {
        consume(product);
    }

    public abstract void consume(P product);
}
```

### 消息队列注册中心(服务端)

#### 接口

```java
public interface ProducerConsumerRegistry<P> {
    void register(String notifierName,MyProducer<P> producer);

    /**
     * 消费者获取到消息后, 是所有消费者串行处理? 是各个消费者并行处理?就依靠消费者客户端决定了
     */
    void register(MyConsumer<P> consumer);

    /**
     * 生产者各自创建消息
     */
    Runnable getNotifier(String notifierName);

    /**
     * 统一将发布消息给消费者(发布的行为是串行的, 消费者处理消息是并行的)
     */
    Runnable getListeners();
}
```

#### 实现

```java
public class ProducerConsumerRegistryImpl<P> implements ProducerConsumerRegistry<P> {
    private final Queue<P> messageQueue = new ConcurrentLinkedQueue<>();
    private final Map<String,ProducerNotify> producers = new HashTable<>();
    private final List<MyConsumer<P>> consumers = new ArrayList<>();

    public ProducerConsumerRegistryImpl() {
    }

    @Override
    public void register(String notifierName,MyProducer<P> producer) {
        this.producers.put(notifierName, new ProducerNotify(producer));
    }

    @Override
    public void register(MyConsumer<P> consumer) {
        this.consumers.add(consumer);
    }

    @Override
    public Runnable getNotifier(String notifierName) {
        return producers.get(notifierName);
    }

    @Override
    public Runnable getListeners() {
        return new AlwaysLoopWarp(new ConsumersWait(consumers));
    }

    private class ProducerNotify extends AbstractNotifiedStandardPattern {
        private final MyProducer<P> producer;

        public ProducerNotify(MyProducer<P> producer) {
            super(messageQueue);
            this.producer = producer;
        }

        @Override
        protected void finishPreTask() {
            // 并行生产消息
            messageQueue.offer(producer.produce());
        }
    }

    private class ConsumersWait extends AbstractWaitStandardPattern {
        private final List<MyConsumer<P>> consumers;

        public ConsumersWait(List<MyConsumer<P>> consumers) {
            super(messageQueue);
            this.consumers = consumers;
        }

        @Override
        protected boolean isPrepared() {
            return !messageQueue.isEmpty();
        }

        @Override
        protected void executeIfPrepared() {
            P product = messageQueue.poll();
            // 串行发送消息, 并行处理消息(并行处理消息在客户端)
            for (MyConsumer<P> consumer : consumers) {
                consumer.accept(product);
            }
        }

        @Override
        protected void executeIfUnprepared() {
        }
    }

}
```

## 使用

在消息队列的服务端, 各Producer生产消息, 然后发送给消息队列中间件

然后消息队列中间件发送消息给客户端的网卡, 客户端上可能由多个Customer, 而且客户端也可能有消息堆积

故服务端和客户端都使用消息队列

### 注册生产者(服务端)

```java
private static void registerProducer(
        String producerName,
        ProducerConsumerRegistry<String> registry,
        Random random) {
    registry.register(new MyProducer<>() {
        @Override
        public String produce() {
            return producerAction(producerName, "" + random.nextInt());
        }
    });
}
```

### 组测消费者(服务端->客户端数据传输)

```java
private static void registerCustomer(String customerName, ProducerConsumerRegistry<String> registry) {
    ProducerConsumerRegistry<String> client = new ProducerConsumerRegistryImpl<>();
    client.register(new MyConsumer<>() {
        @Override
        public void consume(String product) {
            action(customerName, product);
        }
    });
    // 异步
    new Thread(client.getListeners()).start();
    // 注册服务端的信息发送到客户端后, 客户端在将消息发送给各消费者客户端(同步)
    registry.register(new MyConsumer<>() {
        @Override
        public void consume(String product) {
            // 客户端接收到消息后串行发送给各个消费者
            // 各个消费者消费时是异步的
            client.register("Client", new MyProducer<>() {
                @Override
                public String produce() {
                    return product;
                }
            });
            client.getNotifier("Client").run();
        }
    });
}
```

### 主程序

```java
public static void main(String[] args) {
    Random random = new Random(System.currentTimeMillis());
    ProducerConsumerRegistry<String> registry = new ProducerConsumerRegistryImpl<>();
    registerCustomer("CustomerA", registry);
    registerCustomer("CustomerB", registry);
    registerProducer("ProducerX", registry, random);
    registerProducer("ProducerY", registry, random);
    registerProducer("ProducerZ", registry, random);
    new Thread(registry.getListeners()).start();
    for (int i = 0; i < 20; i++) {
        // 用三个生产者发送20条数据
        // 如果三个生产者是三个线程, 每个线程可以自定义怎么发消息, 那么会涉及任务队列
        // 虽然和消息队列大差不差, 但是还是会导致测试代码更臃肿, 故略
        new Thread(registry.getNotifier(random.nextInt(3) + "")).start();
    }
}
```

### 测试日志

产品信息: 随机整数

产品: "生产者名 产品信息"

生产者日志: "产品 生产时机"

消费者日志: "消费者名 产品 生产时机"

before-after之间的时间间隔和产品长度有关, 越长消耗时间越长

```log
01:33:05.137 [Thread-3] INFO org.harvey.juc.demo.PcDemo -- ProducerZ -482816785 before
01:33:05.251 [Thread-3] INFO org.harvey.juc.demo.PcDemo -- ProducerZ -482816785 after
01:33:05.251 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerZ -482816785 before
01:33:05.251 [Thread-22] INFO org.harvey.juc.demo.PcDemo -- ProducerZ 781014989 before
01:33:05.251 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerZ -482816785 before
01:33:05.362 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerZ -482816785 after
01:33:05.362 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerZ -482816785 after
01:33:05.362 [Thread-22] INFO org.harvey.juc.demo.PcDemo -- ProducerZ 781014989 after
01:33:05.362 [Thread-21] INFO org.harvey.juc.demo.PcDemo -- ProducerY -1608552808 before
01:33:05.472 [Thread-21] INFO org.harvey.juc.demo.PcDemo -- ProducerY -1608552808 after
01:33:05.472 [Thread-20] INFO org.harvey.juc.demo.PcDemo -- ProducerX -1780346953 before
01:33:05.582 [Thread-20] INFO org.harvey.juc.demo.PcDemo -- ProducerX -1780346953 after
01:33:05.582 [Thread-19] INFO org.harvey.juc.demo.PcDemo -- ProducerY -1922220638 before
01:33:05.692 [Thread-19] INFO org.harvey.juc.demo.PcDemo -- ProducerY -1922220638 after
01:33:05.692 [Thread-18] INFO org.harvey.juc.demo.PcDemo -- ProducerX -1546763447 before
01:33:05.801 [Thread-18] INFO org.harvey.juc.demo.PcDemo -- ProducerX -1546763447 after
01:33:05.801 [Thread-17] INFO org.harvey.juc.demo.PcDemo -- ProducerX 12447302 before
01:33:05.910 [Thread-17] INFO org.harvey.juc.demo.PcDemo -- ProducerX 12447302 after
01:33:05.910 [Thread-16] INFO org.harvey.juc.demo.PcDemo -- ProducerX 1208744383 before
01:33:06.018 [Thread-16] INFO org.harvey.juc.demo.PcDemo -- ProducerX 1208744383 after
01:33:06.018 [Thread-15] INFO org.harvey.juc.demo.PcDemo -- ProducerY 1952916607 before
01:33:06.128 [Thread-15] INFO org.harvey.juc.demo.PcDemo -- ProducerY 1952916607 after
01:33:06.128 [Thread-14] INFO org.harvey.juc.demo.PcDemo -- ProducerY 1733979297 before
01:33:06.236 [Thread-14] INFO org.harvey.juc.demo.PcDemo -- ProducerY 1733979297 after
01:33:06.236 [Thread-13] INFO org.harvey.juc.demo.PcDemo -- ProducerZ 913056729 before
01:33:06.345 [Thread-13] INFO org.harvey.juc.demo.PcDemo -- ProducerZ 913056729 after
01:33:06.345 [Thread-12] INFO org.harvey.juc.demo.PcDemo -- ProducerZ -1778716314 before
01:33:06.456 [Thread-12] INFO org.harvey.juc.demo.PcDemo -- ProducerZ -1778716314 after
01:33:06.457 [Thread-10] INFO org.harvey.juc.demo.PcDemo -- ProducerY 1898826716 before
01:33:06.565 [Thread-10] INFO org.harvey.juc.demo.PcDemo -- ProducerY 1898826716 after
01:33:06.565 [Thread-11] INFO org.harvey.juc.demo.PcDemo -- ProducerZ 535376338 before
01:33:06.674 [Thread-11] INFO org.harvey.juc.demo.PcDemo -- ProducerZ 535376338 after
01:33:06.674 [Thread-9] INFO org.harvey.juc.demo.PcDemo -- ProducerY -1776201922 before
01:33:06.783 [Thread-9] INFO org.harvey.juc.demo.PcDemo -- ProducerY -1776201922 after
01:33:06.783 [Thread-5] INFO org.harvey.juc.demo.PcDemo -- ProducerY -581310412 before
01:33:06.892 [Thread-5] INFO org.harvey.juc.demo.PcDemo -- ProducerY -581310412 after
01:33:06.892 [Thread-7] INFO org.harvey.juc.demo.PcDemo -- ProducerZ -831178789 before
01:33:07.002 [Thread-7] INFO org.harvey.juc.demo.PcDemo -- ProducerZ -831178789 after
01:33:07.002 [Thread-6] INFO org.harvey.juc.demo.PcDemo -- ProducerZ 967605482 before
01:33:07.112 [Thread-6] INFO org.harvey.juc.demo.PcDemo -- ProducerZ 967605482 after
01:33:07.113 [Thread-8] INFO org.harvey.juc.demo.PcDemo -- ProducerX 441707727 before
01:33:07.220 [Thread-8] INFO org.harvey.juc.demo.PcDemo -- ProducerX 441707727 after
01:33:07.220 [Thread-4] INFO org.harvey.juc.demo.PcDemo -- ProducerZ 933749980 before
01:33:07.329 [Thread-4] INFO org.harvey.juc.demo.PcDemo -- ProducerZ 933749980 after
01:33:07.329 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerZ 781014989 before
01:33:07.329 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerZ 781014989 before
01:33:07.437 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerZ 781014989 after
01:33:07.437 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerZ 781014989 after
01:33:07.437 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerY -1608552808 before
01:33:07.437 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerY -1608552808 before
01:33:07.548 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerY -1608552808 after
01:33:07.549 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerX -1780346953 before
01:33:07.549 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerY -1608552808 after
01:33:07.549 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerX -1780346953 before
01:33:07.657 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerX -1780346953 after
01:33:07.657 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerY -1922220638 before
01:33:07.657 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerX -1780346953 after
01:33:07.657 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerY -1922220638 before
01:33:07.765 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerY -1922220638 after
01:33:07.765 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerX -1546763447 before
01:33:07.765 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerY -1922220638 after
01:33:07.765 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerX -1546763447 before
01:33:07.874 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerX -1546763447 after
01:33:07.874 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerX 12447302 before
01:33:07.874 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerX -1546763447 after
01:33:07.874 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerX 12447302 before
01:33:07.982 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerX 12447302 after
01:33:07.982 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerX 1208744383 before
01:33:07.982 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerX 12447302 after
01:33:07.982 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerX 1208744383 before
01:33:08.091 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerX 1208744383 after
01:33:08.091 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerX 1208744383 after
01:33:08.091 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerY 1952916607 before
01:33:08.091 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerY 1952916607 before
01:33:08.202 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerY 1952916607 after
01:33:08.202 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerY 1733979297 before
01:33:08.204 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerY 1952916607 after
01:33:08.204 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerY 1733979297 before
01:33:08.312 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerY 1733979297 after
01:33:08.312 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerZ 913056729 before
01:33:08.312 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerY 1733979297 after
01:33:08.312 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerZ 913056729 before
01:33:08.421 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerZ 913056729 after
01:33:08.421 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerZ -1778716314 before
01:33:08.421 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerZ 913056729 after
01:33:08.421 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerZ -1778716314 before
01:33:08.531 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerZ -1778716314 after
01:33:08.531 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerZ -1778716314 after
01:33:08.531 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerY 1898826716 before
01:33:08.531 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerY 1898826716 before
01:33:08.640 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerY 1898826716 after
01:33:08.640 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerZ 535376338 before
01:33:08.640 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerY 1898826716 after
01:33:08.640 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerZ 535376338 before
01:33:08.751 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerZ 535376338 after
01:33:08.751 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerZ 535376338 after
01:33:08.751 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerY -1776201922 before
01:33:08.751 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerY -1776201922 before
01:33:08.860 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerY -1776201922 after
01:33:08.860 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerY -581310412 before
01:33:08.860 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerY -1776201922 after
01:33:08.860 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerY -581310412 before
01:33:08.970 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerY -581310412 after
01:33:08.971 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerZ -831178789 before
01:33:08.971 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerY -581310412 after
01:33:08.971 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerZ -831178789 before
01:33:09.079 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerZ -831178789 after
01:33:09.079 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerZ -831178789 after
01:33:09.079 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerZ 967605482 before
01:33:09.079 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerZ 967605482 before
01:33:09.189 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerZ 967605482 after
01:33:09.189 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerX 441707727 before
01:33:09.189 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerZ 967605482 after
01:33:09.189 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerX 441707727 before
01:33:09.300 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerX 441707727 after
01:33:09.300 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerZ 933749980 before
01:33:09.300 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerX 441707727 after
01:33:09.300 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerZ 933749980 before
01:33:09.409 [Thread-1] INFO org.harvey.juc.demo.PcDemo -- CustomerB ProducerZ 933749980 after
01:33:09.409 [Thread-0] INFO org.harvey.juc.demo.PcDemo -- CustomerA ProducerZ 933749980 after

```

