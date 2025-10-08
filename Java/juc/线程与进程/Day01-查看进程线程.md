# 查看进程线程

## Windows

任务管理器

cmd窗口

```powershell
tasklist # 查看进程
```

```powershell
taskkill # 杀死进程
```

## Linux

-   查看所有进程

    ```shell
    ps -fe
    ```

-   查看PID进程的所有线程

    ```shell
    ps -fT -p <PID>
    ```

-   杀死进程

    ```shell
    kill <PID>
    ```

-   按下大写H后切换是否显示线程

    ```shell
    top
    ```

-   查看PID进程的所有线程

    ```shell
    top -H -p <PID>
    ```

    





### Java

-   查看Java进程

    ```shell
    jps
    ```

-   查看PID java进程的所有线程状态

    ```shell
    jstack <PID>
    ```

-   查看PIDjava进程中的线程的运行状况(图形化界面)

    ```shell
    jconsole
    ```

    <img src="../assets/Day01-%E6%9F%A5%E7%9C%8B%E8%BF%9B%E7%A8%8B%E7%BA%BF%E7%A8%8B/image-20240905180926383.png" alt="image-20240905180926383" style="zoom: 50%;" />

    这是Jconsole的进程信息(用jconsole看jconsole😂)
