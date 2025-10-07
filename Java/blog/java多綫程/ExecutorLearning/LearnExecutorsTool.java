package ExecutorLearning;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author HarveyBlocks
 * @date 2023/09/24 20:45
 **/

public class LearnExecutorsTool {
    //Executors里全是静态方法
    public static void main(String[] args) {
        //性质A:如果某个线程因为异常而结束,则创建一个新的线程顶替原线程
		
        ExecutorService pool1 = Executors.newFixedThreadPool(3);
        //核心3条,总线程3条,性质A
        ExecutorService pool2 = Executors.newSingleThreadExecutor();
        // 核心1条,总线程1条,性质A
        ExecutorService pool3 = Executors.newCachedThreadPool();
        //线程数随任务的增加而增加,线程空闲了60s就会被回收
        ExecutorService pool4 = Executors.newScheduledThreadPool(3);
        //核心3条,在给定的延时后运行任务,或定期执行任务

        //其本质还是ThreadPoolExecutor

        //计算密集型(长期做计算任务) 核心线程数 = cpu核数 + 1;
        //IO密集型(长期做文件处理和通信任务)  核心线程数 = cpu核数 + 2;
        //cpu核数 : Ctrl + Alt + Delete => 任务管理器 => 性能 => 逻辑处理器(这台电脑是20核)

        //注意!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //大型并发环境(京东,淘宝,一瞬间几千几万人一起上)中使用Executors如果不注意可能会出现系统风险
                //1. 其不限制任务数的大小,导致OOM(内存溢出异常)
                //1. 其不限制线程数的大小,导致OOM(内存溢出异常)
        //这个时候应该用ThreadPoolExecutor
    }
}