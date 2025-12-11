# Calendar类

- Calender类的构造方法是protected，无法直接创建对象

## 常用方法

| 修饰符和返回值  | 函数名与参数                                                 | 描述                                                         |
| --------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| static Calendar | getInstance()                                                | 使用默认时区和区域设置获取日历。                             |
| static Calendar | getInstance(Locale aLocale)                                  | 使用默认时区和指定的区域设置获取日历                         |
| void            | set(int year,  int month, int date, int hourOfDay, int minute, int second) | 设置字段中的值YEAR，MONTH，DAY_OF_MONTH ，HOUR_OF_DAY，MINUTE和SECOND。 |
| int             | get(int field)                                               | 返回给定日历字段的值。                                       |
| void            | setTime(Date date)                                           | 使用给定的Date设置此日历的时间。用于实现Date和Calendar对象之间的转换。 |
| Date            | getTime()                                                    | 返回一个 Date表示此物体 Calendar的时间值                     |
| abstract void   | add(int field,  int amount)                                  | 根据日历的规则，将指定的时间量添加或减去给定的日历字段。     |
| long            | getTimeInMillis()                                            | 以毫秒为单位返回此日历的时间值。                             |

```java
import java.util.Calendar;

public class Main {
    public static void main(String[] args) {
        //创建calendar对象
        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar.toString());
        
        /*        			java.util.GregorianCalendar[time=1692436578872,areFieldsSet=true,areAllFieldsSet=true,lenient=true,zone=sun.util.calendar.ZoneInfo[id="Asia/Shanghai",offset=28800000,dstSavings=0,useDaylight=false,transitions=31,lastRule=null],firstDayOfWeek=1,minimalDaysInFirstWeek=1,ERA=1,YEAR=2023,MONTH=7,WEEK_OF_YEAR=33,WEEK_OF_MONTH=3,DAY_OF_MONTH=19,DAY_OF_YEAR=231,DAY_OF_WEEK=7,DAY_OF_WEEK_IN_MONTH=3,AM_PM=1,HOUR=5,HOUR_OF_DAY=17,MINUTE=16,SECOND=18,MILLISECOND=872,ZONE_OFFSET=28800000,DST_OFFSET=0]
        */
        //肥肠的阴间

    }
}
```

### 示例

```java
import java.util.Calendar;

public class Main {
    public static void main(String[] args) {
        //创建calendar对象
        Calendar calendar = Calendar.getInstance();

        //获取时间信息
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);//0-11!!!!!!!!!!!!!!!!!！！！！！！！！！
        int date = calendar.get(Calendar.DAY_OF_MONTH);//等价于Calendar.DATE
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);//星期天是第一天
        int hour12 = calendar.get(Calendar.HOUR);//12小时制
        int hour24 = calendar.get(Calendar.HOUR_OF_DAY);//24小时制
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        calendar.add(Calendar.DAY_OF_YEAR,1);
        calendar.set(Calendar.MONTH,8);//9月
        System.out.println(calendar.getTimeInMillis());
        System.out.println(calendar.getTime().toLocaleString());
    }
}
```

## 补充方法

    .getActualMaximum(value);
    .getActualMinimum(value);
``` java
import java.util.Calendar;

public class Main {
    public static void main(String[] args) {
        //创建calendar对象
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.MONTH,1);//二月
        int max = calendar.getActualMaximum(Calendar.DATE);
        int min = calendar.getActualMinimum(Calendar.DATE);
        System.out.println(max);//28
        System.out.println(min);//1
    }
}
```

