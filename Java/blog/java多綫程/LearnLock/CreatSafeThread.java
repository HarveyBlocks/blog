package LearnLock;
//学习线程安全整体实践,整体和class CreatUnsafeThread一样
import java.util.Calendar;

/**
 * @author HarveyBlocks
 * @date 2023/09/22 17:04
 **/
public class CreatSafeThread {
    //新建并启动一个线程
    public void print2Threads(){
        System.out.println("time(s)\t\t" +
                "thread-1" +
                "\t\t\t" +
                "thread-2"
        );
        Runnable thread= () -> printManyTimes();
        new Thread(thread,"thread-1").start();
        new Thread(thread,"\t\t\t\t\t"+"thread-2").start();
    }

    private int num = 0;//制造线程不安全
    //线程的运行主体
    public  void printManyTimes() {
        int color = 3;
        if (Thread.currentThread().getName().equals("thread-1")) {
            color = 4;
        }

        final long start
                = Calendar
                .getInstance()
                .getTimeInMillis();
        String time;
        for (int i = 0; i < 5; i++) {
            time =
                    String.format("\033[1;32m%.3f\033[0m", (Calendar
                            .getInstance()
                            .getTimeInMillis() - start) / 1000.0
                    ) + "\t\t";
//----------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓------唯一的小改动
            synchronized (this){
                System.out.println(
                    time +
                            "\033[1;3" + color + "m" +
                            Thread
                                    .currentThread()
                                    .getName() + "->" + i + ":" + num++ +
                            "\033[0m"
                );
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println(
                        time +
                                "\033[1;31m" +
                                Thread
                                        .currentThread()
                                        .getName() +
                                " " +
                                "is interrupted? :" +
                                Thread
                                        .currentThread()
                                        .isInterrupted() +
                                "\033[0m"
                );
            }
            if (i % 10 == 0)
                Thread
                        .currentThread()
                        .interrupt();//探究interrupt()方法

        }
    }
    }
}
