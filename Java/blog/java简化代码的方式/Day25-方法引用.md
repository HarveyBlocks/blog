# s方法引用

- 作用:进一步简化Lambda表达式
- 意味指向某个方法
- 标志符号 "::"

## 静态方法引用

### 语法

- 类名 :: 静态方法

### 应用场景

- 某个Lamda表达式里只是调用了一个静态方法
- 前后参数一致

### 示例

```java
package LearnLambda;

import java.util.Arrays;

public class Demo {
    public static void main(String[] args) {
        Student[] students = new Student[5];
        students[0] = new Student("A",15,90);
        students[1] = new Student("B",16,97);
        students[2] = new Student("A",13,93);
        students[3] = new Student("D",14,92);
        students[4] = new Student("C",17,96);


        /*
        Arrays.sort(students,new Comparator<Student>() {
            @Override
            public int compare(Student student1, Student student2){
                return student1.getScore() - student2.getScore();
            }
        });
        */

        Arrays.sort(students,(student1, student2)->
                student1.getScore() - student2.getScore()
        );

        Arrays.sort(students,(student1, student2)->
                CompareByData.compareByScore(student1,student2)
        );

        Arrays.sort(students,CompareByData :: compareByScore);


    }
}

class CompareByData {
    public static int compareByScore(Student student1, Student student2){

        return student1.getScore() - student2.getScore();

    }
}
```







## 实例方法引用

### 语法

- 对象名 :: 非静态方法

### 应用场景

- 某个Lamda表达式里只是调用了一个实例方法
- 前后参数一致

### 示例

```java
CompareByData compareByData = new CompareByData();

Arrays.sort(students,(student1, student2)->
        compareByData.compareByScore(student1,student2)
);

Arrays.sort(students,compareByData :: compareByScore);
```

```java
class CompareByData {
    public int compareByScore(Student student1, Student student2){

        return student1.getScore() - student2.getScore();

    }
}
```



## 特定类型方法引用


### 语法

- 类型 :: 方法

### 应用场景

- 某个Lamda表达式里只是调用了一个实例方法
- 被重写方法的第一个形参数作为方法的主调
- 后面的参数(可不存在)都是作为实例方法入参



```java
String[] strings = {"Ada","ads","dSa","asd","daA"};

Arrays.sort(strings,new Comparator<String>() {
    //自定义排序规则:忽略首字母大叫写
    @Override
    public int compare(String string1, String string2){
        return string1.compareToIgnoreCase(string2);
        //-------↑所谓主调
    }
});

Arrays.sort(strings,(string1, string2) -> string1.compareToIgnoreCase(string2));

Arrays.sort(strings,String :: compareToIgnoreCase);
```



#### 后面的参数不存在的情况

```java
list.stream()
        .map(student -> student.getName())
        .forEach(element -> System.out.println(element));



list.stream()
        .map(Student::getName)//??????
        .forEach(System.out::println);
```



## 构造器方法引用




### 语法

- 类名 :: new

### 应用场景

- 某个Lamda表达式里只是创建对象
- 前后参数一致
- 使用构造器引用

