# 任务调度

定时任务, 延时任务



## Timer

```java
Timer timer = new Timer();
long delayMillion = 1000L;
timer.schedule(new TimerTask() {
    @Override
    public void run() {
        log.debug("start 1");
        sleep(2);
        log.debug("finish 1");
    }
}, delayMillion);
timer.schedule(new TimerTask() {
    @Override
    public void run() {
        log.debug("start 2");
        sleep(2);
        log.debug("finish 2");
    }
}, delayMillion);
log.debug("...");
```

TimerTask不是函数式接口, 所以不能Lambda, 悲

### 串行执行

只有一个线程来运行所有的任务(悲)

所以第二个任务, 即使要求在"延迟1s", 也只能等到第一个任务执行完之后执行, 不能第一时间执行

```log
21:53:07.195 [main] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- ...
21:53:08.196 [Timer-0] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- start 1
21:53:10.198 [Timer-0] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- finish 1
21:53:10.198 [Timer-0] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- start 2
21:53:12.213 [Timer-0] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- finish 2
```

### 异常中断

出现异常, 整个Timer会直接中断

```java
Timer timer = new Timer();
long delayMillion = 1000L;
timer.schedule(new TimerTask() {
    @Override
    public void run() {
        log.debug("start 1");
        int x = 1 / 0;
        sleep(2);
        log.debug("finish 1");
    }
}, delayMillion);
timer.schedule(new TimerTask() {
    @Override
    public void run() {
        log.debug("start 2");
        sleep(2);
        log.debug("finish 2");
    }
}, delayMillion);
log.debug("...");
```

```log
21:57:00.498 [main] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- ...
21:57:01.508 [Timer-0] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- start 1
Exception in thread "Timer-0" java.lang.ArithmeticException: / by zero
	at org.harvey.juc.juc.excutors.ScheduleExecutors$1.run(ScheduleExecutors.java:30)
	at java.base/java.util.TimerThread.mainLoop(Timer.java:556)
	at java.base/java.util.TimerThread.run(Timer.java:506)

进程已结束，退出代码为 0
```

## ScheduledExecutorService

在delay的延时等待, 和定时的时间间隔中, 会见缝插针地执行任务, 所以即使只有三个线程, 也可以执行超过三个的定时任务



### 构建

```java
ScheduledExecutorService pool = Executors.newScheduledThreadPool(3);
```



### 延时任务

```java
pool.schedule(() -> {
    log.debug("start 1");
    sleep(2);
    log.debug("finish 1");
}, 1, TimeUnit.SECONDS);
pool.schedule(() -> {
    log.debug("start 2");
    sleep(2);
    log.debug("finish 2");
}, 1, TimeUnit.SECONDS);
pool.schedule(() -> {
    log.debug("start 3");
    int x = 1 / 0;
    sleep(2);
    log.debug("finish 3");
}, 1, TimeUnit.SECONDS);
pool.schedule(() -> {
    log.debug("start 4");
    sleep(2);
    log.debug("finish 4");
}, 1, TimeUnit.SECONDS);
log.debug("...");
```

```log
22:01:09.432 [main] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- ...
22:01:10.442 [pool-1-thread-1] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- start 1
22:01:10.442 [pool-1-thread-2] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- start 2
22:01:10.442 [pool-1-thread-3] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- start 3
22:01:10.442 [pool-1-thread-3] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- start 4
22:01:12.443 [pool-1-thread-3] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- finish 4
22:01:12.443 [pool-1-thread-1] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- finish 1
22:01:12.443 [pool-1-thread-2] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- finish 2
```



### 定时任务

```java
pool.scheduleAtFixedRate(() -> {
    log.debug("你好1");
    throw new RuntimeException(); // 定时任务直接中断
}, 2, 3, TimeUnit.SECONDS);
pool.scheduleAtFixedRate(() -> {
    log.debug("你好2");
}, 2, 1, TimeUnit.SECONDS);
log.debug("...");
```

```log
22:06:06.095 [main] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- ...
22:06:08.099 [pool-1-thread-1] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- 你好1
22:06:08.099 [pool-1-thread-2] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- 你好2
22:06:09.099 [pool-1-thread-1] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- 你好2
22:06:10.098 [pool-1-thread-1] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- 你好2
22:06:11.107 [pool-1-thread-1] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- 你好2
22:06:12.109 [pool-1-thread-2] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- 你好2
22:06:13.109 [pool-1-thread-2] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- 你好2
22:06:14.107 [pool-1-thread-2] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- 你好2
22:06:15.100 [pool-1-thread-2] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- 你好2
```

#### scheduleAtFixedRate

如果任务执行时间长于间隔时间, 则为之奈何?

```java
pool.scheduleAtFixedRate(() -> {
    log.debug("start");
    sleep(3);
    log.debug("end");
}, 0, 1, TimeUnit.SECONDS);
```

```log
22:10:12.816 [pool-1-thread-1] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- start
22:10:15.833 [pool-1-thread-1] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- end
22:10:15.834 [pool-1-thread-1] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- start
22:10:18.844 [pool-1-thread-1] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- end
22:10:18.844 [pool-1-thread-2] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- start
22:10:21.855 [pool-1-thread-2] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- end
22:10:21.855 [pool-1-thread-1] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- start
22:10:24.865 [pool-1-thread-1] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- end
22:10:24.865 [pool-1-thread-1] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- start

```

从方法开始时时间间隔开始计时, 而不是从方法结束开始计时

#### scheduleAtFixedDelay

```java
pool.scheduleWithFixedDelay(() -> {
    log.debug("start");
    sleep(3);
    log.debug("end");
}, 0, 1, TimeUnit.SECONDS);
```

```log
22:11:47.406 [pool-1-thread-1] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- start
22:11:50.425 [pool-1-thread-1] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- end
22:11:51.425 [pool-1-thread-1] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- start
22:11:54.439 [pool-1-thread-1] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- end
22:11:55.454 [pool-1-thread-2] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- start
22:11:58.462 [pool-1-thread-2] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- end
22:11:59.475 [pool-1-thread-2] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- start
22:12:02.481 [pool-1-thread-2] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- end
22:12:03.484 [pool-1-thread-2] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- start
22:12:06.498 [pool-1-thread-2] DEBUG org.harvey.juc.juc.excutors.ScheduleExecutors -- end

```

从方法结束开始计时时间间隔

### 处理异常

不会在控制台打印异常栈, 则为之奈何?

1.  在run里写try-catch
2.  用Callable-Future, 也能获取到异常

## 实践

每周四晚六点执行代码

```java
LocalDateTime now = LocalDateTime.now();
// 本周周四
LocalDateTime thisWeekThus = now.withHour(18).withMinute(0).withSecond(0).with(DayOfWeek.THURSDAY);
LocalDateTime nextThus;
if (now.isAfter(thisWeekThus)) {
    nextThus = thisWeekThus.plusWeeks(1L);
} else {
    nextThus = thisWeekThus;
}
long initDelay = Duration.between(now, nextThus).toMillis();
long period = TimeUnit.DAYS.toMillis(7);
System.out.println(nextThus);
ScheduledExecutorService pool = Executors.newScheduledThreadPool(3);
pool.scheduleAtFixedRate(() -> {
    log.debug("现在是周四晚六点");
}, initDelay, period, TimeUnit.MILLISECONDS);
```

