# 可变字符串

为了避免

1. 字符串产生内存浪费
2. 每次都要产生一个新字符串而降低效率

的缺点

## StringBuffer

Buffer 缓存区，让它更快

- 可变长字符串
- JDK1.0提供
- 运行效率较StringBuilder慢
- 线程安全

**方法和StringBuilder一模一样**

## StringBuilder

- 可变长字符串
- JDK5.0提供
- 运行效率较StringBuffer快
- 线程不安全

单线程用StringBuilder

```java
StringBuilder stringBuilder = new StringBuilder();

// append()
stringBuilder.append("hi");
stringBuilder.append("hello");
System.out.println(stringBuilder);//hihello
System.out.println(stringBuilder.toString());//hihello

//length()
System.out.println(stringBuilder.length());

// insert()
stringBuilder.insert(2,",Mike!");
System.out.println(stringBuilder);//hi,Mike!hello

//replace()
stringBuilder.replace(3, 7,"Amy");
System.out.println(stringBuilder);//hi,Amy!hello

//replace()配合空字符串，还可以用来模拟删除
stringBuilder.replace(2, 6,"");
System.out.println(stringBuilder);//hi!hello

//delete()
System.out.println(stringBuilder.delete(2,3));//hihello
//看来不能直接对单独的字符进行操作qwq

//reverse(),反转
stringBuilder.reverse();
System.out.println(stringBuilder);
```

## 运用场景

用+拼接字符串,**效率低**且**浪费空间**

