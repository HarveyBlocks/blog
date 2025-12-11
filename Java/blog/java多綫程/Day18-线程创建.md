# 线程创建



## 通过继承Thread类创建

1. 自定义线程类继承**Thread**类
2. 重写**run()**方法
3. 创建线程对象,调用**start()**方法启动线程

### 代码实现

```java
//线程的执行先后循序由cpu调度执行
public class TestThread extends Thread{
    //重写一个run()
    @Override
    public void run() {
        for (int i = 0; i < 200; i++) {
            try {
                Thread.sleep(50);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            System.out.println("测试线程一"+i);
        }
    }
    public static void main(String[] args) {
        //创建线程对象
        TestThread tT1 = new TestThread();
        //调用start()方法
        tT1.start();
        /*
        tT1.run();
        按顺序先运行run()
         */

        for (int i = 0; i < 200; i++) {
            try {
                Thread.sleep(50);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            System.out.println("主线程"+i);
        }
    }

}
```



### 网图下载

1. 下载Commons IO包
2. 把包下载到项目里
3. 代码实现

```java
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.Collection;
import java.net.URL;

public class TestThread extends Thread{
    private String URL;
    private String name;
    public TestThread(String URL,String name){
        this.URL = URL;
        this.name = name;
    }

    @Override
    public void run(){
        WebDownloader webDownloader = new WebDownloader();
        webDownloader.downloader(URL,name);
        System.out.println("下载了"+name);
    }
    
    public static void main(String[] args) {
        //创建线程对象
        TestThread tT1 = new TestThread("https://","1,jpg");
        TestThread tT2 = new TestThread("https://","1,jpg");
        TestThread tT3 = new TestThread("https://","1,jpg");
        //调用start()方法
        tT1.start();
        tT2.start();
        tT3.start();

    }

}
//下载器
class WebDownloader {
    //下载方法
    public void downloader(String url, String name){
        try{
            FileUtils.copyURLToFile(new URL(url), new File(name));
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
```

## 通过实现Runnable接口创建

1. 自定义线程类继实现**Runnable**接口
2. 重写**run()**方法
3. 创建线程对象,用自定义线程类作为参数,调用**start()**方法启动线程

```java
//线程的执行先后循序由cpu调度执行
public class TestThread implements Runnable{//实现Runnable接口
    //重写一个run()
    @Override
    public void run() {
        for (int i = 0; i < 200; i++) {
            try {
                Thread.sleep(50);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            System.out.println("测试线程一"+i);
        }
    }
    public static void main(String[] args) {
        //创建线程对象
        TestThread testThread=new TestThread();
		//用自定义线程类作为参数,调用**start()**方法启动线程
        new Thread(testThread).start();


        for (int i = 0; i < 200; i++) {
            try {
                Thread.sleep(50);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            System.out.println("主线程"+i);
        }
    }

}
```



## Thread与Runnable对比

因为java是单继承,所以推荐使用Runnable接口,**方便同一个对象被多个线程使用**

```Java
TestThread testThread=new TestThread();

new Thread(testThread,"线程名1").start();
new Thread(testThread,"线程名2").start();
new Thread(testThread,"线程名3").start();
```



## 实现Callable接口创建

1. 实现Callable接口,需要返回值类型
2. 重写call方法,需要抛出异常
3. 创建目标对象
4. 创建执行服务:ExecutorService ser = Executors.newFixedThreadPool(1);
5. 提交执行:Future<Boolean> result1 = ser.submit(t1);
6. 获取结果:boolean r1 = result1.get();
7. 关闭服务:ser.shutdownNow();

```java
import java.util.concurrent.*;
public class TestCallable implements Callable{
    private int ticket = 20;
    private String name;

    @Override
    public Boolean call() {//居然是包装类?!
        while(ticket>0){
            try{
                Thread.sleep(50);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            System.out.println(name+"拿到了第"+(21-ticket--)+"张票");
        }
        return true;
    }
    public static void main(String[] args)
            throws ExecutionException,InterruptedException {//抛出异常

        TestCallable testCallable1=new TestCallable();
        testCallable1.name="t1";

        TestCallable testCallable2=new TestCallable();
        testCallable2.name="t2";

        //1. 创建执行服务:
        ExecutorService ser = Executors.newFixedThreadPool(2);//开辟两条线程
        //2. 提交执行:
        Future<Boolean> result1 = ser.submit(testCallable1);
        Future<Boolean> result2 = ser.submit(testCallable2);
        //3. 获取结果:
        boolean r1 = result1.get();
        boolean r2 = result2.get();
        //4. 关闭服务:
        ser.shutdownNow();
    }

}
```



保留问题Future<Boolean>

