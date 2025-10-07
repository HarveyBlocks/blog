# IP归属地

## 文件

[IP](ip2region.xdb)

## 导入依赖

```xml
<dependency>
    <groupId>org.lionsoul</groupId>
    <artifactId>ip2region</artifactId>
    <version>2.7.0</version>
</dependency>
```

## 查询

###完全基于文件IO

```java
// 1、创建 searcher 对象
String dbPath = "src/main/resources/ip2region.xdb";
Searcher searcher = null;
try {
    searcher = Searcher.newWithFileOnly(dbPath);
} catch (IOException e) {
    log.error("failed to create searcher with `{}`: \n", dbPath, e);
    return null;
}

// 2、查询
String region = "";
try {
    region = searcher.search(ip);
    log.debug("{region: {}, ioCount: {}}\n", region, searcher.getIOCount());
} catch (Exception e) {
    log.error("failed to search({}): \n", ip, e);
}

// 3、关闭资源
try {
    searcher.close();
} catch (IOException e) {
    log.error("failed to close searcher with `{}`: %s\n", dbPath, e);
}
// 备注：并发使用，每个线程需要创建一个独立的 searcher 对象单独使用。
return region.split("\\|");
```

### 缓存查过的地址



```java
@Slf4j
public class IpTool {
    private static Searcher searcher;
    static {
        // 1、创建 searcher 对象
        String dbPath = "src/main/resources/ip2region.xdb";
        // 1、从 dbPath 中预先加载 VectorIndex 缓存，并且把这个得到的数据作为全局变量，后续反复使用。
        byte[] vIndex = null;
        try {
            vIndex = Searcher.loadVectorIndexFromFile(dbPath);
        } catch (Exception e) {
            System.out.printf("failed to load vector index from `%s`: %s", dbPath, e);
        }

        // 2、使用全局的 vIndex 创建带 VectorIndex 缓存的查询对象。
        if(vIndex!=null){
            try {
                searcher = Searcher.newWithVectorIndex(dbPath, vIndex);
            } catch (IOException e) {
                log.error("failed to create searcher with `{}`: ", dbPath, e);
            }
        }
    }
    public static String[] map(String ip) {
        // 2、查询
        String[] region = new String[]{};
        if(searcher==null){
            return region;
        }

        try {
            region = searcher.search(ip).split("\\|");
            log.debug("{region: {}, ioCount: {}}", Arrays.toString(region), searcher.getIOCount());
        } catch (Exception e) {
            log.error("failed to search({}): ", ip, e);
        }
        return region;
        // 备注：并发使用，每个线程需要创建一个独立的 searcher 对象单独使用。
    }
    public static void close(){
        // 3、关闭资源
        try {
            searcher.close();
        } catch (IOException e) {
            log.error("failed to close searcher : %s\n", e);
        }
    }

    public static void main(String[] args) throws IOException {
        Arrays.stream(IpTool.map("210.34.59.73")).forEach(System.out::println);
        Arrays.stream(IpTool.map("127.0.0.1")).forEach(System.out::println);
        Arrays.stream(IpTool.map("localhost")).forEach(System.out::println);
    }
}
```



###缓存整个文件

```java
import org.lionsoul.ip2region.xdb.Searcher;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class SearcherTest {
    public static void main(String[] args) {
        String dbPath = "ip2region.xdb file path";

        // 1、从 dbPath 加载整个 xdb 到内存。
        byte[] cBuff;
        try {
            cBuff = Searcher.loadContentFromFile(dbPath);
        } catch (Exception e) {
            System.out.printf("failed to load content from `%s`: %s\n", dbPath, e);
            return;
        }

        // 2、使用上述的 cBuff 创建一个完全基于内存的查询对象。
        Searcher searcher;
        try {
            searcher = Searcher.newWithBuffer(cBuff);
        } catch (Exception e) {
            System.out.printf("failed to create content cached searcher: %s\n", e);
            return;
        }

        // 3、查询
        try {
            String ip = "1.2.3.4";
            long sTime = System.nanoTime();
            String region = searcher.search(ip);
            long cost = TimeUnit.NANOSECONDS.toMicros((long) (System.nanoTime() - sTime));
            System.out.printf("{region: %s, ioCount: %d, took: %d μs}\n", region, searcher.getIOCount(), cost);
        } catch (Exception e) {
            System.out.printf("failed to search(%s): %s\n", ip, e);
        }
        
        // 4、关闭资源 - 该 searcher 对象可以安全用于并发，等整个服务关闭的时候再关闭 searcher
        // searcher.close();

        // 备注：并发使用，用整个 xdb 数据缓存创建的查询对象可以安全的用于并发，也就是你可以把这个 searcher 对象做成全局对象去跨线程访问。
    }
}
```