## UV和PV

-   `UV`
    -   `Unique Visitor`
    -   独立访客量
    -   通过互联网访问,浏览这个网站的自然人
    -   一天内, 同一个用户多次访问该网站,只记录一次
-   `PV`
    -   `Page View`
    -   页面访问量或点击量
    -   用户内访问网站的一个页面, 记录一次PV; 用户多次打开页面, 则记录多次PV
    -   往往用来衡量网站的流量

## 百万数据测试

```java
/**
 * 11.092 s
 * 2006579
 * 0.0032895
 */
@Deprecated
public void logLog(){

    long start = System.currentTimeMillis();

    String key = "logLog";
    for (int i = 0; i < 200; i++) {
        String[] uuids = new String[10000];
        for (int j = 0; j < 10000; j++) {
            uuids[j] = UUID.randomUUID().toString();
        }
        stringRedisTemplate.opsForHyperLogLog().add(key,uuids);
    }

    long end = System.currentTimeMillis();
    System.out.println((end-start)/1000.0+" s");

    Long size = stringRedisTemplate.opsForHyperLogLog().size(key);

    double times = 200.0*10000;
    System.out.println(size);
    System.out.println((size-times)/times);

    stringRedisTemplate.delete(key);
}
```



测试10次

```java
for (int i = 0; i < 10; i++) {
    System.out.println("--------"+i+"--------");
    bean.logLog();
}
```





测试解雇哦

```sql
--------0--------
11.931 s
2002788
0.001394
--------1--------
12.591 s
1992689
-0.0036555
--------2--------
12.656 s
1980759
-0.0096205
--------3--------
14.404 s
2019045
0.0095225
--------4--------
14.327 s
2014946
0.007473
--------5--------
13.48 s
2020598
0.010299
--------6--------
13.892 s
1989821
-0.0050895
--------7--------
14.45 s
2004888
0.002444
--------8--------
14.638 s
1970064
-0.014968
--------9--------
13.141 s
1995566
-0.002217
```

