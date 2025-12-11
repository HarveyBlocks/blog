-   写什么就打印什么,嘎嘎牛逼

![image-20231014173908574](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/javaIO流/Day35-文件打印流/image-20231014173908574.png)

```java
public class PrintStream extends FilterOutputStream
    implements Appendable, Closeable
```

```java
class FilterOutputStream extends OutputStream
```

-   离谱啊啊啊啊啊啊啊啊啊啊啊啊啊啊
-   缝合怪
-   男人的梦想

![image-20231014174306643](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/javaIO流/Day35-文件打印流/image-20231014174306643.png)

```java
public class PrintWriter extends Writer
```

### PrintStream和PrintWriter的区别

-   PrinterWeiter的writer(String s)可以,PrintStream不行

### 注意

-   无敌的男人唯一的缺点:
-   PrintWriter("Path....",true)不能这么写追加数据
-   高级流都是不能直接是追加数据的
-   PrintStream同理
-   **但是**
-   PrintWriter(new FileWriter("Path.......",true))可以追加数据!!!!!
-   所以它还是无敌的

