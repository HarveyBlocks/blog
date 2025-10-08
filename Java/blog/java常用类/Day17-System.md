# System

- 构造方法私有
- 属性方法皆为静态

| 方法名                                                       | 说明                                                  |
| ------------------------------------------------------------ | ----------------------------------------------------- |
| public static native void arraycopy(Object src,  int  srcPos,                                     Object dest, int destPos,  int length); | 复制数组                                              |
| static long currentTimeMillis()                              | 获取当前系统时间，返回的是毫秒值                      |
| static void gc()                                             | 建议jvm赶快启动垃圾回收其回收垃圾                     |
| static void exit(int status)                                 | 退出jvm，如果参数是0，表示退出jvm，非0表示异常退出jvm |

## arraycopy()

```java
public class Main {
    public static void main(String[] args) {
        
        int[] src = {1, 2, 4, 76, 14, 15, 1, 6, 1, 5, 9, 95, 5, 4, 6, 14, 5, 8};
        int[] dest = new int[50];
        int srcPos = 5;
        int destPos = 11;
        int length = 12;
        //arraycopy(哪个数组,哪个位置开始,到目标数组,从哪个位置开始放,长度复制几个)
        System.arraycopy(src, srcPos, dest, destPos, length);
        for (int srcx:
             src) {
            System.out.print(srcx+",");
        }
        System.out.println();
        for (int destx:
             dest) {
            System.out.print(destx+",");
        }
    }
}
```

```java
1,2,4,76,14,15,1,6,1,5,9,95,5,4,6,14,5,8,
0,0,0,0,0,0,0,0,0,0,0,15,1,6,1,5,9,95,5,4,6,14,5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
```

## currentTimeMillis()

从1970.1.1.00：00.000

```java
public class Main {
    public static void main(String[] args) {
        for (int j = 0; j < 9999999; j++) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 9999999; i++) System.out.print("");
            long end = System.currentTimeMillis();
            System.out.println((end - start) / 1000.0);
        }
    }
}
```

## static void exit(int status)
