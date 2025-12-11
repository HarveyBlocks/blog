# Date类

表示特定的瞬间，精确到毫秒。

Date类中的大部分方法已经被Calendar类的方法取代

？？？？？？？总感觉没必要？？？？？？？

## 写了一个读秒器

```java
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        Date date1 = new Date();
        Date date2 = new Date();
        int i = 0;
        while(i<60){
            if (date1.toString.equals(date2.toString)){
//注意这里的equals是经过重写的，不是比较地址.但比较的单位是毫秒，所以还是要.toString????我不知道qwq
                date1 = new Date();
            }else{
                System.out.println(date1);
                date2=new Date();
                i++;
            }

        }

    }
}
```

## 昨天的时间

```java
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        Date date1 = new Date();

        /*
        date1.getTime()
        得到一个从1970.1.1.00:00到现在的时间jiange
        单位是毫秒
        */
        Date date2 = new Date(date1.getTime()-24*60*60*1000);
        System.out.println(date1);
        System.out.println(date2);

        /*
        date1.after(date2)
        date1是在date2之后吗？
        返回一个boolean值
        相反的,date1.before(date2)
        */
        boolean flag = date1.after(date2);
        System.out.println(flag);//true
    }
}
```

## 转换表示时间的格式

转化到当地的时间表示格式（**已过时**）

```java
System.out.println(date1);
//Sat Aug 19 16:43:07 CST 2023

System.out.println(date2.toLocaleString());
//2023-8-19 16:42:33
```

