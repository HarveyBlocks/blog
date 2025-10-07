package ExecutorLearning;

import java.util.concurrent.*;

/**
 * @author HarveyBlocks
 * @date 2023/09/24 19:24
 **/
public class LearnCreatThreadPoolExecutor {
    //1.用有参构造器
    public static void main(String[] args) {
        /*ThreadPoolExecutor(int corePoolSize,线程池核心线程的数量
                              int maximumPoolSize,线程池最大线程数量>corePoolSize,可以额外创建临时线程
                              long keepAliveTime,指定临时线程的存活时间(空闲的时间)
                              TimeUnit unit,存活时间的单位(秒,分,时,天)
                              BlockingQueue<Runnable> workQueue,指定线程的任务队列
                                        ↑接口,不要直接上
                              ThreadFactory threadFactory,指定线程池的线程池工厂(创建核心,临时线程)
                                        ↑函数式接口
                              RejectedExecutionHandler handler指定线程池任务的拒绝策略(线程在忙,任务队列满,则何如)
                              )
         public ThreadPoolExecutor(int corePoolSize,正式工
                              int maximumPoolSize,临时工
                              long keepAliveTime,临时工闲了多久被辞退
                              TimeUnit unit,单位
                              BlockingQueue<Runnable> workQueue,客人排队处
                              ThreadFactory threadFactory,招员工的HR
                              RejectedExecutionHandler handler忙不过来(正式工在忙,临时工在忙,客人排满了)咋办
                              )
         */


        /*
        * 1. 核心线程用完了,任务队列也满了,还可以创建临时线程,就会创建临时线程
        * 2. 核心,临时线程在忙,任务队列满,有新的任务,就会开始拒绝任务
        * */
        ExecutorService pool = new ThreadPoolExecutor(
                3,
                5,
                20,
                TimeUnit.SECONDS,//枚举变量TimeUnit
                new LinkedBlockingDeque<>(),//基于链表,不限制大小
                //new ArrayBlockingQueue<>(4),基于数组,限制大小,最多缓存4个队伍
                Executors.defaultThreadFactory(),//获取默认的线程工厂,一般用这个
                new ThreadPoolExecutor.AbortPolicy()//AbortPolicy是ThreadPoolExecutor的内部类,实现了RejectedExecutionHandler接口
        );

    }
}
