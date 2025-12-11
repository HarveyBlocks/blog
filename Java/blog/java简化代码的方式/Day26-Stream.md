# Stream

- Stream也叫Stream流,
- JDK8开始新增的一套API

## 作用

可以用于操作集合和数组的数据

## 优势

- 大量结合了**Lambda语法风格**来编程
- 操作集合和数组更简单 , 更强大
- 代码更简洁,可读性更好

## 使用步骤

1. 获取数据源(集合/数组)
2. 获取steam流
3. 调用各种方法对数据进行处理,计算
   - 过滤,排序,去重......
4. 获取处理的结果
   - 遍历,统计,收集到一个新集合......

## 使用举例

### 要求:

```java
List<String> list = new ArrayList<>();

Collections.addAll(list, "afs", "fsg", "faf", "adf", "ad", "afq");
//要求:char 'a' 开头 , 且3个字符的,存入到一个集合中去
```



### 经典做法:

```java
List<String> list1 = new ArrayList<>();

for (String elements:list) {
    //elements.charAt(0)=='a'
    if (elements.startsWith("a") && elements.length() == 3){
        list1.add(elements);
    }
}
```





### 用Stream流

```java
//使用stream流,支持链式编程
List<String> list2 = new ArrayList<>();
list2 = list.stream()
            .filter(s -> s.startsWith("a"))//过滤
            .filter(s -> s.length()==3)
            .collect(Collectors.toList());
```

