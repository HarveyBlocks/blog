![image-20231014163825854](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/javaIO流/Day35-文件字符输入输出流/image-20231014163825854.png)

![image-20231014141424496](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/javaIO流/Day35-文件字符输入输出流/image-20231014141424496.png)

![image-20231014163925194](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/javaIO流/Day35-文件字符输入输出流/image-20231014163925194.png)

-   关闭流也有从缓冲区添加到硬盘里去
-   缓冲区满了?自动存到硬盘去

# 好了,现在来改造System.out.println吧!

先来看看out是啥:

```java
public final static PrintStream out = null;
```

呦呦呦

这不是PrintStream吗,

几天不见,

怎么这么拉了?

>   null->默认->控制台

```java
try(PrintStream ps = 
            new PrintStream(
                    new FileOutputStream("C:/Users/27970/Desktop/ab.txt",true
                    )
            );
){
    System.setOut(ps);
    System.out.println("你好?");
}catch (FileNotFoundException e) {
    throw new RuntimeException(e);
}
```

