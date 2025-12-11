# 使用Stream

## 获取Stream流

```java
public interface Stream<T>
```

Stream是个接口 ,不能直接取new

### 对于Collection集合

collection提供方法

```java
default Stream<E> stream() {
    return StreamSupport.stream(spliterator(), false);
}
```

可以:

``` java
集合名.stream();
```

### 对于Map集合

```java
Set<键的类> keys = map名.keySet();
keys.stream();
//Set<String> keys = map.keySet().stream().collect(Collectors.toSet());???????


Collection<值的类> values = map名.values();
Stream<值的类> valuesStream = values.stream();//也可以向上面这么写
```

### 对于数组

1. Arrays类里提供方法

```java
public static <T> Stream<T> stream(T[] array) {
    return stream(array, 0, array.length);
}
```

​	可以:

```java
Arrays.stream(数组名);
```

2. Stream接口提供的方法

```java
public static<T> Stream<T> of(T... values) {
    return Arrays.stream(values);
}
```

可以:

```java
Stream.of(数组名);
```




### 对于多个数据

 Stream接口提供的方法:

```java
public static<T> Stream<T> of(T... values) {
    return Arrays.stream(values);
}
```

可以:

```java
Stream.of(值1,值2,值3.....);
```

## 使用Stream流方法



终结方法之一:

| 返回值类型 | 方法及形参                           | 描述 |
| ---------- | ------------------------------------ | ---- |
| void       | forEach(Consumer<? super T> action); | 遍历 |

收集Stream流之一:

| 返回值类型 | 方法及形参                                    | 描述 |
| ---------- | --------------------------------------------- | ---- |
| <R, A> R   | collect(Collector<? super T, A, R> collector) |      |


- 不算终结,因为:

```java
(集合/数组/...)表.stream().collect(Collectors.toSet()).add();
```


### 中间方法

- 中间方法指调用完成后会返回新的Stream流
- 于是支持链式编程





| 返回值类型    | 方法及形参                                      | 描述                                               |
| ------------- | ----------------------------------------------- | -------------------------------------------------- |
| Stream<T>     | filter(  Predicate<? super T>        predicate) | Predicate函数式接口,筛选                           |
| Stream<T>     | sorted()                                        | 升序排序,对对象(我不好说)无法直接排序,需要指定规则 |
| Stream<T>     | sorted(Comparator<? super T> comparator);       | Comparator函数式接口,排序,指定排序规则             |
| Stream<T>     | limit(long maxSize)                             | 获取**前**maxSize个数据                            |
| Stream<T>     | skip(long n)                                    | 跳过元素(删去前几个元素?)                          |
| Stream<T>     | distinct()                                      | 去重                                               |
| <R> Stream<R> | map(Function<? super T, ? extends R> mapper);   | 映射方法                                           |
| Stream<T>     | concat(Stream stream1,Stream stream2)           | 合并俩个流                                         |

#### 实现降序





| 返回值类型 | 方法及形参                                | 描述                                               |
| ---------- | ----------------------------------------- | -------------------------------------------------- |
| Stream<T>  | sorted()                                  | 升序排序,对对象(我不好说)无法直接排序,需要指定规则 |
| Stream<T>  | sorted(Comparator<? super T> comparator); | Comparator函数式接口,排序,指定排序规则             |


已有集合list(乱序)存Double

```java
list.stream()
        .sorted((element1,element2) -> element2 -element1)//编译时异常,引用类型Double不能减
        .forEach(element -> System.out.println(element));
```

正解:

```java
list.stream()
        .sorted((element1,element2) -> Double.compare(element2 ,element1))
        .forEach(element -> System.out.println(element));
```


#### 去重

| 返回值类型 | 方法及形参 | 描述                                      |
| ---------- | ---------- | ----------------------------------------- |
| Stream<T>  | distinct() | 去重,**重写 hashCode() 和 equals() 方法** |

已有集合list(乱序)存Double,长度大于3

```java
list.stream()
                .distinct()
                .forEach(element -> System.out.println(element));
```


#### 获取最低的数据3个



| 返回值类型 | 方法及形参          | 描述                    |
| ---------- | ------------------- | ----------------------- |
| Stream<T>  | limit(long maxSize) | 获取**前**maxSize个数据 |


已有集合list(乱序)存Double,长度大于3

```java
list.stream()
                .sorted()
                .limit(3)
                .forEach(element -> System.out.println(element));
```

#### 获取最高数据3个



| 返回值类型 | 方法及形参   | 描述                      |
| ---------- | ------------ | ------------------------- |
| Stream<T>  | skip(long n) | 跳过元素(删去前几个元素?) |


已有集合list(乱序)存Double,长度大于3

```java
list.stream()
        .sorted()
        .skip(list.size()-3)
        .forEach(element -> System.out.println(element));
```

#### 去除学生对象的不必要信息,只保留其名字(映射)


| 返回值类型    | 方法及形参                                    | 描述     |
| ------------- | --------------------------------------------- | -------- |
| <R> Stream<R> | map(Function<? super T, ? extends R> mapper); | 映射方法 |



```java
list.stream()
        .map(student -> student.getName())//??????
        .forEach(element -> System.out.println(element));

//方法引用啊二货!

list.stream()
        .map(Student::getName)//??????
        .forEach(System.out::println);
		//out是在System中声明的实例对象,所以这是一个实例方法引用

```





####  合流


| 返回值类型 | 方法及形参                            | 描述       |
| ---------- | ------------------------------------- | ---------- |
| Stream<T>  | concat(Stream stream1,Stream stream2) | 合并俩个流 |

可合并 同类 流:

```java
Stream<String> stream1 = Stream.of("a","b");
Stream<String> stream2 = Stream.of("c","b");

Stream<String> stream = Stream.concat(stream1,stream2);

stream.forEach(element->System.out.print(element +","));


//输出:
//a,b,c,b,
```

可合并 不同类 流

```java
Stream<String> stream1 = Stream.of("a","b");
Stream<Character> stream2 = Stream.of('c','b');

Stream<Object> stream = Stream.concat(stream1,stream2);

stream.forEach(element->System.out.print(element +","));


//输出:
//a,b,c,b,
```



### 终结方法:

- 不会返回Stream了

#### 获取Stream流的信息

| 返回值类型  | 方法及形参                            | 描述                                                         |
| ----------- | ------------------------------------- | ------------------------------------------------------------ |
| void        | forEach(Consumer<? super T> action);  | 遍历                                                         |
| long        | count()                               | 返回stream中的元素个数                                       |
| Optional<T> | max(Comparator<? super T> comparator) | 返回**最大**的元素的*Option(容器)?*,**一定要自定义比较规则** |
| Optional<T> | min(Comparator<? super T> comparator) | 返回**最小**的元素的*Option(容器)?*,**一定要自定义比较规**   |



##### 关于Option(容器)

```java
System.out.println(
        Stream.of("a", "b")
                .toArray()
);
```

#### 收集Stream流:

- 集合,数组才是开发中的目的
- 注意!注意1注意!
  - 流只能收集一次!
  - 在收集一次后流会关闭!!!

| 返回值类型 | 方法及形参                                    | 描述          |
| ---------- | --------------------------------------------- | ------------- |
| Object[]   | toArray()                                     | 收集成数组    |
| c[]<A> A[] | toArray(IntFunction<A[]> generator)           | 收集成A类数组 |
| <R, A> R   | collect(Collector<? super T, A, R> collector) | 收集成集合    |
#####  收集成数组

Object[]  toArray()

```java
Object[] students1 = list.stream().toArray();
```

c[]<A> A[]   toArray(IntFunction<A[]> generator)

```java
Student[] students2 = list.stream().toArray(length -> new Student[length]);
```



##### 收集成集合
```java
Stream stream =Stream.of("a", "b");

System.out.println(stream .toArray());

System.out.println(stream.collect(Collectors.toSet()));//运行时异常
```

###### Map是键值对,怎么和Steam联系呢?

- Stream -> Map

```java
Map<String,Integer> map = list.stream()
        .collect(Collectors
                .toMap(
                        student->student.getName(),student->student.getAge()
                )//----------把什么作为键------------------把什么作为值----
        );//但是会运行时异常qwq,因为这么写不能去重qwq
```

```java
Map<String,Integer> map = list.stream()
    	.distinct()//记得重写hashCode()和equals()
        .collect(Collectors
                .toMap(
                        student->student.getName(),student->student.getAge()
                )//----------把什么作为键------------------把什么作为值----
        );
```

##### 收集成键值对(分组)

```java
Map<String, List<User>> map = users
    .stream().collect(Collectors.groupingBy(User::getGender));
//依据用户的性别分组
```

## 并行流

JDK8的一种Stream, 使用多线程技术

```java
collections.stream()/*.....*/; // 使用流
collections.parallelStream()/*.....*/; // 使用并行流
```

