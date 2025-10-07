# Optional

用链式编程的方式, 减少if-else造成的代码冗长

## 需求

诸如

```java
if (user == null) {
    return;
}
Address address = user.getAddress();
if (address == null) {
    return;
}
Country country = address.getCountry();
if (country == null) {
    return;
}
String isocode = country.getIsocode();
if (isocode == null) {
    return;
}
isocode = isocode.toUpperCase();
```

## 创建

-   创建空的`Optional`

    ```java
    Optional<School> emptyOpt = Optional.empty();
    ```

-   value为null抛出` NullPointerException`

    ```java
    Optional<School> schoolOpt = Optional.of(value);
    ```

-   value为null时等价于`Optional#empty()`, 不为null时等价于`Optional#of()`

    ```java
    Optional<School> schoolOpt2 = Optional.ofNullable(value);
    ```



## 使用

### 获取对象

```java
School school = schoolOpt.get();
```

如果`schoolOpt`中存储的是null, 那么抛出异常`NoSuchElementException`

###判断是否存有实例

####isPresent

```java
Optional<School> schoolOpt = Optional.ofNullable(new School());
System.out.println(schoolOpt.isPresent());
```

####ifPresent

或用链式编程直接决定各种情况下的行为

```java
Optional<School> schoolOpt = Optional.ofNullable(null);
Consumer<School> consumer = System.out::println;
// 如果存在则执行consumer
schoolOpt.ifPresent(consumer); // 不会进入
// 如果存在则执行consumer, 否则执行runnable
schoolOpt.ifPresentOrElse(consumer, // 不会进入
        () -> {
            System.out.println("NULL"); // 执行
        });
```

```java
Optional<School> schoolOpt = Optional.ofNullable(new School());
Consumer<School> consumer = System.out::println;
schoolOpt.ifPresent(consumer); // 执行
schoolOpt.ifPresentOrElse(consumer,  // 执行
        () -> {
            System.out.println("NULL");  // 不会进入
        });
```

### orElse

####orElse

如果Optional中没有实例, 则返回other

```java
School school = schoolOpt.orElse(other);
```



```java
public void test() {
    School originValue = new School();
    testOrElse(Optional.ofNullable(originValue), originValue);
    testOrElse(Optional.ofNullable(null), null);
}

private static void testOrElse(Optional<School> schoolOpt, School originValue) {
    School defaultValue = new School();
    School school = schoolOpt.orElse(defaultValue);
    Assert.assertTrue(originValue == null ? school == defaultValue : school == originValue);
}
```

#### orElseGet

如果Optional中没有实例, 则执行supplier逻辑

```java
Supplier<School> supplier = () -> defaultValue;
School school = schoolOpt.orElseGet(supplier);
```