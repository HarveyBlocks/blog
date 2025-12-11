# 缓冲流

![image-20231014150742916](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/javaIO流/Day35-文件字节字符输入输出缓冲(buffer)流/image-20231014150742916.png)

-   包装流的构造里套原始流

## 字节缓冲输入输出流

![image-20231014145430962](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/javaIO流/Day35-文件字节字符输入输出缓冲(buffer)流/image-20231014145430962.png)

### API

![image-20231014145637992](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/javaIO流/Day35-文件字节字符输入输出缓冲(buffer)流/image-20231014145637992.png)

```java
InputStream bufferedInputStream = 
        new BufferedInputStream(
                new FileInputStream(
                        "./src/TCP_NET/Client.java"
                ),
                1024*16//可以自己调
        )
```

## 字符缓存输入输出流

### API(功能新增!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!)

![image-20231014151109479](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/javaIO流/Day35-文件字节字符输入输出缓冲(buffer)流/image-20231014151109479.png)

![image-20231014152042510](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/javaIO流/Day35-文件字节字符输入输出缓冲(buffer)流/image-20231014152042510.png)

-   用readLine()读csa之类的啊

```java
@Test
public void writeText() {
    try (
            Reader reader = new FileReader("./src/TCP_NET/Client.java");
            BufferedReader bReader = new BufferedReader(reader,1024*16);
            //文件若不存在,就会自己创建
    ) {
        String line;
        while ((line = bReader.readLine()) != null) {
            System.out.println(line);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

