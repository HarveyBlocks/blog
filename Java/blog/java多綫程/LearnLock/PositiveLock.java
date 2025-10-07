package LearnLock;

import sun.misc.Unsafe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author HarveyBlocks
 * @date 2023/09/25 09:14
 **/
public class PositiveLock {
    //乐观锁和悲观锁
    //悲观锁一上来就加锁,没有安全感,每次有一个线程进入访问完毕后,解锁
        //性能差,造成线程的堵塞
    //乐观锁一开始不上锁,要出现线程安全问题才上锁
        //线程安全,性能好
    public static void main(String[] args) {
        MyRunnable target = new MyRunnable();
        for (int i = 0; i<100;i++){
            new Thread(target,i+"").start();
        }
        boolean flag = false;
        while (!flag) {
            flag = true;
            for (boolean check:target.getIsEnd()) {
                if(check == false) flag = false;
            }
        }
        System.out.println(flag);
        target.printList();
    }

}
class MyRunnable implements Runnable{

    private int count;
    private List<Integer> list = new ArrayList<>();
    private boolean[] isEnd;
    public boolean[] getIsEnd(){
        return isEnd;
    }
    public void initList(){
        for (int i = 0; i < 10001; i++) {
            list.add(i, 0);
        }
    }
    public void initIsEnd(){
        isEnd = new boolean[100];
        for (int i = 0; i < 100; i++) {
            isEnd[i] = false ;
        }
    }
    public void printList(){
        int i = 0;
        for (Integer number:list) {
            if (number.equals(0)) System.out.println(i);
            i++;
        }

    }

    public MyRunnable(){
        this.initList();
        this.initIsEnd();
    }
    //线程危险
    /*
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            System.out.println("count = "+(++count));
            // 我想要到一万,但是会有线程安全,要到10000
        }
    }
    */
    //悲观锁
    /*
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            synchronized (MyRunnable.class) {//在这里等价于this,因为只有一个target
                System.out.println("count = "+(++count));
            }
        }
    }
        //最终count好多次都是9999,接近一万,说明线程不安全了一次,很少,悲观锁很不划算
    */
    //乐观锁
    //CAS算法:比较和交换算法
    @Override
    public void run() {



        for (int i = 0; i < 100; i++) {

/*            int count1 ;//count,要更新的值
            int count2 = 0;//count2,新值
                                  //count1,期望
            boolean flag = true;
            while (true){
                if (count>=100*100) {
                    flag = false;
                    break ;//没意义,不需要再执行操作,就跳出
                }
                count1 = count;

                if(count!=count1){
                    continue;
                }else {
                    count2 = count1+1;
                    count = count2;
                    break;
                }
            }

            if(flag)list.set(count2,1);*/


            list.set(safeCount.incrementAndGet(),1);
            //----------------------------------------先加一,再返回值


        }
        int name = Integer.parseInt(Thread.currentThread().getName());
        isEnd[name]=true;


    }

    private AtomicInteger safeCount = new AtomicInteger();

}

