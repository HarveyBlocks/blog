# Random

| 构造方法          | 解释                                        |
| ----------------- | ------------------------------------------- |
| Random()          | 创建一个新的随机数生成器。                  |
| Random(long seed) | 使用单个 long种子创建一个新的随机数生成器。 |

| 返回值类型 | 函数名             | 解释                                                         |
| ---------- | ------------------ | ------------------------------------------------------------ |
| double     | nextDouble()       | 返回下一个伪随机数， 0.0和 1.0之间的  double值              0.0分布。 |
| int        | nextInt()          | 返回下一个伪随机数，均匀分布 int值。 正负21个亿。            |
| int        | nextInt(int bound) | 返回伪随机的，均匀分布 int值,0（含）和指定值（不包括）       |

```java
package LearnCollections;

import java.util.Date;
import java.util.Random;

/**
 * @author HarveyBlocks
 * @date 2023/09/01 11:38
 **/
public class Demo01 {
    public static void main(String[] args) {
        Date date = new Date();
        long time = date.getSeconds();//自动转换
        Random random = new Random(time);
        for (int i = 0; i < 10; i++) {
            System.out.print(random.nextInt(5)+",");
        }
        //1,0,4,1,2,3,4,0,2,3,
    }
}
```

