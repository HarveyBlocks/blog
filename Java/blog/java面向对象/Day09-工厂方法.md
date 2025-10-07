# 工厂方法(factory method)

- 静态方法的一种应用
- 用于构造对象
- 相较于构造器不能更改名字(总是与类名相同),工厂方法能更改名字
- 可以一个类得到两个不同的实例(例如: 对于人类要有两个学生实例和老师实例)
- 甚至可以返回其他类型的实例

## 示例

```java
LocalDate now = LocalDate.now();
```

```java
public static LocalDate now() {
    return now(Clock.systemDefaultZone());
}
public static LocalDate now(Clock clock) {
    ...
    return LocalDate.ofEpochDay(epochDay);
}
public static LocalDate ofEpochDay(long epochDay) {
    ...
    return new LocalDate(year, month, dom);
}
```

