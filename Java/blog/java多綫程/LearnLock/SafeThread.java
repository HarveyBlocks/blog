package LearnLock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author HarveyBlocks
 * @date 2023/09/22 17:04
 **/
public class SafeThread {
    public static int num = 0;
    public synchronized static void method0(){
        System.out.println(num++);
    };
    /*
    逻辑上和method0()等价,效率上method0_0()一种更高(
        why?因为
            method0()线程是在方法外排队,要多次加载方法;
            method0_0()线程是在代码块边上排队,要加载的东西少
        )
     */
    public static void method0_0(){
        //--------------------↓锁对象
        synchronized (SafeThread.class){
            System.out.println(num++);
        }
    };
    public synchronized void method1(){
        System.out.println(num++);
    };
    //逻辑上和method1()等价,效率上method1_0()一种更高
    public void method1_0(){
        synchronized (this){
            System.out.println(num++);
        }
    };
    /*解释锁对象:
    对于处于同一个对象构造的线程的锁对象应当相同
    先对锁对象有以下选择:
        1. String类,String作为锁,对所有对象都是相同的,把所有该类的对象都锁住了,不合适
        2. this,方法为非静态时使用
        3. 类名.class,方法为静态时使用
     */

    /*Lock接口
    lock()//上锁
    unlock()//解锁
     */
    public void wrongMethod(){
        Lock lock = new ReentrantLock();
        lock.lock();
        //注意:lock上锁后,如果没有unlock(),就永远不会解锁,即使产生异常了啥的也会上锁.这样,别的线程就进不来了
        {
            System.out.println(num++);
        }
        lock.unlock();
        //这种写法不合适
    }
    public void correctMethod(){
        Lock lock = new ReentrantLock();
        try {
            lock.lock();
            {
                System.out.println(num++);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
