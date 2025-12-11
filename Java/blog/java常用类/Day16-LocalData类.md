# LocalDate类

## 创建对象

```java
//LocalDate localData1 = new LocalDate();//编译时异常,LocalDate是private
LocalDate now = LocalDate.now();//factory method 静态工厂代码
System.out.println(now);//2023-09-12
LocalDate.now();

LocalDate newLocalDate = LocalDate.of(1979, 12, 8);
```

## 方法

| Modifier Type | Method                          | Description                                 |
| ------------- | ------------------------------- | ------------------------------------------- |
| int           | getYear()                       | 返回年                                      |
| Month         | getMonth()                      | 返回月(SEPTEMBER之类的)                     |
| int           | getMonthValue()                 | 返回月的整型                                |
| int           | getDayOfMonth()                 | 返回日(月的)                                |
| int           | getDayOfYear()                  | 返回日(年的)                                |
| DayOfWeek     | getDayOfWeek()                  | 返回日(SATURDAY之类的)                      |
| int           | getDayOfWeek().getValue()       | 返回日的值([1,7],[Mon,Sun])                 |
| LocalDate     | minusDays((long daysToSubtract) | 返回之前(或之后,靠参数的正负决定)某天的日期 |
|               |                                 |                                             |

```java
int year = newLocalDate.getYear();
jint month = newLocalDate.getMonthValue();

int day1 = newLocalDate.getDayOfMonth();
int day2 = newLocalDate.getDayOfYear();

int[] localTime = {year,month,day1,day2};
DayOfWeek day3 = newLocalDate.getDayOfWeek();

System.out.println("{year,month,dayOfMonth,dayOfYear} = "+Arrays.toString(localTime));
//{year,month,dayOfMonth,dayOfYear} = [1979, 12, 8, 342]

System.out.println("dayOfWeek = "+day3);
//dayOfWeek = SATURDAY

int dayOfWeekValue = day3.getValue();
System.out.println(dayOfWeekValue);//6
//Mon(1)->Sun(7)

LocalDate minusDay = newLocalDate.minusDays(-1);
System.out.println(minusDay);
//2023-09-14

System.out.println(newLocalDate.minusDays(1));
//2023-09-12
```

## 修改时间

```java
LocalDate localDate = LocalDate.of(1979, 12, 8);
LocalDate newLocalDate = localDate.plusDays(100);

System.out.println(localDate);//1979-12-08
//不对原对象做更改

System.out.println(newLocalDate);//1980-03-17
//而是新产生了一个对象
```

