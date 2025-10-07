package ExecutorLearning;

import java.util.concurrent.*;

/**
 * @author HarveyBlocks
 * @date 2023/09/24 19:49
 **/
public class LearnCreatThreadPoolExecutorExactly {
    public static void main(String[] args) {
        ExecutorService pool = new ThreadPoolExecutor(
                3,
                5,
                20,
                TimeUnit.SECONDS,//枚举变量TimeUnit
                new ArrayBlockingQueue<>(6),//基于数组,限制大小,最多缓存4个队伍
                Executors.defaultThreadFactory(),//获取默认的线程工厂,一般用这个
                new ThreadPoolExecutor.AbortPolicy()
                //AbortPolicy是ThreadPoolExecutor的内部类,实现了RejectedExecutionHandler接口
                    //抛出异常,丢弃任务
        );

        Runnable target = new MyRunnable();

        pool.execute(target);//pool.execute(Runnable);执行Runnable任务
        //线程池会自动创建一个新线程,自动处理这个任务,自动执行;
        pool.execute(target);
        //线程池会自动创建一个新线程,自动处理这个任务,自动执行;
        pool.execute(target);
        //线程池会自动创建一个新线程,自动处理这个任务,自动执行;

        //以下任务开始排在任务队列
        pool.execute(target);
            // 这时候任务队列未满,不会创建临时线程,等待核心线程空闲
        pool.execute(target);
        pool.execute(target);
        pool.execute(target);
        pool.execute(target);
        pool.execute(target);//这里任务队列满了
        pool.execute(target);//这里开始创建临时线程
        pool.execute(target);//这里创建第二个临时线程

        pool.execute(target);//这里开始拒绝新任务,会抛出异常
        pool.execute(target);

        /*
        * 丢弃策略:
        * ThreadPoolExecutor.AbortPolicy()抛出异常,丢弃任务
        * ThreadPoolExecutor.CallerRunsPolicy()丢弃任务,不抛异常
        * ThreadPoolExecutor.DiscardOldestPolicy()抛弃队列中等待最久(队列特点:先进先出)的任务,然后把当前任务加入队列
        * ThreadPoolExecutor.DiscardPolicy()绕过线程池主线程来执行
        * */


        //线程池里的线程不会直接死亡,它会等待下一个任务
        //想关掉?:
        pool.shutdown();//等待任务都执行完,关闭线程池
        //pool.shutdownNow();//立刻关闭线程池,然后抛异常

    }
}
class MyRunnable implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println(Thread.currentThread().getName()+"->"+i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}