# 初识并发问题

`Thread.currentThread().getName()`得到线程名

## 问题引入:买票

```java
public class TestThread implements Runnable{
    private int ticket = 20;
    @Override
    public void run() {
        while(ticket>0){
            try{
                Thread.sleep(50);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+"拿到了第"+(21-ticket--)+"张票");
        }
    }
    public static void main(String[] args) {

        TestThread testThread=new TestThread();

        new Thread(testThread,"小明").start();
        new Thread(testThread,"小红").start();
        new Thread(testThread,"黄牛").start();
    }

}
```

![image-20230825090730601](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java多綫程/Day18-并发问题/image-20230825090730601.png)

## 阻塞

一条线程的某段逻辑需要另一条线程的执行完之后才能执行, 怎么办呢?

```java
private static final CountDownLatch WAIT_FOR_XXX = new CountDownLatch(1);
```

```java
WAIT_FOR_XXX.await();  // 当WAIT_FOR_LOGIN的值还不是0, 就会阻塞
```

```java
WAIT_FOR_XXX.countDown(); // 减一
```

