package RestaurantSimulation;

//线程通讯-生产者消费者模型
/*
* 模拟了一个餐厅
* 厨师没人每次只能做一个food
* 顾客没人每次只能吃一个food
* 桌子上只能放一个food
* 当桌子上没food时:
*   厨师线程中的一条线程做一个food,然后唤醒顾客来吃
* 当桌子上有food时:
*   顾客吃food,然后唤醒厨师来做
* */

/**
 * @author HarveyBlocks
 * @date 2023/09/22 16:20
 **/
public class Demo {
    public static void main(String[] args) {
        Desk desk = new Desk();
        new Thread(()->{
            while (true) {
                desk.cook();
            }
        },"厨师1").start();
        new Thread(()->{
            while (true) {
                desk.cook();
            }
        },"厨师2").start();
        new Thread(()->{
            while (true) {
                desk.cook();
            }
        },"厨师3").start();
        new Thread(()->{
            while (true) {
                desk.eat();
            }
        },"顾客1").start();
        new Thread(()->{
            while (true) {
                desk.eat();
            }
        },"顾客1").start();
    }
}
