# Fork-Join

## 概念



-   JDK 1.7 加入的线程池
-   分治
-   适用能被拆分成小任务的CPU密集型运算
-   将大任务拆分为算法上相同的小任务, 直至不能拆分可直接求解
-   Fork-Join将小任务分派给不同线程, 进一步提升了运算效率
-   Fork-Join默认创建与CPU核心数大小相同的线程池

## 使用

### 斐波那契

```java
@AllArgsConstructor
private static class FibonacciTask extends RecursiveTask<Integer> {
    private final int value;

    @Override
    protected Integer compute() {
        sleep(0.2);
        if (value == 0) {
            return 1;
        }
        FibonacciTask subTask = new FibonacciTask(value - 1);

        subTask.fork(); // 分配任务
        log.debug("forked : {} => {}", value, value - 1);
        Integer join = subTask.join();
        int result = join * value;
        log.debug("joined: {} * {} = {}", value, join, result);
        return result; // 取到值
    }
}
```

### 汉诺塔

```java
private static class HanoiTask extends RecursiveAction {
    private final char a;
    private final char b;
    private final char c;
    private final int depth;

    public HanoiTask(char from, char to, char by, int depth) {
        this.a = from;
        this.c = to;
        this.b = by;
        this.depth = depth;
    }

    private static void move(char from, char to) {
        // log.debug(from + " -> " + to);
    }

    @Override
    protected void compute() {
        if (depth == 0) {
            return;
        }
        HanoiTask hanoiTaskPre = new HanoiTask(a, b, c, depth - 1);
        hanoiTaskPre.fork();
        move(a, c);
        HanoiTask hanoiTaskPost = new HanoiTask(b, c, a, depth - 1);
        hanoiTaskPost.fork();
        hanoiTaskPre.join();
        hanoiTaskPost.join(); // 这样明显不对, 是为了更好分配线程
        /*
        HanoiTask hanoiTaskPre = new HanoiTask(a, b, c, depth - 1);
        hanoiTaskPre.fork();
        hanoiTaskPre.join();
        move(a, c);
        HanoiTask hanoiTaskPost = new HanoiTask(b, c, a, depth - 1);
        hanoiTaskPost.fork();
        hanoiTaskPost.join(); // 这样才对
        汉诺塔问题要求的是"策略", 策略是有序的, 故不适合使用这样多线程分配再合并的思想做
        只会再线程分配上消耗时间
        */
    }
}

public static void demo() {
    ForkJoinPool pool = new ForkJoinPool();
    HanoiTask task = new HanoiTask('A', 'C', 'B', 32);
    long start = System.currentTimeMillis();
    log.debug("{}", pool.invoke(task));
    long end = System.currentTimeMillis();
    log.debug("cost: {} s", (end - start) / 1000.0);
    // 不打印日志, 直接跑完
    // 直接递归 32个盘子, 21.753 s
    // 使用ForkJoinPool join写后面 32个盘子, 35.671 s
    // 使用ForkJoinPool join依照逻辑 24个盘子, 108.128 s
}
```

### 累和

```java
@AllArgsConstructor
private static class SummarizeTask extends RecursiveTask<Integer> {
    private final int start;
    private final int end;

    @Override
    protected Integer compute() {
        sleep(0.2);
        int differ = end - start;
        if (differ == 0) {
            return end;
        }
        if (differ == 1) {
            return end + start;
        }
        int mid = (start + end) / 2;
        SummarizeTask subTaskPre = new SummarizeTask(start, mid);
        SummarizeTask subTaskPost = new SummarizeTask(mid + 1, end);
        subTaskPre.fork();
        log.debug("forked : {} => {}", start, mid);
        subTaskPost.fork();
        log.debug("forked : {} => {}", mid + 1, end);

        Integer preJoin = subTaskPre.join();
        Integer postJoin = subTaskPost.join();
        int result = preJoin + postJoin;
        log.debug("joined: {} + {} = {}", preJoin, postJoin, result);
        return result; // 取到值
    }
}
```

递归串行执行25.854 s (sleep了0.2s)

循环串行执行20.634 s (sleep了0.2s)

22个线程并行执行 3.073s  (sleep的0.2s被利用了起来)
