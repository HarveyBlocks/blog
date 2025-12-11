# 整数缓存区

```java
public class Main {
    public static void main(String[] args) {
        Integer integer1 = new Integer(2);
        Integer integer2 = new Integer(2);
        System.out.println(integer1==integer2);//false，因为比的是地址

        Integer integer3 = 2;//自动装箱
        Integer integer4 = 2;//自动装箱
        System.out.println(integer3==integer4);//true，因为...
        /*以下是自动装箱的实质，以
        Integer integer3 = 2;
        为例：
        Integer integer3 = Integer.valueOf(2);
        */

        System.out.println(integer3==integer1);//false
    }
}
```

以下是Integer.Integer()的构造器源码

```java
/**
 * The value of the {@code Integer}.
 *
 * @serial
 */
private final int value;

/**
 * Constructs a newly allocated {@code Integer} object that
 * represents the specified {@code int} value.
 *
 * @param   value   the value to be represented by the
 *                  {@code Integer} object.
 */
public Integer(int value) {
    this.value = value;
}
```

以下是Integer.valueOf()的构造器源码

```java
public static Integer valueOf(int i) {
    if (i >= IntegerCache.low && i <= IntegerCache.high)//IntegerCache整数缓冲区
        return IntegerCache.cache[i + (-IntegerCache.low)];
    return new Integer(i);
}
```

又有IntegerCache（整数缓冲区）源码如下：

```java
    private static class IntegerCache {
        static final int low = -128;
        static final int high;//是127，继续往下看h
        static final Integer cache[];

        static {
            // high value may be configured by property
            int h = 127;//再往下看
            String integerCacheHighPropValue =
                sun.misc.VM.getSavedProperty("java.lang.Integer.IntegerCache.high");
            if (integerCacheHighPropValue != null) {
                try {
                    int i = parseInt(integerCacheHighPropValue);
                    i = Math.max(i, 127);
                    // Maximum array size is Integer.MAX_VALUE
                    h = Math.min(i, Integer.MAX_VALUE - (-low) -1);
                } catch( NumberFormatException nfe) {
                    // If the property cannot be parsed into an int, ignore it.
                }
            }
            high = h;//诺

            cache = new Integer[(high - low) + 1];//缓存区能存256个元素
            int j = low;
            for(int k = 0; k < cache.length; k++)
                cache[k] = new Integer(j++);

            // range [-128, 127] must be interned (JLS7 5.1.7)
            assert IntegerCache.high >= 127;
        }

        private IntegerCache() {}
    }
```

综合源码，我们发现自动装箱的比较还是地址

这个地址是整数缓存区的地址

我们写点代码中的integer3和integer4的地址都是整数缓存区里2的地址

故比较后地址一致，结果为true

那么，当自动装箱数不在[-127,128]

```java
public class Main {
    public static void main(String[] args) {
        Integer integer3 = 200;
        Integer integer4 = 200;
        System.out.println(integer3==integer4);
    }
}
```

valueOf()方法将执行

```java
return new Integer(i);
```

结果自然为**false**

## 作用

预先创建常用的整数包装类型对象

能够重复利用，节省内存

