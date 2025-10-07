package ExecutorLearning;

import java.util.concurrent.*;

/**
 * @author HarveyBlocks
 * @date 2023/09/24 20:24
 **/
public class ThreadPoolExecutorWithCallable {
    //Callable的特点是能返回一些值
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService pool = new ThreadPoolExecutor(
                3,
                5,
                20,
                TimeUnit.SECONDS,//枚举变量TimeUnit
                new ArrayBlockingQueue<>(6),//基于数组,限制大小,最多缓存4个队伍
                Executors.defaultThreadFactory(),//获取默认的线程工厂,一般用这个
                new ThreadPoolExecutor.AbortPolicy()
                //抛出异常,丢弃任务
        );

        Future<String> submit1 = pool.submit(new MyCallable(10));//返回Future,未来任务对象
        Future<String> submit2 = pool.submit(new MyCallable());
        Future<String> submit3 = pool.submit(new MyCallable(30));

        System.out.println(submit1.get());
        System.out.println(submit2.get());
        System.out.println(submit3.get());

        //用了线程池,对一大堆任务,更划算
    }
}
class MyCallable implements Callable<String>{
    private int n;
    public MyCallable(int n){
        this.n = n;
    }
    public MyCallable(){
        this(20);
    }
    @Override
    public String call() throws Exception {
        int sum = 0;
        for (int i = 0; i < n; i++) {
            sum+=i+1;
            Thread.sleep(200);
        }
        return Thread.currentThread().getName()+"->"+sum;
    }
}