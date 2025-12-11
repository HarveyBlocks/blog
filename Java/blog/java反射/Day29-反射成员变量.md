# 反射成员变量

```java
public class Student {
    private String name;
    public int age;
    private int score;
    {...}
}
```
## 用方法获取字段

Class类里的方法:

| Modifier and Type | Method                        | Description                         |
| ----------------- | ----------------------------- | ----------------------------------- |
| Field             | getDeclaredField(String name) | 依据标识符,返回public的成员变量对象 |
| Field[]           | getDeclaredFields()           | 返回所有成员变量的数组              |
| Field             | getField(String name)         | 依据标识符,返回public的成员变量对象 |
| Field[]           | getFields()                   | 返回所有public成员变量的数组        |

```java
public static void main(String[] args)
        throws ClassNotFoundException, NoSuchFieldException {
    Class studentClass = Class.forName("LearnReflection.Student");

    System.out.println(
            studentClass.getField("age")//抛出异常
    );

    Arrays.stream(studentClass.getFields())
            .forEach(System.out::println);

    System.out.println(
            studentClass.getDeclaredField("score")//抛出异常
    );

    Arrays.stream(studentClass.getDeclaredFields())
            .forEach(System.out::println);
}
```

## 获取字段的信息

```java
public String getName() {...}
public Class<?> getType() {...}
public Object get(Object obj) throws ... {...}
public int getModifiers() {...}//获取权限修饰符,返回整形
```

```java
public static void main(String[] args)
        throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    Class studentClass = Class.forName("LearnReflection.Student");
    Field studentName = studentClass.getDeclaredField("name");//name是private的

    System.out.println(studentName.getName());//name
    System.out.println(studentName.getType());//class java.lang.String

    studentName.setAccessible(true);
    System.out.println(
            studentName.get(new Student("Mike"))
    );//Mike

}
```

## 对字段进行操作

Field类里的方法:

| Modifier and Type | Method                        | Description |
| ----------------- | ----------------------------- | ----------- |
| Object            | get(Object obj)               | 获取值      |
| void              | set(Object obj, Object value) | 赋值        |

```java
public static void main(String[] args)
        throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    Class studentClass = Class.forName("LearnReflection.Student");
    Field studentName = studentClass.getDeclaredField("name");//name是private
    Student student = new Student("Mike");

    studentName.setAccessible(true);

    studentName.set(student,"Amy");//赋值
    System.out.println(
            studentName.get(student)
    );//Amy
}
```

