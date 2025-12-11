# SimpleDateFormat

- SimpleDateFormat是一个以语言环境有关的方式来格式化和解析日期的具体类
- 格式化（日期->文本）
- 解析   （文本->日期）
- 常用的字母格式化或时间：

| 字母 | 日期或时间           | 示例 |
| ---- | -------------------- | ---- |
| y    | 年                   | 2023 |
| M    | 年中月               | 08   |
| d    | 月中日               | 19   |
| H    | 一天中的小时数[0,23] | 18   |
| m    | 分钟                 | 12   |
| s    | 秒                   | 43   |
| S    | 毫秒                 | 146  |

```java
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws ParseException{//当然，也可以Throwable等
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss.SSS");
        
        //格式化date
        Date date = new Date();
        String dateStr = sdf.format(date);
        
        //解析String
        Date date1=sdf.parse("1977/03/20-15:24:25.141");//报错ParseException
        //原因就是parse是一个带throws 异常的函数，所以如果不在上一级函数也指明抛出异常，或者对parse函数的操作不放在try catch快中，编译就会出错
        
        System.out.println(date1.toLocaleString());
    }
}
```

