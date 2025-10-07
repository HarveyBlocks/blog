package RestaurantSimulation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HarveyBlocks
 * @date 2023/09/22 16:21
 **/
public class Desk {
    List<String> food = new ArrayList<>();
    public synchronized void cook() {
        try {
            if (food.size()==0) {
                food.add(
                        "food that " +
                                Thread.currentThread().getName() +
                                " cooked"
                );
                System.out.println(Thread.currentThread().getName()
                        + " has cooked food.");
                Thread.sleep((long) (1000*Math.random()));
            }else{
                System.out.println(
                        Thread.currentThread().getName() +
                                " is dispensable to cook food."
                );
            }
            this.notify();
        /*
        关于使用notify()还是notifyAll()
        1. 使用notify(),唤醒一条线程()具体哪一条有固定算法,我不知道
        2. 使用notifyAll(),唤醒所有线程,但所有线程都会去抢夺锁

        所以何不唤醒随便一条线程?反正最后能打开锁的也只有一条线程
         */

            //先唤醒,再休眠,自己休眠了,怎么唤醒别人?
            this.wait();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void eat() {
        try {
            if (food.size()==1) {
                System.out.println("\t"+Thread.currentThread().getName()
                        + " has eaten " + food.get(0)+".");
                food.remove(0);
                Thread.sleep((long) (1000*Math.random()));
            }else{
                System.out.println("\t"+
                        Thread.currentThread().getName() +
                                " can't get food.For the desk is empty."
                );
            }
            this.notify();
            this.wait();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
