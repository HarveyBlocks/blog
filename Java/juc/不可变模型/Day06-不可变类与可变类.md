# 不可变

## 不可变immutable类

>   String为例

String是不可变的, 其内的方法不会改变自己的字段值, 其如`substring`是返回一个new出来的新对象

就是所有方法都被cpp中的const修饰了一样

不可变类被finnal修饰, 防止自己的字段/方法被子类继承后改变其不可变的意义

## 可变mutable类

>   SimpleDataFormat为例

sdf是可变类, 其内字段会在方法中更改, 故是线程不安全的

```java
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
for (int i = 0; i < 1000; i++) {
    new Thread(()->{
        try {
            sdf.parse("1234-12-2");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }).start();
}
```

会抛出异常

SimpleDateFormat的不可变实现是 *DateTimeFormat*

SImpleDataFormat对应的不可变类DateTimeFormatter

```java
DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
for (int i = 0; i < 1000; i++) {
    new Thread(() -> {
        TemporalAccessor parse = dtf.parse("1234-12-2");
        System.out.println(parse);
    }).start();
}
```

## BigDecimal

BigDecimal是不可变类, 为什么需要Atomic给BigDecimal保证线程安全

因为Atomic给BigDecimal保证线程安全的时候, 需求为一读一写
